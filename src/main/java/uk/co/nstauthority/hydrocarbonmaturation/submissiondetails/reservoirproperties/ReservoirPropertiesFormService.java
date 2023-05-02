package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class ReservoirPropertiesFormService implements SubmissionFormService {

  private final ReservoirPropertiesFormValidator reservoirPropertiesFormValidator;
  private final ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;
  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;

  @Autowired
  public ReservoirPropertiesFormService(ReservoirPropertiesFormValidator reservoirPropertiesFormValidator,
                                        ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService,
                                        ProbabilityDistributionFormService probabilityDistributionFormService,
                                        DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService,
                                        HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService) {
    this.reservoirPropertiesFormValidator = reservoirPropertiesFormValidator;
    this.reservoirPropertiesPersistenceService = reservoirPropertiesPersistenceService;
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.discoveryWellDetailsPersistenceService = discoveryWellDetailsPersistenceService;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult,
                                ValidationType validationType, SubmissionDetail submissionDetail) {
    if (submissionDetail.isDiscoverySubmission()) {
      discoveryWellDetailsPersistenceService
          .getDiscoveryWellDetailBySubmissionDetail(submissionDetail)
          .ifPresentOrElse(
              discoveryWellDetail ->
                  reservoirPropertiesFormValidator.validate(form, bindingResult,
                      new ReservoirPropertiesValidatorHints(
                          validationType,
                          submissionDetail,
                          discoveryWellDetail.getHydrocarbonsDiscovered())),
              () ->
                  reservoirPropertiesFormValidator.validate(form, bindingResult,
                      new ReservoirPropertiesValidatorHints(validationType, submissionDetail, null)));
      return bindingResult;
    } else if (submissionDetail.isProspectSubmission()) {
      hydrocarbonCasePersistenceService
          .getHydrocarbonCaseDetail(submissionDetail)
          .ifPresentOrElse(
              hydrocarbonCaseDetail -> {
                var mixedCase = hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCaseDetail);
                if (mixedCase) {
                  reservoirPropertiesFormValidator.validate(form, bindingResult,
                      new ReservoirPropertiesValidatorHints(
                          validationType,
                          submissionDetail,
                          HydrocarbonsDiscovered.MIXED_OIL_AND_GAS));
                } else {
                  reservoirPropertiesFormValidator.validate(form, bindingResult,
                      new ReservoirPropertiesValidatorHints(
                          validationType,
                          submissionDetail,
                          null));
                }
              },
              () ->
                  reservoirPropertiesFormValidator.validate(form, bindingResult,
                      new ReservoirPropertiesValidatorHints(
                          validationType,
                          submissionDetail,
                          null))
          );
      return bindingResult;
    }
    reservoirPropertiesFormValidator.validate(
        form, bindingResult, new ReservoirPropertiesValidatorHints(validationType, submissionDetail, null));
    return bindingResult;
  }

  ReservoirPropertiesForm getReservoirPropertiesForm(SubmissionDetail submissionDetail) {
    return reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail)
        .map(this::reservoirPropertyToEntity)
        .orElse(new ReservoirPropertiesForm());
  }

  private ReservoirPropertiesForm reservoirPropertyToEntity(ReservoirProperty reservoirProperty) {
    var form = new ReservoirPropertiesForm();

    setFormation(reservoirProperty, form);
    setSubUnit(reservoirProperty, form);
    form.setAgeStageOrSeries(reservoirProperty.getAgeStage());
    form.setGroupName(reservoirProperty.getGroupName());
    form.setLithology(reservoirProperty.getLithology());
    form.setLithologyOther(reservoirProperty.getLithologyOther());

    var submissionDetail = reservoirProperty.getSubmissionDetail();
    if (submissionDetail.isProspectSubmission()) {
      form.setAverageNetPayThickness(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT
      ));
      form.setDepthToHydrocarbonContact(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT
      ));
    }

    if (submissionDetail.isDryWellOrDiscoverySubmission()) {
      form.setReservoirConnectivity(reservoirProperty.getReservoirConnectivity());
      form.setReservoirFacies(reservoirProperty.getReservoirFacies());
      form.setAverageNetPayThickness(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DRY_WELL_DISCOVERY
      ));
    }

    if (submissionDetail.isDiscoverySubmission()) {
      var discoveryWellDetail = discoveryWellDetailsPersistenceService
          .getDiscoveryWellDetailBySubmissionDetail(submissionDetail)
          .orElse(new DiscoveryWellDetail());

      if (discoveryWellDetailsPersistenceService.isDiscoveryOilOnly(discoveryWellDetail)
          || discoveryWellDetailsPersistenceService.isDiscoveryMixed(discoveryWellDetail)) {
        form.setDepthToOilWaterContact(probabilityDistributionFormService.probabilityDistributionEntityToForm(
            submissionDetail,
            ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT
        ));
        form.setPercentOfGrvOilBearing(probabilityDistributionFormService.probabilityDistributionEntityToForm(
            submissionDetail,
            ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING
        ));
      }
      if (discoveryWellDetailsPersistenceService.isDiscoveryGasOnly(discoveryWellDetail)
          || discoveryWellDetailsPersistenceService.isDiscoveryMixed(discoveryWellDetail)) {
        form.setDepthToGasWaterContact(probabilityDistributionFormService.probabilityDistributionEntityToForm(
            submissionDetail,
            ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT
        ));
      }
      if (discoveryWellDetailsPersistenceService.isDiscoveryMixed(discoveryWellDetail)) {
        form.setDepthToGasOilContact(probabilityDistributionFormService.probabilityDistributionEntityToForm(
            submissionDetail,
            ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT
        ));
      }
    }

    form.setGrossReservoirThickness(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT_DISCOVERY
    ));

    form.setGrossRockVolume(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME
    ));

    form.setNetPorosity(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT_DISCOVERY
    ));
    form.setNetToGross(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT_DISCOVERY
    ));

    var hydrocarbonCaseDetail = hydrocarbonCasePersistenceService
        .getHydrocarbonCaseDetail(submissionDetail)
        .orElseGet(HydrocarbonCaseDetail::new);
    if (hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCaseDetail)
        || submissionDetail.isDryWellSubmission()) {
      form.setPercentOfGrvOilBearing(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING
      ));
    }

    return form;
  }

  private void setFormation(ReservoirProperty reservoirProperty, ReservoirPropertiesForm form) {
    if (reservoirProperty.getFormationName() != null) {
      form.setFormationName(reservoirProperty.getFormationName().name());
    } else if (reservoirProperty.getFormationNameManual() != null) {
      form.setFormationName(
          SearchSelectorService.getValueWithManualEntryPrefix(reservoirProperty.getFormationNameManual())
      );
    }
  }

  private void setSubUnit(ReservoirProperty reservoirProperty, ReservoirPropertiesForm form) {
    if (reservoirProperty.getSubUnit() != null) {
      form.setSubUnit(reservoirProperty.getSubUnit().name());
    } else if (reservoirProperty.getSubUnitManual() != null) {
      form.setSubUnit(
          SearchSelectorService.getValueWithManualEntryPrefix(reservoirProperty.getSubUnitManual())
      );
    }
  }
}

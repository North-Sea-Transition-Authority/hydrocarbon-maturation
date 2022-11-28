package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Service
class ReservoirPropertiesFormService implements SubmissionFormService {

  private final ReservoirPropertiesFormValidator reservoirPropertiesFormValidator;
  private final ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;
  private final ProbabilityDistributionFormService probabilityDistributionFormService;

  @Autowired
  public ReservoirPropertiesFormService(ReservoirPropertiesFormValidator reservoirPropertiesFormValidator,
                                        ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService,
                                        ProbabilityDistributionFormService probabilityDistributionFormService) {
    this.reservoirPropertiesFormValidator = reservoirPropertiesFormValidator;
    this.reservoirPropertiesPersistenceService = reservoirPropertiesPersistenceService;
    this.probabilityDistributionFormService = probabilityDistributionFormService;
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult, ValidationType validationType) {
    throw new ActionNotAllowedException("Must supply submission type to this validator");
  }

  @Override
  public BindingResult validate(SubmissionForm form, BindingResult bindingResult,
                                ValidationType validationType, SubmissionType submissionType) {
    reservoirPropertiesFormValidator.validate(form, bindingResult, validationType, submissionType);
    return bindingResult;
  }

  ReservoirPropertiesForm getReservoirPropertiesForm(SubmissionDetail submissionDetail) {
    return reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail)
        .map(this::reservoirPropertyToEntity)
        .orElse(new ReservoirPropertiesForm());
  }

  private ReservoirPropertiesForm reservoirPropertyToEntity(ReservoirProperty reservoirProperty) {
    var form = new ReservoirPropertiesForm();
    form.setFormationName(reservoirProperty.getFormationName());
    form.setAgeStageOrSeries(reservoirProperty.getAgeStage());
    form.setLithology(reservoirProperty.getLithology());

    var submissionDetail = reservoirProperty.getSubmissionDetail();
    var submissionType = submissionDetail.getSubmissionType();
    if (submissionType == SubmissionType.PROSPECT_UPDATE
        || submissionType == SubmissionType.CREATE_PROSPECT) {
      form.setAverageNetPayThickness(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT
      ));
      form.setDepthToHydrocarbonContact(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT
      ));
    }

    if (submissionType == SubmissionType.POST_DRILL_DRY_HOLE) {
      form.setReservoirConnectivity(reservoirProperty.getReservoirConnectivity());
      form.setReservoirFacies(reservoirProperty.getReservoirFacies());
      form.setAverageNetPayThickness(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          submissionDetail,
          ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DRY_WELL_DISCOVERY
      ));
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
    form.setPercentOfGrvOilBearing(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail,
        ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING
    ));

    return form;
  }
}

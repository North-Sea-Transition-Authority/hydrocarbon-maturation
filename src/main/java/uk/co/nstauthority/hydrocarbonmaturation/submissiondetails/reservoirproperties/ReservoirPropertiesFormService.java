package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
class ReservoirPropertiesFormService {

  private final ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;
  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;
  private final GeneralReservoirInformationService generalReservoirInformationService;

  @Autowired
  public ReservoirPropertiesFormService(ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService,
                                        ProbabilityDistributionFormService probabilityDistributionFormService,
                                        HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService,
                                        GeneralReservoirInformationService generalReservoirInformationService) {
    this.reservoirPropertiesPersistenceService = reservoirPropertiesPersistenceService;
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  ReservoirPropertiesForm getReservoirPropertiesForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return reservoirPropertiesPersistenceService.getReservoirProperty(reservoirSubmissionDetail)
        .map(this::reservoirPropertyEntityToForm)
        .orElse(new ReservoirPropertiesForm());
  }

  private ReservoirPropertiesForm reservoirPropertyEntityToForm(ReservoirProperty reservoirProperty) {
    var form = new ReservoirPropertiesForm();

    setFormation(reservoirProperty, form);
    setSubUnit(reservoirProperty, form);

    form.setAgeStageOrSeries(reservoirProperty.getAgeStage());
    form.setGroupName(reservoirProperty.getGroupName());
    form.setLithology(reservoirProperty.getLithology());
    form.setLithologyOther(reservoirProperty.getLithologyOther());

    var reservoirSubmissionDetail = reservoirProperty.getReservoirSubmissionDetail();
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    if (submissionDetail.isProspectSubmission()) {
      form.setAverageNetPayThickness(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT
      ));
      form.setDepthToHydrocarbonContact(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT_PROSPECT
      ));
    }

    if (submissionDetail.isDryWellOrDiscoverySubmission()) {
      form.setReservoirConnectivity(reservoirProperty.getReservoirConnectivity());
      form.setReservoirFacies(reservoirProperty.getReservoirFacies());
    }

    if (submissionDetail.isDryWellSubmission()) {
      form.setAverageNetPayThickness(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL
      ));
    }

    if (submissionDetail.isDiscoverySubmission()) {
      var hydrocarbonType =  generalReservoirInformationService
          .getReservoirHydrocarbons(reservoirSubmissionDetail)
          .orElse(new ReservoirHydrocarbons());

      form.setAverageNetPayThickness(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY
      ));

      if (generalReservoirInformationService.isOilOnly(hydrocarbonType)
          || generalReservoirInformationService.isMixed(hydrocarbonType)) {
        form.setDepthToOilWaterContact(probabilityDistributionFormService.probabilityDistributionEntityToForm(
            reservoirSubmissionDetail,
            ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_DISCOVERY
        ));
        form.setPercentOfGrvOilBearing(probabilityDistributionFormService.probabilityDistributionEntityToForm(
            reservoirSubmissionDetail,
            ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_DISCOVERY
        ));
      }
      if (generalReservoirInformationService.isGasOnly(hydrocarbonType)
          || generalReservoirInformationService.isMixed(hydrocarbonType)) {
        form.setDepthToGasWaterContact(probabilityDistributionFormService.probabilityDistributionEntityToForm(
            reservoirSubmissionDetail,
            ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_DISCOVERY
        ));
      }
      if (generalReservoirInformationService.isMixed(hydrocarbonType)) {
        form.setDepthToGasOilContact(probabilityDistributionFormService.probabilityDistributionEntityToForm(
            reservoirSubmissionDetail,
            ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_DISCOVERY
        ));
      }
    } else if (submissionDetail.isFieldSubmission()) {
      var hydrocarbonType = generalReservoirInformationService
          .getReservoirHydrocarbons(reservoirSubmissionDetail)
          .orElse(new ReservoirHydrocarbons());

      form.setAverageNetPayThickness(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_FIELD
      ));
      form.setPercentOfGrvOilBearing(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_FIELD
      ));

      if (submissionDetail.isCessationOfProductionSubmission()) {
        if (generalReservoirInformationService.isOilOnly(hydrocarbonType)
            || generalReservoirInformationService.isMixed(hydrocarbonType)) {
          form.setOriginalDepthToOilWaterContact(probabilityDistributionFormService.probabilityDistributionEntityToForm(
              reservoirSubmissionDetail,
              ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_OIL_WATER_CONTACT_FIELD
          ));
          form.setDepthToMovedOilWaterContact(probabilityDistributionFormService.probabilityDistributionEntityToForm(
              reservoirSubmissionDetail,
              ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_OIL_WATER_CONTACT_FIELD
          ));
        }
        if (generalReservoirInformationService.isGasOnly(hydrocarbonType)
            || generalReservoirInformationService.isMixed(hydrocarbonType)) {
          form.setOriginalDepthToGasWaterContact(probabilityDistributionFormService.probabilityDistributionEntityToForm(
              reservoirSubmissionDetail,
              ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_WATER_CONTACT_FIELD
          ));
          form.setDepthToMovedGasWaterContact(probabilityDistributionFormService.probabilityDistributionEntityToForm(
              reservoirSubmissionDetail,
              ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_WATER_CONTACT_FIELD
          ));
        }
        if (generalReservoirInformationService.isMixed(hydrocarbonType)) {
          form.setOriginalDepthToGasOilContact(probabilityDistributionFormService.probabilityDistributionEntityToForm(
              reservoirSubmissionDetail,
              ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_OIL_CONTACT_FIELD
          ));
          form.setDepthToMovedGasOilContact(probabilityDistributionFormService.probabilityDistributionEntityToForm(
              reservoirSubmissionDetail,
              ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_OIL_CONTACT_FIELD
          ));
        }
      } else {
        if (generalReservoirInformationService.isOilOnly(hydrocarbonType)
            || generalReservoirInformationService.isMixed(hydrocarbonType)) {
          form.setDepthToOilWaterContact(probabilityDistributionFormService.probabilityDistributionEntityToForm(
              reservoirSubmissionDetail,
              ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_FIELD
          ));
        }
        if (generalReservoirInformationService.isGasOnly(hydrocarbonType)
            || generalReservoirInformationService.isMixed(hydrocarbonType)) {
          form.setDepthToGasWaterContact(probabilityDistributionFormService.probabilityDistributionEntityToForm(
              reservoirSubmissionDetail,
              ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_FIELD
          ));
        }
        if (generalReservoirInformationService.isMixed(hydrocarbonType)) {
          form.setDepthToGasOilContact(probabilityDistributionFormService.probabilityDistributionEntityToForm(
              reservoirSubmissionDetail,
              ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_FIELD
          ));
        }
      }
    }

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      form.setGrossReservoirThickness(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT
      ));

      form.setGrossRockVolume(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT
      ));

      form.setNetPorosity(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT
      ));
      form.setNetToGross(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT
      ));
    } else if (resourceType == ResourceType.DISCOVERY) {
      form.setGrossReservoirThickness(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY
      ));

      form.setGrossRockVolume(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY
      ));

      form.setNetPorosity(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY
      ));
      form.setNetToGross(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY
      ));
    } else if (resourceType == ResourceType.FIELD) {
      form.setGrossReservoirThickness(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_FIELD
      ));

      form.setGrossRockVolume(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_FIELD
      ));

      form.setNetPorosity(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.NET_POROSITY_FIELD
      ));
      form.setNetToGross(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.NET_TO_GROSS_FIELD
      ));
    }

    var hydrocarbonCaseDetail = hydrocarbonCasePersistenceService
        .getHydrocarbonCaseDetail(submissionDetail)
        .orElseGet(HydrocarbonCaseDetail::new);
    if (hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCaseDetail)
        || submissionDetail.isDryWellSubmission()) {
      if (resourceType == ResourceType.PROSPECT) {
        form.setPercentOfGrvOilBearing(probabilityDistributionFormService.probabilityDistributionEntityToForm(
            reservoirSubmissionDetail,
            ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT
        ));
      } else if (resourceType == ResourceType.DISCOVERY) {
        form.setPercentOfGrvOilBearing(probabilityDistributionFormService.probabilityDistributionEntityToForm(
            reservoirSubmissionDetail,
            ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_DISCOVERY
        ));
      } else if (resourceType == ResourceType.FIELD) {
        form.setPercentOfGrvOilBearing(probabilityDistributionFormService.probabilityDistributionEntityToForm(
            reservoirSubmissionDetail,
            ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_FIELD
        ));
      }
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

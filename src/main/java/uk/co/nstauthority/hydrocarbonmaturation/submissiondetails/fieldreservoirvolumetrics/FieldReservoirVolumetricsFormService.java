package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class FieldReservoirVolumetricsFormService {

  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;
  private final GeneralReservoirInformationService generalReservoirInformationService;

  @Autowired
  public FieldReservoirVolumetricsFormService(
      ProbabilityDistributionFormService probabilityDistributionFormService,
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService,
      GeneralReservoirInformationService generalReservoirInformationService) {

    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  public FieldReservoirVolumetricsForm getFieldReservoirVolumetricsForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = new FieldReservoirVolumetricsForm();

    if (generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)) {
      form.setOilInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
      ));
    } else if (generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)) {
      form.setGasInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
          reservoirSubmissionDetail,
          ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD
      ));
    }
    return form;
  }

  @Transactional
  public void saveFieldReservoirVolumetrics(FieldReservoirVolumetricsForm form,
                                            ReservoirSubmissionDetail reservoirSubmissionDetail) {

    if (generalReservoirInformationService.isOilOnly(reservoirSubmissionDetail)) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD,
          form.getOilInPlace(),
          reservoirSubmissionDetail
      );
    } else if (generalReservoirInformationService.isGasOnly(reservoirSubmissionDetail)) {
      probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
          ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD,
          form.getGasInPlace(),
          reservoirSubmissionDetail
      );
    }

  }
}

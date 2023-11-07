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

    form.setOilInPlace(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD
    ));

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
    }

  }
}

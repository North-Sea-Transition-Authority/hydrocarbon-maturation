package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldgasvolumetrics;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics.GasCaseVolumetricsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class FieldGasVolumetricsService {

  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  public FieldGasVolumetricsService(ProbabilityDistributionFormService probabilityDistributionFormService,
                                    ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService) {
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
  }

  public GasCaseVolumetricsForm getGasVolumetricsForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return submissionDetailToForm(reservoirSubmissionDetail);
  }

  private GasCaseVolumetricsForm submissionDetailToForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = new GasCaseVolumetricsForm();

    form.setGasHydrocarbonSaturation(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_FIELD
    ));

    form.setGasFormationVolumeFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_FIELD
    ));

    form.setGasRecoveryFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR_FIELD
    ));

    form.setCondensateGasRatioLifeOfField(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD_CONSTANT
    ));

    return form;
  }

  @Transactional
  public void saveGasVolumetrics(GasCaseVolumetricsForm gasCaseVolumetricsForm,
                                 ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var gasHydrocarbonSaturation = ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_FIELD;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        gasHydrocarbonSaturation,
        gasCaseVolumetricsForm.getGasHydrocarbonSaturation(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var formationVolumeFactor = ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_FIELD;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        formationVolumeFactor,
        gasCaseVolumetricsForm.getGasFormationVolumeFactor(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var gasRecoveryFactor = ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR_FIELD;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        gasRecoveryFactor,
        gasCaseVolumetricsForm.getGasRecoveryFactor(),
        submissionDetail,
        reservoirSubmissionDetail);

    var condensateGasRatioLifeOfField =
        ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD_CONSTANT;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        condensateGasRatioLifeOfField,
        gasCaseVolumetricsForm.getCondensateGasRatioLifeOfField(),
        submissionDetail,
        reservoirSubmissionDetail
    );
  }
}

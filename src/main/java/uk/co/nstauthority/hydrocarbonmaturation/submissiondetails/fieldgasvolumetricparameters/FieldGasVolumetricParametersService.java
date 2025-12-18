package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldgasvolumetricparameters;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetricparameters.GasCaseVolumetricParametersForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class FieldGasVolumetricParametersService {

  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  public FieldGasVolumetricParametersService(
      ProbabilityDistributionFormService probabilityDistributionFormService,
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService) {
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
  }

  public GasCaseVolumetricParametersForm getGasVolumetricsForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return submissionDetailToForm(reservoirSubmissionDetail);
  }

  private GasCaseVolumetricParametersForm submissionDetailToForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = new GasCaseVolumetricParametersForm();

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

    form.setGasExpansionFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR_FIELD
    ));

    form.setCondensateGasRatioLifeOfField(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD_CONSTANT
    ));

    return form;
  }

  @Transactional
  public void saveGasVolumetrics(GasCaseVolumetricParametersForm gasCaseVolumetricParametersForm,
                                 ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var gasHydrocarbonSaturation = ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_FIELD;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        gasHydrocarbonSaturation,
        gasCaseVolumetricParametersForm.getGasHydrocarbonSaturation(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var formationVolumeFactor = ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_FIELD;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        formationVolumeFactor,
        gasCaseVolumetricParametersForm.getGasFormationVolumeFactor(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var gasRecoveryFactor = ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR_FIELD;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        gasRecoveryFactor,
        gasCaseVolumetricParametersForm.getGasRecoveryFactor(),
        submissionDetail,
        reservoirSubmissionDetail);

    var gasExpansionFactor = ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR_FIELD;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        gasExpansionFactor,
        gasCaseVolumetricParametersForm.getGasExpansionFactor(),
        submissionDetail,
        reservoirSubmissionDetail);

    var condensateGasRatioLifeOfField =
        ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD_CONSTANT;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        condensateGasRatioLifeOfField,
        gasCaseVolumetricParametersForm.getCondensateGasRatioLifeOfField(),
        submissionDetail,
        reservoirSubmissionDetail
    );
  }
}

package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldoilvolumetricparameters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetricparameters.OilCaseVolumetricParametersForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class FieldOilVolumetricParametersService {

  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Autowired
  public FieldOilVolumetricParametersService(
      ProbabilityDistributionFormService probabilityDistributionFormService,
      ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService) {
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
  }

  public OilCaseVolumetricParametersForm getOilVolumetricsForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return submissionDetailToForm(reservoirSubmissionDetail);
  }

  private OilCaseVolumetricParametersForm submissionDetailToForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = new OilCaseVolumetricParametersForm();

    form.setOilHydrocarbonSaturation(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_FIELD
    ));

    form.setOilFormationVolumeFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_FIELD
    ));

    form.setOilRecoveryFactor(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_FIELD
    ));

    form.setAssociatedGasOilRatioLifeOfField(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        reservoirSubmissionDetail,
        ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD_CONSTANT
    ));

    return form;
  }

  @Transactional
  public void saveOilVolumetrics(OilCaseVolumetricParametersForm oilCaseVolumetricParametersForm,
                                 ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var oilHydrocarbonSaturation = ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_FIELD;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilHydrocarbonSaturation,
        oilCaseVolumetricParametersForm.getOilHydrocarbonSaturation(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var formationVolumeFactor = ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_FIELD;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        formationVolumeFactor,
        oilCaseVolumetricParametersForm.getOilFormationVolumeFactor(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var oilRecoveryFactor = ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_FIELD;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilRecoveryFactor,
        oilCaseVolumetricParametersForm.getOilRecoveryFactor(),
        submissionDetail,
        reservoirSubmissionDetail);

    var associatedGasOilRatioLifeOfField =
        ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD_CONSTANT;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        associatedGasOilRatioLifeOfField,
        oilCaseVolumetricParametersForm.getAssociatedGasOilRatioLifeOfField(),
        submissionDetail,
        reservoirSubmissionDetail
    );
  }
}

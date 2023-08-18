package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldoilvolumetrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics.OilCaseVolumetricsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;

@Service
public class FieldOilVolumetricsService {

  private final ProbabilityDistributionFormService probabilityDistributionFormService;
  private final ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Autowired
  public FieldOilVolumetricsService(ProbabilityDistributionFormService probabilityDistributionFormService,
                                    ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService) {
    this.probabilityDistributionFormService = probabilityDistributionFormService;
    this.probabilityDistributionPersistenceService = probabilityDistributionPersistenceService;
  }

  public OilCaseVolumetricsForm getOilVolumetricsForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return submissionDetailToForm(reservoirSubmissionDetail);
  }

  private OilCaseVolumetricsForm submissionDetailToForm(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var form = new OilCaseVolumetricsForm();

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
  public void saveOilVolumetrics(OilCaseVolumetricsForm oilCaseVolumetricsForm,
                                 ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var oilHydrocarbonSaturation = ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_FIELD;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilHydrocarbonSaturation,
        oilCaseVolumetricsForm.getOilHydrocarbonSaturation(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var formationVolumeFactor = ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_FIELD;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        formationVolumeFactor,
        oilCaseVolumetricsForm.getOilFormationVolumeFactor(),
        submissionDetail,
        reservoirSubmissionDetail
    );

    var oilRecoveryFactor = ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_FIELD;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        oilRecoveryFactor,
        oilCaseVolumetricsForm.getOilRecoveryFactor(),
        submissionDetail,
        reservoirSubmissionDetail);

    var associatedGasOilRatioLifeOfField =
        ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD_CONSTANT;
    probabilityDistributionPersistenceService.saveProbabilityDistributionWhenNotNull(
        associatedGasOilRatioLifeOfField,
        oilCaseVolumetricsForm.getAssociatedGasOilRatioLifeOfField(),
        submissionDetail,
        reservoirSubmissionDetail
    );
  }
}

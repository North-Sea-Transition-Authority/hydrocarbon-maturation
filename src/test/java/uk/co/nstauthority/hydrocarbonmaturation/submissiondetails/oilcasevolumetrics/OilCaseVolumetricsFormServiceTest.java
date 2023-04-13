package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class OilCaseVolumetricsFormServiceTest {

  @Mock
  private OilCaseVolumetricsFormValidator formValidator;

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @InjectMocks
  private OilCaseVolumetricsFormService oilCaseVolumetricsFormService;

  private final SubmissionDetail submissionDetail = new SubmissionDetail();

  @Test
  void validate_verifyMethodCall() {
    var form = new OilCaseVolumetricsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var captor = ArgumentCaptor.forClass(SubmissionFormValidatorHints.class);

    oilCaseVolumetricsFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    verify(formValidator, times(1)).validate(eq(form), eq(bindingResult), captor.capture());

    assertThat(captor.getValue())
        .extracting(SubmissionFormValidatorHints::getSubmissionDetail,
            SubmissionFormValidatorHints::getValidationType)
        .containsExactly(submissionDetail, ValidationType.FULL);
  }

  @Test
  void getOilCaseVolumetricsForm_newEntity_thenEmptyForm() {
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.FORMATION_VOLUME_FACTOR_PROSPECT_DISCOVERY))
        .thenReturn(new ProbabilityDistributionForm());

    assertThat(oilCaseVolumetricsFormService.getOilCaseVolumetricsForm(submissionDetail))
        .extracting(
            form -> form.getOilHydrocarbonSaturation().getProbabilityDistributionType(),
            form -> form.getOilRecoveryFactor().getProbabilityDistributionType(),
            form -> form.getAssociatedGasOilRatioLifeOfField().getProbabilityDistributionType(),
            form -> form.getFormationVolumeFactor().getProbabilityDistributionType()
        ).containsExactly(
            null,
            null,
            null,
            null
        );
  }

  @Test
  void getOilCaseVolumetricsForm_existingEntity_thenPopulatedForm() {
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.FORMATION_VOLUME_FACTOR_PROSPECT_DISCOVERY))
        .thenReturn(probabilityDistributionForm);

    assertThat(oilCaseVolumetricsFormService.getOilCaseVolumetricsForm(submissionDetail))
        .extracting(
            form -> form.getFormationVolumeFactor().getProbabilityDistributionType(),
            form -> form.getFormationVolumeFactor().getConstant().getConstant(),
            form -> form.getFormationVolumeFactor().getConstant().getComments(),
            form -> form.getOilRecoveryFactor().getProbabilityDistributionType(),
            form -> form.getOilRecoveryFactor().getConstant().getConstant(),
            form -> form.getOilRecoveryFactor().getConstant().getComments(),
            form -> form.getOilHydrocarbonSaturation().getProbabilityDistributionType(),
            form -> form.getOilHydrocarbonSaturation().getConstant().getConstant(),
            form -> form.getOilHydrocarbonSaturation().getConstant().getComments(),
            form -> form.getAssociatedGasOilRatioLifeOfField().getProbabilityDistributionType(),
            form -> form.getAssociatedGasOilRatioLifeOfField().getConstant().getConstant(),
            form -> form.getAssociatedGasOilRatioLifeOfField().getConstant().getComments())
        .containsExactly(
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getConstant().getConstant(),
            probabilityDistributionForm.getConstant().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getConstant().getConstant(),
            probabilityDistributionForm.getConstant().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getConstant().getConstant(),
            probabilityDistributionForm.getConstant().getComments(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getConstant().getConstant(),
            probabilityDistributionForm.getConstant().getComments()
        );
  }
}
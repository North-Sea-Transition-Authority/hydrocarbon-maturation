package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcasevolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
class MixedCaseVolumetricsFormServiceTest {

  @Mock
  private MixedCaseVolumetricsFormValidator formValidator;

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @InjectMocks
  private MixedCaseVolumetricsFormService mixedCaseVolumetricsFormService;

  private final SubmissionDetail submissionDetail = new SubmissionDetail();

  @Test
  void validate_verifyMethodCall() {
    var form = new MixedCaseVolumetricsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var captor = ArgumentCaptor.forClass(SubmissionFormValidatorHints.class);

    mixedCaseVolumetricsFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    verify(formValidator, times(1)).validate(
        eq(form), eq(bindingResult), captor.capture());

    assertThat(captor.getValue())
        .extracting(SubmissionFormValidatorHints::getSubmissionDetail,
            SubmissionFormValidatorHints::getValidationType)
        .containsExactly(submissionDetail, ValidationType.FULL);
  }

  @Test
  void getMixedCaseVolumetricsForm_newEntity_thenEmptyForm() {
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        eq(submissionDetail), any(ProbabilityDistributionFieldInstance.class)))
        .thenReturn(new ProbabilityDistributionForm());

    assertThat(mixedCaseVolumetricsFormService.getMixedCaseVolumetricsForm(submissionDetail))
        .extracting(
            form -> form.getOilRecoveryFactor().getProbabilityDistributionType(),
            form -> form.getGasRecoveryFactor().getProbabilityDistributionType(),
            form -> form.getGasExpansionFactor().getProbabilityDistributionType(),
            form -> form.getGasHydrocarbonSaturation().getProbabilityDistributionType(),
            form -> form.getCondensateGasRatioLifeOfField().getProbabilityDistributionType(),
            form -> form.getAssociatedGasOilRatioLifeOfField().getProbabilityDistributionType(),
            form -> form.getOilHydrocarbonSaturation().getProbabilityDistributionType(),
            form -> form.getFormationVolumeFactor().getProbabilityDistributionType()
        )
        .containsExactly(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
  }

  @Test
  void getMixedCaseVolumetricsForm_existingEntity_thenPopulatedForm() {
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        eq(submissionDetail), any(ProbabilityDistributionFieldInstance.class)))
        .thenReturn(probabilityDistributionForm);

    assertThat(mixedCaseVolumetricsFormService.getMixedCaseVolumetricsForm(submissionDetail))
        .extracting(
            form -> form.getOilRecoveryFactor().getProbabilityDistributionType(),
            form -> form.getOilRecoveryFactor().getConstant().getConstant(),
            form -> form.getOilRecoveryFactor().getConstant().getComments(),
            form -> form.getGasRecoveryFactor().getProbabilityDistributionType(),
            form -> form.getGasRecoveryFactor().getConstant().getConstant(),
            form -> form.getGasRecoveryFactor().getConstant().getComments(),
            form -> form.getGasExpansionFactor().getProbabilityDistributionType(),
            form -> form.getGasRecoveryFactor().getConstant().getConstant(),
            form -> form.getOilRecoveryFactor().getConstant().getComments(),
            form -> form.getGasHydrocarbonSaturation().getProbabilityDistributionType(),
            form -> form.getGasHydrocarbonSaturation().getConstant().getConstant(),
            form -> form.getGasHydrocarbonSaturation().getConstant().getComments(),
            form -> form.getCondensateGasRatioLifeOfField().getProbabilityDistributionType(),
            form -> form.getCondensateGasRatioLifeOfField().getConstant().getConstant(),
            form -> form.getCondensateGasRatioLifeOfField().getConstant().getComments(),
            form -> form.getAssociatedGasOilRatioLifeOfField().getProbabilityDistributionType(),
            form -> form.getAssociatedGasOilRatioLifeOfField().getConstant().getConstant(),
            form -> form.getAssociatedGasOilRatioLifeOfField().getConstant().getComments(),
            form -> form.getOilHydrocarbonSaturation().getProbabilityDistributionType(),
            form -> form.getOilHydrocarbonSaturation().getConstant().getConstant(),
            form -> form.getOilHydrocarbonSaturation().getConstant().getComments(),
            form -> form.getFormationVolumeFactor().getProbabilityDistributionType(),
            form -> form.getFormationVolumeFactor().getConstant().getConstant(),
            form -> form.getFormationVolumeFactor().getConstant().getComments())
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
            probabilityDistributionForm.getConstant().getComments(),
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
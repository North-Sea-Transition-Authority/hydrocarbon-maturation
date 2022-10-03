package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascasevolumetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFormService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class GasCaseVolumetricsFormServiceTest {

  @Mock
  private GasCaseVolumetricsFormValidator formValidator;

  @Mock
  private ProbabilityDistributionFormService probabilityDistributionFormService;

  @InjectMocks
  private GasCaseVolumetricsFormService gasCaseVolumetricsFormService;

  private final SubmissionDetail submissionDetail = new SubmissionDetail();

  @Test
  void validate_verifyMethodCall() {
    var form = new GasCaseVolumetricsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    gasCaseVolumetricsFormService.validate(form, bindingResult, ValidationType.FULL);
    verify(formValidator, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  void getGasCaseVolumetricsForm_newEntity_thenEmptyForm() {
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR))
        .thenReturn(new ProbabilityDistributionForm());
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD))
        .thenReturn(new ProbabilityDistributionForm());

    assertThat(gasCaseVolumetricsFormService.getGasCaseVolumetricsForm(submissionDetail))
        .extracting(
            form -> form.getGasHydrocarbonSaturation().getProbabilityDistributionType(),
            form -> form.getGasRecoveryFactor().getProbabilityDistributionType(),
            form -> form.getGasExpansionFactor().getProbabilityDistributionType(),
            form -> form.getCondensateGasRatioLifeOfField().getProbabilityDistributionType()
        )
        .containsExactly(
            null,
            null,
            null,
            null
        );
  }

  @Test
  void getGasCaseVolumetricsForm_existingEntity_thenPopulatedForm() {
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm();

    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR))
        .thenReturn(probabilityDistributionForm);
    when(probabilityDistributionFormService.probabilityDistributionEntityToForm(
        submissionDetail, ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD))
        .thenReturn(probabilityDistributionForm);

    assertThat(gasCaseVolumetricsFormService.getGasCaseVolumetricsForm(submissionDetail))
        .extracting(
            form -> form.getCondensateGasRatioLifeOfField().getProbabilityDistributionType(),
            form -> form.getCondensateGasRatioLifeOfField().getConstant().getConstant(),
            form -> form.getGasExpansionFactor().getProbabilityDistributionType(),
            form -> form.getGasExpansionFactor().getConstant().getConstant(),
            form -> form.getGasRecoveryFactor().getProbabilityDistributionType(),
            form -> form.getGasRecoveryFactor().getConstant().getConstant(),
            form -> form.getGasHydrocarbonSaturation().getProbabilityDistributionType(),
            form -> form.getGasHydrocarbonSaturation().getConstant().getConstant()
            )
        .containsExactly(
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getConstant().getConstant(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getConstant().getConstant(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getConstant().getConstant(),
            probabilityDistributionForm.getProbabilityDistributionType(),
            probabilityDistributionForm.getConstant().getConstant()
        );
  }
}
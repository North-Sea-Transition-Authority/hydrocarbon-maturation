package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcasevolumetrics;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
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

    oilCaseVolumetricsFormService.validate(form, bindingResult, ValidationType.FULL);
    verify(formValidator, times(1)).validate(form, bindingResult, ValidationType.FULL);
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
        submissionDetail, ProbabilityDistributionFieldInstance.FORMATION_VOLUME_FACTOR))
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
        submissionDetail, ProbabilityDistributionFieldInstance.FORMATION_VOLUME_FACTOR))
        .thenReturn(probabilityDistributionForm);

    Assertions.assertThat(oilCaseVolumetricsFormService.getOilCaseVolumetricsForm(submissionDetail))
        .extracting(
            form -> form.getFormationVolumeFactor().getProbabilityDistributionType(),
            form -> form.getFormationVolumeFactor().getConstant().getConstant(),
            form -> form.getOilRecoveryFactor().getProbabilityDistributionType(),
            form -> form.getOilRecoveryFactor().getConstant().getConstant(),
            form -> form.getOilHydrocarbonSaturation().getProbabilityDistributionType(),
            form -> form.getOilHydrocarbonSaturation().getConstant().getConstant(),
            form -> form.getAssociatedGasOilRatioLifeOfField().getProbabilityDistributionType(),
            form -> form.getAssociatedGasOilRatioLifeOfField().getConstant().getConstant())
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
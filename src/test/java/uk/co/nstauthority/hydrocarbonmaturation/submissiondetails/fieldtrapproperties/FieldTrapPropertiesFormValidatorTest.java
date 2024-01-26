package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldtrapproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapType;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class FieldTrapPropertiesFormValidatorTest {

  private FieldTrapPropertiesFormValidator fieldTrapPropertiesFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    fieldTrapPropertiesFormValidator = new FieldTrapPropertiesFormValidator();
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @ParameterizedTest
  @EnumSource(value = ValidationType.class)
  void validate_whenValidForm_noErrors(ValidationType validationType) {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    assertThat(fieldTrapPropertiesFormValidator.isValid(form, bindingResult, submissionDetail, validationType))
        .isTrue();

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validatePartial_whenInvalidForm_errors() {
    var form = getInvalidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    assertThat(fieldTrapPropertiesFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL))
        .isFalse();

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("depthToCrest." + ProbabilityDistributionType.CONSTANT.getPath() + ".error",
                Set.of("depthToCrest." + ProbabilityDistributionType.CONSTANT.getPath() + ".error.error")),
            entry("depthToSpillPoint." + ProbabilityDistributionType.CONSTANT.getPath() + ".error",
                Set.of("depthToSpillPoint." + ProbabilityDistributionType.CONSTANT.getPath() + ".error.error")),
            entry("productiveArea." + ProbabilityDistributionType.CONSTANT.getPath() + ".error",
                Set.of("productiveArea." + ProbabilityDistributionType.CONSTANT.getPath() + ".error.error")),

            entry("verticallySeparateHcPools.decimalInputValue",
                Set.of("verticallySeparateHcPools.decimalInputValue.notNumeric")),
            entry("structuralCompartments.decimalInputValue",
                Set.of("structuralCompartments.decimalInputValue.notNumeric")),
            entry("originalHydrocarbonColumnHeight.decimalInputValue",
                Set.of("originalHydrocarbonColumnHeight.decimalInputValue.notNumeric"))
        ).doesNotContainKeys(
            "otherTrapType",
            "closureMechanisms"
        );
  }

  @Test
  void validateFull_whenInvalidForm_errors() {
    var form = getInvalidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    assertThat(fieldTrapPropertiesFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL))
        .isFalse();

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("depthToCrest." + ProbabilityDistributionType.CONSTANT.getPath() + ".error",
                Set.of("depthToCrest." + ProbabilityDistributionType.CONSTANT.getPath() + ".error.error")),
            entry("depthToSpillPoint." + ProbabilityDistributionType.CONSTANT.getPath() + ".error",
                Set.of("depthToSpillPoint." + ProbabilityDistributionType.CONSTANT.getPath() + ".error.error")),
            entry("productiveArea." + ProbabilityDistributionType.CONSTANT.getPath() + ".error",
                Set.of("productiveArea." + ProbabilityDistributionType.CONSTANT.getPath() + ".error.error")),

            entry("otherTrapType", Set.of("otherTrapType.required")),
            entry("closureMechanisms", Set.of("closureMechanisms.required")),

            entry("verticallySeparateHcPools.decimalInputValue",
                Set.of("verticallySeparateHcPools.decimalInputValue.notNumeric")),
            entry("structuralCompartments.decimalInputValue",
                Set.of("structuralCompartments.decimalInputValue.notNumeric")),
            entry("originalHydrocarbonColumnHeight.decimalInputValue",
                Set.of("originalHydrocarbonColumnHeight.decimalInputValue.notNumeric"))
        );
  }

  @Test
  void validatePartial_whenEmptyForm_noErrors() {
    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    assertThat(fieldTrapPropertiesFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL))
        .isTrue();

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validateFull_whenEmptyForm_errors() {
    var form = getEmptyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    assertThat(fieldTrapPropertiesFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL))
        .isFalse();

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("trapType", Set.of("trapType.required")),
            entry("centreOfInterestQuadrantBlock", Set.of("centreOfInterestQuadrantBlock.required")),
            entry("closureMechanisms", Set.of("closureMechanisms.required")),

            entry("verticallySeparateHcPools.decimalInputValue",
                Set.of("verticallySeparateHcPools.decimalInputValue.required")),
            entry("structuralCompartments.decimalInputValue",
                Set.of("structuralCompartments.decimalInputValue.required")),
            entry("originalHydrocarbonColumnHeight.decimalInputValue",
                Set.of("originalHydrocarbonColumnHeight.decimalInputValue.required"))
        );
  }

  private FieldTrapPropertiesForm getValidForm() {
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setConstant(DecimalInput.createDecimalInput(1));

    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CONSTANT);
    probabilityDistributionForm.setProbabilityOfExceedanceForm(probabilityOfExceedanceForm);

    var form = new FieldTrapPropertiesForm();
    form.setDepthToCrest(probabilityDistributionForm);
    form.setDepthToSpillPoint(probabilityDistributionForm);
    form.setProductiveArea(probabilityDistributionForm);

    form.setTrapType(TrapType.COMBINATION);
    form.setCentreOfInterestQuadrantBlock("1/1");
    form.setClosureMechanisms("mechanisms");
    form.setVerticallySeparateHcPools(DecimalInput.createDecimalInput(1));
    form.setStructuralCompartments(DecimalInput.createDecimalInput(2));
    form.setOriginalHydrocarbonColumnHeight(DecimalInput.createDecimalInput(3));

    return form;
  }

  private FieldTrapPropertiesForm getInvalidForm() {
    var invalidDecimalInput = new DecimalInput("a");

    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();
    probabilityOfExceedanceForm.setConstant(invalidDecimalInput);

    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CONSTANT);
    probabilityDistributionForm.setProbabilityOfExceedanceForm(probabilityOfExceedanceForm);

    var form = new FieldTrapPropertiesForm();
    form.setTrapType(TrapType.OTHER);
    form.setOtherTrapType(null);
    form.setDepthToCrest(probabilityDistributionForm);
    form.setDepthToSpillPoint(probabilityDistributionForm);
    form.setProductiveArea(probabilityDistributionForm);

    form.setClosureMechanisms("");
    form.setVerticallySeparateHcPools(invalidDecimalInput);
    form.setStructuralCompartments(invalidDecimalInput);
    form.setOriginalHydrocarbonColumnHeight(invalidDecimalInput);

    return form;
  }

  private FieldTrapPropertiesForm getEmptyForm() {
    var probabilityOfExceedanceForm = new ProbabilityOfExceedanceForm();

    var probabilityDistributionForm = new ProbabilityDistributionForm();
    probabilityDistributionForm.setProbabilityDistributionType(ProbabilityDistributionType.CONSTANT);
    probabilityDistributionForm.setProbabilityOfExceedanceForm(probabilityOfExceedanceForm);

    var form = new FieldTrapPropertiesForm();
    form.setDepthToCrest(probabilityDistributionForm);
    form.setDepthToSpillPoint(probabilityDistributionForm);
    form.setProductiveArea(probabilityDistributionForm);

    form.setVerticallySeparateHcPools(new DecimalInput());
    form.setStructuralCompartments(new DecimalInput());
    form.setOriginalHydrocarbonColumnHeight(new DecimalInput());

    return form;
  }


}
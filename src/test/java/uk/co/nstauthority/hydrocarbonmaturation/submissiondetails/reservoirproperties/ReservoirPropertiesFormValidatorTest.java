package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceForm;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class ReservoirPropertiesFormValidatorTest {

  private ReservoirPropertiesFormValidator reservoirPropertiesFormValidator;

  @BeforeEach
  void setup() {
    reservoirPropertiesFormValidator = new ReservoirPropertiesFormValidator();
  }

  @Test
  void supports_whenSourceAndSealFormClass_thenTrue() {
    var supportedClass = ReservoirPropertiesForm.class;
    assertTrue(reservoirPropertiesFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNonSupportedClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(reservoirPropertiesFormValidator.supports(nonSupportedClass));
  }


  @Test
  void validate_fullValidation_whenValidForm_thenNoValidationErrors() {
    var form = getValidForm();

    var bindingResult = validateReservoirProperties(form, ValidationType.FULL);

    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      names = {"CUSTOM"},
      mode = EnumSource.Mode.EXCLUDE
  )
  void validate_whenEmptyForm_thenValidationErrors(ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithEmptyDistributions(probabilityDistributionType);
    form.setLithology(null);
    form.setFormationName(null);
    form.setAgeStageOrSeries(null);
    var bindingResult = validateReservoirProperties(form, ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("formationName", Set.of("formationName.required")),
            entry("ageStageOrSeries", Set.of("ageStageOrSeries.required")),
            entry("lithology", Set.of("lithology.required")),
            entry("averageNetPayThickness." + probabilityDistributionType.getPath() + ".error",
                Set.of("averageNetPayThickness." + probabilityDistributionType.getPath() + ".error.error")),
            entry("grossReservoirThickness." + probabilityDistributionType.getPath() + ".error",
                Set.of("grossReservoirThickness." + probabilityDistributionType.getPath() + ".error.error")),
            entry("grossRockVolume." + probabilityDistributionType.getPath() + ".error",
                Set.of("grossRockVolume." + probabilityDistributionType.getPath() + ".error.error")),
            entry("percentOfGrvOilBearing." + probabilityDistributionType.getPath() + ".error",
                Set.of("percentOfGrvOilBearing." + probabilityDistributionType.getPath() + ".error.error")),
            entry("netToGross." + probabilityDistributionType.getPath() + ".error",
                Set.of("netToGross." + probabilityDistributionType.getPath() + ".error.error")),
            entry("netPorosity." + probabilityDistributionType.getPath() + ".error",
                Set.of("netPorosity." + probabilityDistributionType.getPath() + ".error.error")),
            entry("depthToHydrocarbonContact." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToHydrocarbonContact." + probabilityDistributionType.getPath() + ".error.error"))
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      names = {"CUSTOM"},
      mode = EnumSource.Mode.EXCLUDE
  )
  void validate_whenInvalidExceedenceForm_thenValidationErrors(
      ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithInvalidDistributions(probabilityDistributionType);

    var bindingResult = validateReservoirProperties(form, ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("averageNetPayThickness." + probabilityDistributionType.getPath() + ".error",
                Set.of("averageNetPayThickness." + probabilityDistributionType.getPath() + ".error.error")),
            entry("grossReservoirThickness." + probabilityDistributionType.getPath() + ".error",
                Set.of("grossReservoirThickness." + probabilityDistributionType.getPath() + ".error.error")),
            entry("grossRockVolume." + probabilityDistributionType.getPath() + ".error",
                Set.of("grossRockVolume." + probabilityDistributionType.getPath() + ".error.error")),
            entry("percentOfGrvOilBearing." + probabilityDistributionType.getPath() + ".error",
                Set.of("percentOfGrvOilBearing." + probabilityDistributionType.getPath() + ".error.error")),
            entry("netToGross." + probabilityDistributionType.getPath() + ".error",
                Set.of("netToGross." + probabilityDistributionType.getPath() + ".error.error")),
            entry("netPorosity." + probabilityDistributionType.getPath() + ".error",
                Set.of("netPorosity." + probabilityDistributionType.getPath() + ".error.error")),
            entry("depthToHydrocarbonContact." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToHydrocarbonContact." + probabilityDistributionType.getPath() + ".error.error"))
        );
  }

  @Test
  void partialValidation_withValidForm() {
    var form = new ReservoirPropertiesForm();
    var bindingResult = validateReservoirProperties(form, ValidationType.PARTIAL);

    assertFalse(bindingResult.hasErrors());
  }

  private BindingResult validateReservoirProperties(ReservoirPropertiesForm form, ValidationType validationType) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    reservoirPropertiesFormValidator.validate(form, bindingResult, validationType);
    return bindingResult;
  }

  private ReservoirPropertiesForm getValidForm() {
    var form = new ReservoirPropertiesForm();
    form.setFormationName("source formation name");
    form.setAgeStageOrSeries("age stage");
    form.setLithology("lithology");
    form.setAverageNetPayThickness(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
    form.setGrossReservoirThickness(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
    form.setDepthToHydrocarbonContact(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
    form.setGrossRockVolume(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
    form.setNetPorosity(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
    form.setNetToGross(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
    form.setPercentOfGrvOilBearing(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());

    return form;
  }

  private ReservoirPropertiesForm getFormWithEmptyDistributions(
      ProbabilityDistributionType probabilityDistributionType) {
    var form = getValidForm();
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            probabilityDistributionType,
            new ProbabilityOfExceedanceForm());
    form.setAverageNetPayThickness(probabilityDistributionForm);
    form.setGrossReservoirThickness(probabilityDistributionForm);
    form.setDepthToHydrocarbonContact(probabilityDistributionForm);
    form.setGrossRockVolume(probabilityDistributionForm);
    form.setNetPorosity(probabilityDistributionForm);
    form.setNetToGross(probabilityDistributionForm);
    form.setPercentOfGrvOilBearing(probabilityDistributionForm);

    return form;
  }

  private ReservoirPropertiesForm getFormWithInvalidDistributions(
      ProbabilityDistributionType probabilityDistributionType) {
    var form = getValidForm();
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            probabilityDistributionType,
            ProbabilityDistributionTestUtil.getInvalidProbabilityOfExceedanceForm());
    form.setAverageNetPayThickness(probabilityDistributionForm);
    form.setGrossReservoirThickness(probabilityDistributionForm);
    form.setDepthToHydrocarbonContact(probabilityDistributionForm);
    form.setGrossRockVolume(probabilityDistributionForm);
    form.setNetPorosity(probabilityDistributionForm);
    form.setNetToGross(probabilityDistributionForm);
    form.setPercentOfGrvOilBearing(probabilityDistributionForm);

    return form;
  }
}
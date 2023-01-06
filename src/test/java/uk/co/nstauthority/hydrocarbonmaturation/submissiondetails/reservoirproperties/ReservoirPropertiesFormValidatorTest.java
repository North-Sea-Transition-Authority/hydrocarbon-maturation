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
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
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

    var bindingResult = validateReservoirProperties(form, ValidationType.FULL, SubmissionType.CREATE_PROSPECT);

    assertFalse(bindingResult.hasErrors());
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"CUSTOM", "NOT_CALCULATED"}
  )
  void validate_whenEmptyForm_prospectSubmission_thenValidationErrors(ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithEmptyDistributions(probabilityDistributionType);
    form.setLithology(null);
    form.setFormationName(null);
    form.setAgeStageOrSeries(null);
    var bindingResult = validateReservoirProperties(form, ValidationType.FULL, SubmissionType.CREATE_PROSPECT);
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
                Set.of("depthToHydrocarbonContact." + probabilityDistributionType.getPath() + ".error.error")),
            entry("averageNetPayThickness." + probabilityDistributionType.getPath() + ".error",
                Set.of("averageNetPayThickness." + probabilityDistributionType.getPath() + ".error.error")),
            entry("percentOfGrvOilBearing." + probabilityDistributionType.getPath() + ".error",
                Set.of("percentOfGrvOilBearing." + probabilityDistributionType.getPath() + ".error.error"))
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"CUSTOM", "NOT_CALCULATED"}
  )
  void validate_whenInvalidExceedenceForm_whenProspectUpdate_thenValidationErrors(
      ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithInvalidDistributions(probabilityDistributionType);

    var bindingResult = validateReservoirProperties(form, ValidationType.FULL, SubmissionType.PROSPECT_UPDATE);
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
                Set.of("depthToHydrocarbonContact." + probabilityDistributionType.getPath() + ".error.error")),
            entry("averageNetPayThickness." + probabilityDistributionType.getPath() + ".error",
                Set.of("averageNetPayThickness." + probabilityDistributionType.getPath() + ".error.error")),
            entry("percentOfGrvOilBearing." + probabilityDistributionType.getPath() + ".error",
                Set.of("percentOfGrvOilBearing." + probabilityDistributionType.getPath() + ".error.error"))
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"CUSTOM", "NOT_CALCULATED"})
  void validate_whenInvalidExceedenceForm_whenDryHole_thenValidationErrors(
      ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithInvalidDistributions(probabilityDistributionType);

    var bindingResult = validateReservoirProperties(form, ValidationType.FULL, SubmissionType.POST_DRILL_DRY_HOLE);
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
                Set.of("netPorosity." + probabilityDistributionType.getPath() + ".error.error")))
        .doesNotContainKey("depthToHydrocarbonContact." + probabilityDistributionType.getPath() + ".error");

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Gross rock volume (GRV) distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places.",
            "Percentage of GRV which is oil bearing distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100 percent.",
            "Average net pay thickness distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places.",
            "Gross reservoir thickness distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places.",
            "Net to gross distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100 percent.",
            "Net porosity distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100 percent."
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"CUSTOM", "NOT_CALCULATED"})
  void validate_whenInvalidExceedenceForm_whenDiscovery_whenMixedHydrocarbon_fullValidation_thenValidationErrors(
      ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithInvalidDistributions(probabilityDistributionType);

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    reservoirPropertiesFormValidator.validate(form, bindingResult,
        new ReservoirPropertiesValidatorHints(
            ValidationType.FULL, SubmissionType.CREATE_DISCOVERY, HydrocarbonsDiscovered.MIXED_OIL_AND_GAS));
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
            entry("depthToOilWaterContact." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToOilWaterContact." + probabilityDistributionType.getPath() + ".error.error")),
            entry("depthToGasWaterContact." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToGasWaterContact." + probabilityDistributionType.getPath() + ".error.error")),
            entry("depthToGasOilContact." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToGasOilContact." + probabilityDistributionType.getPath() + ".error.error")))
        .doesNotContainKey("depthToHydrocarbonContact." + probabilityDistributionType.getPath() + ".error");

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Gross rock volume (GRV) distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places.",
            "Percentage of GRV which is oil bearing distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100 percent.",
            "Average net pay thickness distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places.",
            "Gross reservoir thickness distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places.",
            "Net to gross distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100 percent.",
            "Net porosity distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100 percent.",
            "Depth to oil water contact - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places.",
            "Depth to gas water contact - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places.",
            "Depth to gas oil contact - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places."
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"CUSTOM", "NOT_CALCULATED"})
  void validate_whenInvalidExceedenceForm_whenDiscovery_whenMixedHydrocarbon_partialValidation_thenValidationErrors(
      ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithInvalidDistributions(probabilityDistributionType);

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    reservoirPropertiesFormValidator.validate(form, bindingResult,
        new ReservoirPropertiesValidatorHints(
            ValidationType.PARTIAL, SubmissionType.CREATE_DISCOVERY, HydrocarbonsDiscovered.MIXED_OIL_AND_GAS));
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
            entry("depthToOilWaterContact." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToOilWaterContact." + probabilityDistributionType.getPath() + ".error.error")),
            entry("depthToGasWaterContact." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToGasWaterContact." + probabilityDistributionType.getPath() + ".error.error")),
            entry("depthToGasOilContact." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToGasOilContact." + probabilityDistributionType.getPath() + ".error.error")))
        .doesNotContainKey("depthToHydrocarbonContact." + probabilityDistributionType.getPath() + ".error");

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Gross rock volume (GRV) distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places.",
            "Percentage of GRV which is oil bearing distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100 percent.",
            "Average net pay thickness distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places.",
            "Gross reservoir thickness distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places.",
            "Net to gross distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100 percent.",
            "Net porosity distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100 percent.",
            "Depth to oil water contact - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places.",
            "Depth to gas water contact - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places.",
            "Depth to gas oil contact - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places."
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenInvalidExceedenceForm_whenDiscovery_whenGasHydrocarbon_thenValidationErrors(
      ValidationType validationType) {
    var form = getFormWithInvalidDistributions(ProbabilityDistributionType.NORMAL);

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    reservoirPropertiesFormValidator.validate(form, bindingResult,
        new ReservoirPropertiesValidatorHints(
            validationType, SubmissionType.CREATE_DISCOVERY, HydrocarbonsDiscovered.GAS_ONLY));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("averageNetPayThickness." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("averageNetPayThickness." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("grossReservoirThickness." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("grossReservoirThickness." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("grossRockVolume." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("grossRockVolume." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("percentOfGrvOilBearing." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("percentOfGrvOilBearing." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("netToGross." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("netToGross." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("netPorosity." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("netPorosity." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("depthToGasWaterContact." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("depthToGasWaterContact." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")))
        .doesNotContainKey("depthToHydrocarbonContact." + ProbabilityDistributionType.NORMAL.getPath() + ".error");

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Gross rock volume (GRV) distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places.",
            "Percentage of GRV which is oil bearing distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100 percent.",
            "Average net pay thickness distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places.",
            "Gross reservoir thickness distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places.",
            "Net to gross distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100 percent.",
            "Net porosity distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100 percent.",
            "Depth to gas water contact - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places."
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenInvalidExceedenceForm_whenDiscovery_whenOilHydrocarbon_thenValidationErrors(
      ValidationType validationType) {
    var form = getFormWithInvalidDistributions(ProbabilityDistributionType.NORMAL);

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    reservoirPropertiesFormValidator.validate(form, bindingResult,
        new ReservoirPropertiesValidatorHints(
            validationType, SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT, HydrocarbonsDiscovered.OIL_ONLY));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("averageNetPayThickness." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("averageNetPayThickness." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("grossReservoirThickness." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("grossReservoirThickness." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("grossRockVolume." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("grossRockVolume." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("percentOfGrvOilBearing." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("percentOfGrvOilBearing." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("netToGross." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("netToGross." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("netPorosity." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("netPorosity." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("depthToOilWaterContact." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("depthToOilWaterContact." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")))
        .doesNotContainKey("depthToHydrocarbonContact." + ProbabilityDistributionType.NORMAL.getPath() + ".error");

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Gross rock volume (GRV) distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places.",
            "Percentage of GRV which is oil bearing distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100 percent.",
            "Average net pay thickness distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places.",
            "Gross reservoir thickness distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places.",
            "Net to gross distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100 percent.",
            "Net porosity distribution used - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100 percent.",
            "Depth to oil water contact - all mandatory probability of exceedence inputs must be provided. All inputs must be numbers with up to 2 decimal places."
        );
  }

  @Test
  void partialValidation_whenProspectSubmission_withValidForm() {
    var form = new ReservoirPropertiesForm();
    var bindingResult = validateReservoirProperties(form, ValidationType.PARTIAL, SubmissionType.CREATE_PROSPECT);

    assertFalse(bindingResult.hasErrors());
  }

  private BindingResult validateReservoirProperties(ReservoirPropertiesForm form, ValidationType validationType, SubmissionType submissionType) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    reservoirPropertiesFormValidator.validate(form, bindingResult, new ReservoirPropertiesValidatorHints(validationType, submissionType, null));
    return bindingResult;
  }

  private ReservoirPropertiesForm getValidForm() {
    var form = new ReservoirPropertiesForm();
    form.setFormationName("source formation name");
    form.setAgeStageOrSeries("age stage");
    form.setLithology("lithology");
    form.setReservoirConnectivity("connectivity");
    form.setReservoirFacies("facies");
    form.setAverageNetPayThickness(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
    form.setGrossReservoirThickness(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
    form.setDepthToHydrocarbonContact(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
    form.setGrossRockVolume(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
    form.setNetPorosity(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
    form.setNetToGross(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
    form.setPercentOfGrvOilBearing(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
    form.setDepthToOilWaterContact(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setDepthToGasWaterContact(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
    form.setDepthToGasOilContact(ProbabilityDistributionTestUtil.getValidCustomProbabilityDistributionForm());
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
    form.setDepthToOilWaterContact(probabilityDistributionForm);
    form.setDepthToGasWaterContact(probabilityDistributionForm);
    form.setDepthToGasOilContact(probabilityDistributionForm);
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
    form.setDepthToOilWaterContact(probabilityDistributionForm);
    form.setDepthToGasWaterContact(probabilityDistributionForm);
    form.setDepthToGasOilContact(probabilityDistributionForm);
    return form;
  }
}
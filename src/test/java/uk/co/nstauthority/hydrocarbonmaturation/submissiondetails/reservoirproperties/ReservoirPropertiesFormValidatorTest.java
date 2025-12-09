package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StandardLithology;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.GroupName;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class ReservoirPropertiesFormValidatorTest {

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @InjectMocks
  private ReservoirPropertiesFormValidator reservoirPropertiesFormValidator;

  private SubmissionDetail submissionDetail;
  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setup() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
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
    form.setGroupName(null);
    var bindingResult = validateReservoirProperties(form, ValidationType.FULL, SubmissionType.CREATE_PROSPECT);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("formationName", Set.of("formationName.required")),
            entry("ageStageOrSeries", Set.of("ageStageOrSeries.required")),
            entry("groupName", Set.of("groupName.required")),
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

  @Test
  void validate_otherLithologyNotProvided_thenValidationErrors() {
    var form = new ReservoirPropertiesForm();
    form.setLithology(StandardLithology.OTHER);
    form.setLithologyOther(null);
    var bindingResult = validateReservoirProperties(form, ValidationType.FULL, SubmissionType.CREATE_PROSPECT);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("lithologyOther", Set.of("lithologyOther.required"))
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
            entry("averageNetReservoirThickness." + probabilityDistributionType.getPath() + ".error",
                Set.of("averageNetReservoirThickness." + probabilityDistributionType.getPath() + ".error.error")),
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
        .doesNotContainKey("depthToHydrocarbonContact." + probabilityDistributionType.getPath() + ".error")
        .doesNotContainKey("averageNetPayThickness." + probabilityDistributionType.getPath() + ".error");

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Gross rock volume (GRV) - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Percentage of GRV which is oil bearing - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Average net reservoir thickness - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Gross reservoir thickness - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Net to gross - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Net porosity - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%"
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
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);
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
            entry("depthToGasOilContact." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToGasOilContact." + probabilityDistributionType.getPath() + ".error.error")))
        .doesNotContainKey("depthToHydrocarbonContact." + probabilityDistributionType.getPath() + ".error");

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Gross rock volume (GRV) - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Percentage of GRV which is oil bearing - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Average net pay thickness - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Gross reservoir thickness - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Net to gross - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Net porosity - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Depth to oil-water contact - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Depth to gas-oil contact - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places"
        );
  }

  @Test
  void validate_whenInvalidExceedenceForm_whenDiscovery_whenNoDepthToContactsDistributionType_fullValidation_thenValidationErrors() {
    var form = new ReservoirPropertiesForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("depthToOilWaterContact.probabilityDistributionType" ,
                Set.of("depthToOilWaterContact.probabilityDistributionType.required")),
            entry("depthToGasOilContact.probabilityDistributionType" ,
                Set.of("depthToGasOilContact.probabilityDistributionType.required")));

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Select the distribution used for depth to oil-water contact",
            "Select the distribution used for depth to gas-oil contact"
        );
  }

  @Test
  void validate_whenInvalidExceedenceForm_whenDiscovery_whenNoDepthToContactsDistributionType_partialValidation_thenNoValidationErrors() {
    var form = new ReservoirPropertiesForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.PARTIAL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .doesNotContainKeys(
            "depthToOilWaterContact.probabilityDistributionType",
            "depthToGasOilContact.probabilityDistributionType"
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"CUSTOM", "NOT_CALCULATED"})
  void validate_whenInvalidExceedenceForm_whenFieldNotCop_whenMixedHydrocarbon_fullValidation_thenValidationErrors(
      ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithInvalidDistributions(probabilityDistributionType);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);
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
                Set.of("netPorosity." + probabilityDistributionType.getPath() + ".error.error")));

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Gross rock volume (GRV) - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Percentage of GRV which is oil bearing - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Average net pay thickness - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Gross reservoir thickness - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Net to gross - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Net porosity - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%"
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      mode = EnumSource.Mode.INCLUDE,
      names = {"CUSTOM"})
  void validate_whenInvalidExceedenceForm_whenFieldCop_whenMixedHydrocarbon_fullValidation_thenValidationErrors(
      ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithInvalidDistributions(probabilityDistributionType);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);
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
                Set.of("netPorosity." + probabilityDistributionType.getPath() + ".error.error")));

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Gross rock volume (GRV) - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Percentage of GRV which is oil bearing - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Average net pay thickness - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Gross reservoir thickness - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Net to gross - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Net porosity - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%"
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
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.FULL);
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
            entry("depthToGasOilContact." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToGasOilContact." + probabilityDistributionType.getPath() + ".error.error")))
        .doesNotContainKey("depthToHydrocarbonContact." + probabilityDistributionType.getPath() + ".error");

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Gross rock volume (GRV) - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Percentage of GRV which is oil bearing - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Average net pay thickness - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Gross reservoir thickness - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Net to gross - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Net porosity - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Depth to oil-water contact - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Depth to gas-oil contact - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places"
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      mode = EnumSource.Mode.INCLUDE,
      names = {"CUSTOM"})
  void validate_whenInvalidExceedenceForm_whenFieldCop_whenMixedHydrocarbon_partialValidation_thenValidationErrors(
      ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithInvalidDistributions(probabilityDistributionType);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.PARTIAL);
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
                Set.of("netPorosity." + probabilityDistributionType.getPath() + ".error.error")));

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Gross rock volume (GRV) - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Percentage of GRV which is oil bearing - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Average net pay thickness - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Gross reservoir thickness - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Net to gross - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Net porosity - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%"
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"CUSTOM", "NOT_CALCULATED"})
  void validate_whenInvalidExceedenceForm_whenFieldNotCop_whenMixedHydrocarbon_partialValidation_thenValidationErrors(
      ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithInvalidDistributions(probabilityDistributionType);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDP);

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, ValidationType.PARTIAL);
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
                Set.of("netPorosity." + probabilityDistributionType.getPath() + ".error.error")));

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Gross rock volume (GRV) - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Percentage of GRV which is oil bearing - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Average net pay thickness - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Gross reservoir thickness - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Net to gross - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Net porosity - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%"
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenInvalidExceedenceForm_whenDiscovery_whenGasHydrocarbon_thenValidationErrors(
      ValidationType validationType) {
    var form = getFormWithInvalidDistributions(ProbabilityDistributionType.NORMAL);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("averageNetPayThickness." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("averageNetPayThickness." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("grossReservoirThickness." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("grossReservoirThickness." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("grossRockVolume." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("grossRockVolume." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
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
            "Gross rock volume (GRV) - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Average net pay thickness - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Gross reservoir thickness - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Net to gross - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Net porosity - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Depth to gas-water contact - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places"
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenInvalidExceedenceForm_whenDiscovery_whenOilHydrocarbon_thenValidationErrors(
      ValidationType validationType) {
    var form = getFormWithInvalidDistributions(ProbabilityDistributionType.NORMAL);
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);
    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("averageNetPayThickness." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("averageNetPayThickness." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("grossReservoirThickness." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("grossReservoirThickness." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("grossRockVolume." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("grossRockVolume." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
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
            "Gross rock volume (GRV) - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Average net pay thickness - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Gross reservoir thickness - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Net to gross - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Net porosity - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Depth to oil-water contact - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places"
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenInvalidExceedenceForm_whenFieldNotCop_whenOilHydrocarbon_fluidContacts_thenValidationErrors(
      ValidationType validationType) {
    var form = getFormWithInvalidDistributions(ProbabilityDistributionType.NORMAL);
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDP);
    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("depthToOilWaterContact." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("depthToOilWaterContact." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")));

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Depth to oil-water contact - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places"
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenInvalidExceedenceForm_whenFieldCop_whenOilHydrocarbon_fluidContacts_thenValidationErrors(
      ValidationType validationType) {
    var form = getFormWithInvalidDistributions(ProbabilityDistributionType.NORMAL);
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);
    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("depthToMovedOilWaterContact." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("depthToMovedOilWaterContact." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("originalDepthToOilWaterContact." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("originalDepthToOilWaterContact." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")));


    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Depth to moved oil-water contact - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Original depth to oil-water contact - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places"
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenInvalidExceedenceForm_whenFieldNotCop_whenMixedHydrocarbon_fluidContacts_thenValidationErrors(
      ValidationType validationType) {
    var probabilityDistributionType = ProbabilityDistributionType.NORMAL;
    var form = getFormWithInvalidDistributions(probabilityDistributionType);
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDP);
    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("depthToOilWaterContact." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToOilWaterContact." + probabilityDistributionType.getPath() + ".error.error")),
            entry("depthToGasOilContact." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToGasOilContact." + probabilityDistributionType.getPath() + ".error.error")))
        .doesNotContainKey("depthToHydrocarbonContact." + ProbabilityDistributionType.NORMAL.getPath() + ".error");

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Depth to oil-water contact - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Depth to gas-oil contact - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places"
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenInvalidExceedenceForm_whenFieldCop_whenMixedHydrocarbon_fluidContacts_thenValidationErrors(
      ValidationType validationType) {
    var probabilityDistributionType = ProbabilityDistributionType.NORMAL;
    var form = getFormWithInvalidDistributions(probabilityDistributionType);
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);
    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("depthToMovedOilWaterContact." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToMovedOilWaterContact." + probabilityDistributionType.getPath() + ".error.error")),
            entry("depthToMovedGasOilContact." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToMovedGasOilContact." + probabilityDistributionType.getPath() + ".error.error")),
            entry("originalDepthToOilWaterContact." + probabilityDistributionType.getPath() + ".error",
                Set.of("originalDepthToOilWaterContact." + probabilityDistributionType.getPath() + ".error.error")),
            entry("originalDepthToGasOilContact." + probabilityDistributionType.getPath() + ".error",
                Set.of("originalDepthToGasOilContact." + probabilityDistributionType.getPath() + ".error.error")));

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Depth to moved oil-water contact - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Depth to moved gas-oil contact - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Original depth to oil-water contact - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Original depth to gas-oil contact - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places"
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenInvalidExceedenceForm_whenFieldNotCop_whenGasHydrocarbon_fluidContacts_thenValidationErrors(
      ValidationType validationType) {
    var probabilityDistributionType = ProbabilityDistributionType.NORMAL;
    var form = getFormWithInvalidDistributions(probabilityDistributionType);
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDP);
    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("depthToGasWaterContact." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToGasWaterContact." + probabilityDistributionType.getPath() + ".error.error")));

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Depth to gas-water contact - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places"
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenInvalidExceedenceForm_whenFieldCop_whenGasHydrocarbon_fluidContacts_thenValidationErrors(
      ValidationType validationType) {
    var probabilityDistributionType = ProbabilityDistributionType.NORMAL;
    var form = getFormWithInvalidDistributions(probabilityDistributionType);
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);
    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("depthToMovedGasWaterContact." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToMovedGasWaterContact." + probabilityDistributionType.getPath() + ".error.error")),
            entry("originalDepthToGasWaterContact." + probabilityDistributionType.getPath() + ".error",
                Set.of("originalDepthToGasWaterContact." + probabilityDistributionType.getPath() + ".error.error")));


    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Depth to moved gas-water contact - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Original depth to gas-water contact - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places"
        );
  }

  @Test
  void validate_whenInvalidExceedenceForm_whenAnyField_noSetting_thenValidationErrors() {
    var validationType = ValidationType.FULL;
    var form = getInvalidForm_fieldSubmission();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);
    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("reservoirFacies", Set.of("reservoirFacies.required")),
            entry("reservoirConnectivity", Set.of("reservoirConnectivity.required")),
            entry("fracturePressure.decimalInputValue", Set.of("fracturePressure.decimalInputValue.notNumeric")),
            entry("depositionalSetting", Set.of("depositionalSetting.required")),
            entry("isLateralBarriersBaffles", Set.of("isLateralBarriersBaffles.required")),
            entry("isVerticalBarriersBaffles", Set.of("isVerticalBarriersBaffles.required"))
        )
        .doesNotContainKey("depositionalEnvironment");

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "The fracture pressure must be a number",
            "Select a depositional setting",
            "Select if there any lateral barriers or baffles",
            "Select if there any vertical barriers or baffles"
        );
  }

  @Test
  void validate_whenInvalidExceedenceForm_whenAnyField_settingNoEnvironment_thenValidationErrors() {
    var validationType = ValidationType.FULL;
    var depositionalSetting = DepositionalSetting.TERRESTRIAL;

    var form = getInvalidForm_fieldSubmission();
    form.setDepositionalSetting(depositionalSetting.name());

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);
    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("depositionalEnvironmentMap[" + depositionalSetting + "]", Set.of("depositionalEnvironment.required"))
        )
        .doesNotContainKey("depositionalSetting");

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Select a depositional environment"
        );
  }

  @Test
  void validate_whenInvalidExceedenceForm_whenAnyField_yesBarrierBafflesButNoneChecked_thenValidationErrors() {
    var validationType = ValidationType.FULL;

    var form = getInvalidForm_fieldSubmission();
    form.setIsLateralBarriersBaffles(true);
    form.setIsVerticalBarriersBaffles(true);

    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);
    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("lateralBarriersBafflesDetail", Set.of("lateralBarriersBafflesDetail.required")),
            entry("verticalBarriersBafflesDetail", Set.of("verticalBarriersBafflesDetail.required"))
        );

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Select at least one lateral barrier or baffle",
            "Select at least one vertical barrier or baffle"
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenInvalidExceedenceForm_whenDryHole_thenValidationErrors(
      ValidationType validationType) {
    var form = getFormWithInvalidDistributions(ProbabilityDistributionType.NORMAL);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);
    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("averageNetReservoirThickness." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("averageNetReservoirThickness." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("grossReservoirThickness." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("grossReservoirThickness." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("grossRockVolume." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("grossRockVolume." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("percentOfGrvOilBearing." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("percentOfGrvOilBearing." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("netToGross." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("netToGross." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")),
            entry("netPorosity." + ProbabilityDistributionType.NORMAL.getPath() + ".error",
                Set.of("netPorosity." + ProbabilityDistributionType.NORMAL.getPath() + ".error.error")));

    assertThat(bindingResult.getAllErrors())
        .extracting(DefaultMessageSourceResolvable::getDefaultMessage)
        .contains(
            "Gross rock volume (GRV) - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Percentage of GRV which is oil bearing - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Average net reservoir thickness - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Gross reservoir thickness - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places",
            "Net to gross - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%",
            "Net porosity - all mandatory probability inputs must be provided and all inputs must be numbers with up to 2 decimal places. Percentages must not be more than 100%"
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
    submissionDetail.setSubmissionType(submissionType);
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));
    reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType);
    return bindingResult;
  }

  private ReservoirPropertiesForm getValidForm() {
    var form = new ReservoirPropertiesForm();
    form.setFormationName("source formation name");
    form.setAgeStageOrSeries(ReservoirAge.CARBONIFEROUS);
    form.setGroupName(GroupName.BRENT);
    form.setLithology(StandardLithology.OTHER);
    form.setLithologyOther("other lithology");
    form.setReservoirConnectivity("connectivity");
    form.setReservoirFacies("facies");
    form.setAverageNetPayThickness(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
    form.setAverageNetReservoirThickness(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
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

  private ReservoirPropertiesForm getInvalidForm_fieldSubmission() {
    var form = new ReservoirPropertiesForm();
    form.setFracturePressure(new DecimalInput("a"));
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
    form.setAverageNetReservoirThickness(probabilityDistributionForm);
    form.setGrossReservoirThickness(probabilityDistributionForm);
    form.setDepthToHydrocarbonContact(probabilityDistributionForm);
    form.setGrossRockVolume(probabilityDistributionForm);
    form.setNetPorosity(probabilityDistributionForm);
    form.setNetToGross(probabilityDistributionForm);
    form.setPercentOfGrvOilBearing(probabilityDistributionForm);
    form.setDepthToOilWaterContact(probabilityDistributionForm);
    form.setDepthToGasWaterContact(probabilityDistributionForm);
    form.setDepthToGasOilContact(probabilityDistributionForm);
    form.setOriginalDepthToOilWaterContact(probabilityDistributionForm);
    form.setOriginalDepthToGasWaterContact(probabilityDistributionForm);
    form.setOriginalDepthToGasOilContact(probabilityDistributionForm);
    form.setDepthToMovedGasOilContact(probabilityDistributionForm);
    form.setDepthToMovedOilWaterContact(probabilityDistributionForm);
    form.setDepthToMovedGasWaterContact(probabilityDistributionForm);

    return form;
  }
}
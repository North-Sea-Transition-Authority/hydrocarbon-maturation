package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class DryWellDetailsFormValidatorTest {

  private DryWellDetailsFormValidator dryWellDetailsFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    dryWellDetailsFormValidator = new DryWellDetailsFormValidator();
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
  }

  @Test
  void supports_whenDryWellDetailsFormClass_thenTrue() {
    var supportedClass = DryWellDetailsForm.class;
    assertTrue(dryWellDetailsFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenDryWellDetailsFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(dryWellDetailsFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    dryWellDetailsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenEmptyForm_thenValidationErrors() {
    var form = new DryWellDetailsForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    dryWellDetailsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedErrorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("drillDetailsForm.dryWell", Set.of("drillDetailsForm.dryWell.required")),
            entry("drillDetailsForm.coordinateInputForm.latitudeCoordinates.degrees.decimalInputValue",
                Set.of("drillDetailsForm.coordinateInputForm.latitudeCoordinates.degrees.decimalInputValue.required")),
            entry("drillDetailsForm.coordinateInputForm.longitudeCoordinates.minutes.decimalInputValue",
                Set.of("drillDetailsForm.coordinateInputForm.longitudeCoordinates.minutes.decimalInputValue.required")),
            entry("drillDetailsForm.reservoirDatum", Set.of("drillDetailsForm.reservoirDatum.required")),
            entry("drillDetailsForm.totalDepthFormation", Set.of("drillDetailsForm.totalDepthFormation.required")),
            entry("drillDetailsForm.dataAcquisitionProgramme", Set.of("drillDetailsForm.dataAcquisitionProgramme.required")),
            entry("migrationFailureCategory", Set.of("migrationFailureCategory.required")),
            entry("trapFailureCategory", Set.of("trapFailureCategory.required")),
            entry("sealFailureCategory", Set.of("sealFailureCategory.required")),
            entry("reservoirFailureCategory", Set.of("reservoirFailureCategory.required")),
            entry("sourceFailureCategory", Set.of("sourceFailureCategory.required")),
            entry("dryHoleType", Set.of("dryHoleType.required")),
            entry("dryHoleCost.decimalInputValue", Set.of("dryHoleCost.decimalInputValue.required"))
        );

    assertThat(extractedErrorMessages)
        .contains(
            entry("drillDetailsForm.dryWell", Set.of("Enter the dry well reference")),
            entry("drillDetailsForm.coordinateInputForm.latitudeCoordinates.degrees.decimalInputValue",
                Set.of("Enter a latitude")),
            entry("drillDetailsForm.reservoirDatum", Set.of("Select the datum")),
            entry("drillDetailsForm.totalDepthFormation", Set.of("Enter the total depth formation")),
            entry("drillDetailsForm.dataAcquisitionProgramme", Set.of("Enter the data acquisition programme")),
            entry("migrationFailureCategory", Set.of("Select the migration failure category")),
            entry("trapFailureCategory", Set.of("Select the trap failure category")),
            entry("sealFailureCategory", Set.of("Select the seal failure category")),
            entry("reservoirFailureCategory", Set.of("Select the reservoir failure category")),
            entry("sourceFailureCategory", Set.of("Select the source failure category")),
            entry("dryHoleType", Set.of("Select the dry hole shows type")),
            entry("dryHoleCost.decimalInputValue", Set.of("Enter the total well cost"))
        );
  }

  @Test
  void validate_whenGivenFormPartialValidation_wrongCoordinateValues_thenValidationErrors() {
    var form = getValidForm();

    form.getDrillDetailsForm().getCoordinateInputForm().setLatitudeCoordinates(CoordinateInput.createCoordinateInputForForm(
        BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE));
    form.getDrillDetailsForm().getCoordinateInputForm().setLongitudeCoordinates(CoordinateInput.createCoordinateInputForForm(
        BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE));

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    dryWellDetailsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.PARTIAL, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("drillDetailsForm.coordinateInputForm.latitudeCoordinates.degrees.decimalInputValue",
                Set.of("drillDetailsForm.coordinateInputForm.latitudeCoordinates.degrees.decimalInputValue.lessThanMinValue"))
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenNegativeCostValue_thenValidationErrors(ValidationType validationType) {
    var form = getValidForm();

    form.setDryHoleCost(DecimalInput.createDecimalInput(-5));

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    dryWellDetailsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("dryHoleCost.decimalInputValue", Set.of("dryHoleCost.decimalInputValue.lessThanMinValue"))
        );
  }

  private DryWellDetailsForm getValidForm() {
    var form = new DryWellDetailsForm();
    form.getDrillDetailsForm().setDryWell("dryWellName");
    form.setDryHoleComments("dryHoleComments");
    form.setDryHoleScope("dryHoleScope");
    form.getDrillDetailsForm().setReservoirDatum(ReservoirDatum.ED50);
    form.setDryHoleDescription("description");
    form.getDrillDetailsForm().getCoordinateInputForm().setLatitudeCoordinates(
        CoordinateInput.createCoordinateInputForForm(BigDecimal.valueOf(46), BigDecimal.valueOf(35), BigDecimal.valueOf(35)));
    form.getDrillDetailsForm().getCoordinateInputForm().setLongitudeCoordinates(
        CoordinateInput.createCoordinateInputForForm(BigDecimal.valueOf(25), BigDecimal.valueOf(25), BigDecimal.valueOf(25)));
    form.getDrillDetailsForm().getCoordinateInputForm().setLongitudeHemisphere(Hemisphere.EAST);
    form.getDrillDetailsForm().setDataAcquisitionProgramme("data");
    form.getDrillDetailsForm().setGeophysicalUpdate("update");
    form.getDrillDetailsForm().setTotalDepthFormation("formation");
    form.setDryHoleType(DryHoleType.GAS);
    form.setDryHoleCost(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.getDrillDetailsForm().setTotalDepthFormationComments("Depth comments");
    form.setMigrationFailureCategory(MigrationFailureCategory.PROSPECT_NOT_ON_MIGRATION);
    form.setSourceFailureCategory(SourceFailureCategory.SOURCE_NOT_MATURE);
    form.setSealFailureCategory(SealFailureCategory.SEAL_LITHOLOGY_NOT_PRESENT);
    form.setReservoirFailureCategory(ReservoirFailureCategory.RESERVOIR_INTERVAL_POOR_QUALITY);
    form.setTrapFailureCategory(TrapFailureCategory.STRATIGRAPHIC_TRAP_COMPONENTS_NOT_PRESENT);

    return form;
  }
}
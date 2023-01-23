package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

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
class DiscoveryWellDetailsFormValidatorTest {

  private DiscoveryWellDetailsFormValidator discoveryWellDetailsFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    discoveryWellDetailsFormValidator = new DiscoveryWellDetailsFormValidator();
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  }

  @Test
  void supports_whenDiscoveryWellDetailsFormClass_thenTrue() {
    var supportedClass = DiscoveryWellDetailsForm.class;
    assertTrue(discoveryWellDetailsFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenDiscoveryWellDetailsFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(discoveryWellDetailsFormValidator.supports(nonSupportedClass));
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenValidForm_thenNoValidationErrors(ValidationType validationType) {
    var form = getValidForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    discoveryWellDetailsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenEmptyForm_thenValidationErrors() {
    var form = new DiscoveryWellDetailsForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    discoveryWellDetailsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("wellDetailsForm.dryWell", Set.of("wellDetailsForm.dryWell.required")),
            entry("wellDetailsForm.coordinateInputForm.longitudeCoordinates.degrees.decimalInputValue",
                Set.of("wellDetailsForm.coordinateInputForm.longitudeCoordinates.degrees.decimalInputValue.required")),
            entry("wellDetailsForm.coordinateInputForm.longitudeCoordinates.minutes.decimalInputValue",
                Set.of("wellDetailsForm.coordinateInputForm.longitudeCoordinates.minutes.decimalInputValue.required")),
            entry("wellDetailsForm.coordinateInputForm.longitudeCoordinates.seconds.decimalInputValue",
                Set.of("wellDetailsForm.coordinateInputForm.longitudeCoordinates.seconds.decimalInputValue.required")),
            entry("wellDetailsForm.coordinateInputForm.latitudeCoordinates.degrees.decimalInputValue",
                Set.of("wellDetailsForm.coordinateInputForm.latitudeCoordinates.degrees.decimalInputValue.required")),
            entry("wellDetailsForm.coordinateInputForm.latitudeCoordinates.minutes.decimalInputValue",
                Set.of("wellDetailsForm.coordinateInputForm.latitudeCoordinates.minutes.decimalInputValue.required")),
            entry("wellDetailsForm.coordinateInputForm.latitudeCoordinates.seconds.decimalInputValue",
                Set.of("wellDetailsForm.coordinateInputForm.latitudeCoordinates.seconds.decimalInputValue.required")),
            entry("wellDetailsForm.coordinateInputForm.longitudeHemisphere",
                Set.of("wellDetailsForm.coordinateInputForm.longitudeHemisphere.required")),
            entry("wellDetailsForm.reservoirDatum", Set.of("wellDetailsForm.reservoirDatum.required")),
            entry("wellDetailsForm.totalDepthFormation", Set.of("wellDetailsForm.totalDepthFormation.required")),
            entry("wellDetailsForm.dataAcquisitionProgramme", Set.of("wellDetailsForm.dataAcquisitionProgramme.required")),
            entry("hydrocarbonsDiscovered", Set.of("hydrocarbonsDiscovered.required")),
            entry("discoveryStatus", Set.of("discoveryStatus.required")),
            entry("dryHoleCost.decimalInputValue", Set.of("dryHoleCost.decimalInputValue.required")),
            entry("successRelatedCosts.decimalInputValue", Set.of("successRelatedCosts.decimalInputValue.required"))
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenValidForm_withWrongDecimalPointNumbers_thenValidationErrors(ValidationType validationType) {
    var form = getValidForm();

    var bigDecimalWrongNumberOfDP = BigDecimal.valueOf(55.55555);
    form.getWellDetailsForm().getCoordinateInputForm().setLatitudeCoordinates(CoordinateInput.createCoordinateInputForForm(
        bigDecimalWrongNumberOfDP, bigDecimalWrongNumberOfDP, bigDecimalWrongNumberOfDP));
    form.getWellDetailsForm().getCoordinateInputForm().setLongitudeCoordinates(CoordinateInput.createCoordinateInputForForm(
        bigDecimalWrongNumberOfDP, bigDecimalWrongNumberOfDP, bigDecimalWrongNumberOfDP));
    form.setDryHoleCost(DecimalInput.createDecimalInput(bigDecimalWrongNumberOfDP));
    form.setSuccessRelatedCosts(DecimalInput.createDecimalInput(bigDecimalWrongNumberOfDP));

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    discoveryWellDetailsFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));

    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .contains(
            entry("dryHoleCost.decimalInputValue", Set.of("dryHoleCost.decimalInputValue.incorrectDecimalPlaces")),
            entry("successRelatedCosts.decimalInputValue", Set.of("successRelatedCosts.decimalInputValue.incorrectDecimalPlaces")),
            entry("wellDetailsForm.coordinateInputForm.latitudeCoordinates.degrees.decimalInputValue",
                Set.of("wellDetailsForm.coordinateInputForm.latitudeCoordinates.degrees.decimalInputValue.incorrectDecimalPlaces")),
            entry("wellDetailsForm.coordinateInputForm.latitudeCoordinates.minutes.decimalInputValue",
                Set.of("wellDetailsForm.coordinateInputForm.latitudeCoordinates.minutes.decimalInputValue.incorrectDecimalPlaces")),
            entry("wellDetailsForm.coordinateInputForm.latitudeCoordinates.seconds.decimalInputValue",
                Set.of("wellDetailsForm.coordinateInputForm.latitudeCoordinates.seconds.decimalInputValue.incorrectDecimalPlaces")),
            entry("wellDetailsForm.coordinateInputForm.longitudeCoordinates.minutes.decimalInputValue",
                Set.of("wellDetailsForm.coordinateInputForm.longitudeCoordinates.minutes.decimalInputValue.incorrectDecimalPlaces")),
            entry("wellDetailsForm.coordinateInputForm.longitudeCoordinates.seconds.decimalInputValue",
                Set.of("wellDetailsForm.coordinateInputForm.longitudeCoordinates.seconds.decimalInputValue.incorrectDecimalPlaces")),
            entry("wellDetailsForm.coordinateInputForm.longitudeCoordinates.degrees.decimalInputValue",
                Set.of("wellDetailsForm.coordinateInputForm.longitudeCoordinates.degrees.decimalInputValue.moreThanMaxValue"))
        );
  }

  private DiscoveryWellDetailsForm getValidForm() {
    var form = new DiscoveryWellDetailsForm();
    form.getWellDetailsForm().setDryWell("dryWellName");
    form.setChanceOfCommercialityComments("dryHoleComments");
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    form.getWellDetailsForm().setReservoirDatum(ReservoirDatum.ED50);
    form.setDiscoveryStatus(DiscoveryStatus.TECHNICAL_DISCOVERY);
    form.getWellDetailsForm().getCoordinateInputForm().setLatitudeCoordinates(
        CoordinateInput.createCoordinateInputForForm(BigDecimal.valueOf(46), BigDecimal.valueOf(35), BigDecimal.valueOf(35)));
    form.getWellDetailsForm().getCoordinateInputForm().setLongitudeCoordinates(
        CoordinateInput.createCoordinateInputForForm(BigDecimal.valueOf(25), BigDecimal.valueOf(25), BigDecimal.valueOf(25)));
    form.getWellDetailsForm().getCoordinateInputForm().setLongitudeHemisphere(Hemisphere.EAST);
    form.getWellDetailsForm().setDataAcquisitionProgramme("data");
    form.getWellDetailsForm().setGeophysicalUpdate("update");
    form.getWellDetailsForm().setTotalDepthFormation("formation");
    form.setSuccessRelatedCosts(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setDryHoleCost(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.getWellDetailsForm().setTotalDepthFormationComments("Depth comments");
    return form;
  }
}
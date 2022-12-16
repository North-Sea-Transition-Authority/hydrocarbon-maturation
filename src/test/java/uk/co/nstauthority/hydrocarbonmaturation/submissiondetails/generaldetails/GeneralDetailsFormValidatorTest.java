package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.fivium.energyportalapi.generated.types.LicenceStatus;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class GeneralDetailsFormValidatorTest {

  private GeneralDetailsFormValidator generalDetailsFormValidator;

  @Mock
  private LicenceQueryService licenceQueryService;

  @BeforeEach
  void setup() {
    generalDetailsFormValidator = new GeneralDetailsFormValidator(licenceQueryService);
  }

  @Test
  void supports_whenGeneralDetailsFormClass_thenTrue() {
    var supportedClass = GeneralDetailsForm.class;
    assertTrue(generalDetailsFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotGeneralDetailsFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(generalDetailsFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_withOtherInfoSource_thenNoValidationErrors() {
    var form = getValidForm();
    form.setInformationSource(InformationSource.OTHER);
    form.setOtherSourceOfInformation("Other information");

    var bindingResult = validateGeneralDetailsForm(form, ValidationType.FULL);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenValidForm_withLicenceApplicationInfoSource_thenNoValidationErrors() {
    var form = getValidForm();
    form.setInformationSource(InformationSource.LICENCE_APPLICATION);
    form.setLicenceApplication("F33/1234");

    var bindingResult = validateGeneralDetailsForm(form, ValidationType.FULL);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInvalidForm_thenValidationErrors() {
    var form = new GeneralDetailsForm();
    var bindingResult = validateGeneralDetailsForm(form, ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("resourceName", Set.of("resourceName.required")),
            entry("responsibleOrganisationId", Set.of("responsibleOrganisationId.required")),
            entry("resourceOnALicence", Set.of("resourceOnALicence.required")),
            entry("onshoreOffshore", Set.of("onshoreOffshore.required")),
            entry("resourceLocatedInUkcs", Set.of("resourceLocatedInUkcs.required")),
            entry("informationSource", Set.of("informationSource.required")),
            entry("quadrantBlockSelect", Set.of("quadrantBlockSelect.notEmpty"))
        );
  }

  @Test
  void validate_whenInValidForm_noOtherSourceEntered_thenValidationErrors() {
    var form = getValidForm();
    form.setInformationSource(InformationSource.OTHER);

    var bindingResult = validateGeneralDetailsForm(form, ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("otherSourceOfInformation", Set.of("otherSourceOfInformation.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noLicenceApplicationEntered_thenValidationErrors() {
    var form = getValidForm();
    form.setInformationSource(InformationSource.LICENCE_APPLICATION);

    var bindingResult = validateGeneralDetailsForm(form, ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("licenceApplication", Set.of("licenceApplication.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noQuadrantBlockAndLicence_thenValidationErrors() {
    var form = getValidForm();
    form.setQuadrantBlocks(Collections.emptyList());
    form.setLicences(List.of());
    var bindingResult = validateGeneralDetailsForm(form, ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("licenceSelect", Set.of("licenceSelect.required")),
            entry("quadrantBlockSelect", Set.of("quadrantBlockSelect.notEmpty"))
        );
  }

  @ParameterizedTest
  @MethodSource("invalidPercentageArguments")
  void validate_whenInvalidForm_thenValidationErrors(
      String decimalInputValue,
      String errorCode)
  {
    var form = getValidForm();
    form.setPercentageLocatedInUkcs(new DecimalInput(decimalInputValue));

    var bindingResult = validateGeneralDetailsForm(form, ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("percentageLocatedInUkcs.decimalInputValue", Set.of(errorCode))
        );
  }

  private static Stream<Arguments> invalidPercentageArguments() {
    return Stream.of(
        Arguments.of(null, "percentageLocatedInUkcs.decimalInputValue.required"),
        Arguments.of("abcde", "percentageLocatedInUkcs.decimalInputValue.notNumeric"),
        Arguments.of("-5", "percentageLocatedInUkcs.decimalInputValue.lessThanMinValue"),
        Arguments.of("101", "percentageLocatedInUkcs.decimalInputValue.moreThanMaxValue"),
        Arguments.of("10.567", "percentageLocatedInUkcs.decimalInputValue.incorrectDecimalPlaces")
    );
  }

  @Test
  void partialValidation_withValidForm() {
    var form = new GeneralDetailsForm();
    form.setResourceName("Prospect name");
    form.setResourceLocatedInUkcs(false);
    form.setPercentageLocatedInUkcs(new DecimalInput("50.0"));

    var bindingResult = validateGeneralDetailsForm(form, ValidationType.PARTIAL);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void partialValidation_noProspectName() {
    var form = new GeneralDetailsForm();

    var bindingResult = validateGeneralDetailsForm(form, ValidationType.PARTIAL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("resourceName", Set.of("resourceName.required"))
        );
  }

  @Test
  void partialValidation_resourceLocatedInUkcs_invalidPercentage() {
    var form = new GeneralDetailsForm();
    form.setResourceName("Prospect name");
    form.setResourceLocatedInUkcs(false);
    form.setPercentageLocatedInUkcs(new DecimalInput("abc"));
    var bindingResult = validateGeneralDetailsForm(form, ValidationType.PARTIAL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("percentageLocatedInUkcs.decimalInputValue", Set.of("percentageLocatedInUkcs.decimalInputValue.notNumeric"))
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validateLicences_whenNotExtantLicence(ValidationType validationType) {
    var form = getValidForm();
    form.setLicences(List.of("1", "2"));

    when(licenceQueryService.getLicencesByIds(List.of("1", "2")))
        .thenReturn(List.of(
            new LicenceView("Licence1", 1, LicenceStatus.EXTANT),
            new LicenceView("Licence2", 2, LicenceStatus.SURRENDERED)));


    var bindingResult = validateGeneralDetailsForm(form, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("licenceSelect", Set.of("licenceSelect.notValid"))
        );
  }

  private BindingResult validateGeneralDetailsForm(GeneralDetailsForm form, ValidationType validationType) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    generalDetailsFormValidator.validate(form, bindingResult, validationType, SubmissionType.CREATE_PROSPECT);
    return bindingResult;
  }

  private GeneralDetailsForm getValidForm() {
    var form = new GeneralDetailsForm();
    form.setResourceName("Prospect name");
    form.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    form.setResourceLocatedInUkcs(false);
    form.setPercentageLocatedInUkcs(new DecimalInput("50.0"));
    form.setInformationSource(InformationSource.WELL_INVESTMENT_DECISION);
    form.setQuadrantBlocks(List.of("27/10"));
    form.setResponsibleOrganisationId("25");
    form.setLicences(List.of("122"));
    form.setResourceOnALicence(true);
    return form;
  }
}
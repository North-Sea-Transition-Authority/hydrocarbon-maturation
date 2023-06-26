package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import uk.co.nstauthority.hydrocarbonmaturation.exception.ActionNotAllowedException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class GeneralDetailsFormValidatorTest {

  private GeneralDetailsFormValidator generalDetailsFormValidator;

  @Mock
  private LicenceQueryService licenceQueryService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    generalDetailsFormValidator = new GeneralDetailsFormValidator(licenceQueryService);
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
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

    var bindingResult = validateOffshoreGeneralDetailsForm(form, ValidationType.FULL);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenValidForm_withLicenceApplicationInfoSource_thenNoValidationErrors() {
    var form = getValidForm();
    form.setInformationSource(InformationSource.LICENCE_APPLICATION);
    form.setLicenceApplication("F33/1234");

    var bindingResult = validateOffshoreGeneralDetailsForm(form, ValidationType.FULL);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenOffshoreInvalidForm_thenValidationErrors() {
    var form = new GeneralDetailsForm();
    var bindingResult = validateOffshoreGeneralDetailsForm(form, ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("resourceOnALicence", Set.of("resourceOnALicence.required")),
            entry("resourceLocatedInUkcs", Set.of("resourceLocatedInUkcs.required")),
            entry("informationSource", Set.of("informationSource.required")),
            entry("quadrantBlockSelect", Set.of("quadrantBlockSelect.notEmpty"))
        );
  }

  @Test
  void validate_whenOnshoreInvalidForm_thenValidationErrors() {
    var form = new GeneralDetailsForm();
    var bindingResult = validateOnshoreGeneralDetailsForm(form, ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("resourceOnALicence", Set.of("resourceOnALicence.required")),
            entry("resourceLocatedInEngland", Set.of("resourceLocatedInEngland.required")),
            entry("informationSource", Set.of("informationSource.required")),
            entry("quadrantBlockSelect", Set.of("quadrantBlockSelect.notEmpty"))
        );
  }

  @Test
  void validate_whenInvalidForm_andRelinquishment() {
    submissionDetail.setSubmissionType(SubmissionType.RELINQUISHMENT_PROSPECT);
    var form = new GeneralDetailsForm();
    var bindingResult = validateOffshoreGeneralDetailsForm(form, ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("resourceLocatedInUkcs", Set.of("resourceLocatedInUkcs.required")),
            entry("quadrantBlockSelect", Set.of("quadrantBlockSelect.notEmpty")),
            entry("relinquishmentDay", Set.of("relinquishmentDay.required")),
            entry("relinquishmentMonth", Set.of("relinquishmentMonth.required")),
            entry("relinquishmentYear", Set.of("relinquishmentYear.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noOtherSourceEntered_thenValidationErrors() {
    var form = getValidForm();
    form.setInformationSource(InformationSource.OTHER);

    var bindingResult = validateOffshoreGeneralDetailsForm(form, ValidationType.FULL);
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

    var bindingResult = validateOffshoreGeneralDetailsForm(form, ValidationType.FULL);
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
    var bindingResult = validateOffshoreGeneralDetailsForm(form, ValidationType.FULL);
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

    var bindingResult = validateOffshoreGeneralDetailsForm(form, ValidationType.FULL);
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
    form.setResourceLocatedInUkcs(false);
    form.setPercentageLocatedInUkcs(new DecimalInput("50.0"));

    var bindingResult = validateOffshoreGeneralDetailsForm(form, ValidationType.PARTIAL);

    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void partialValidation_resourceLocatedInUkcs_invalidPercentage() {
    var form = new GeneralDetailsForm();
    form.setResourceLocatedInUkcs(false);
    form.setPercentageLocatedInUkcs(new DecimalInput("abc"));
    var bindingResult = validateOffshoreGeneralDetailsForm(form, ValidationType.PARTIAL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("percentageLocatedInUkcs.decimalInputValue", Set.of("percentageLocatedInUkcs.decimalInputValue.notNumeric"))
        );
  }

  @Test
  void partialValidation_resourceLocatedInEngland_invalidPercentage() {
    var form = new GeneralDetailsForm();
    form.setResourceLocatedInEngland(false);
    form.setPercentageLocatedInEngland(new DecimalInput("abc"));
    var bindingResult = validateOnshoreGeneralDetailsForm(form, ValidationType.PARTIAL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("percentageLocatedInEngland.decimalInputValue", Set.of("percentageLocatedInEngland.decimalInputValue.notNumeric"))
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


    var bindingResult = validateOffshoreGeneralDetailsForm(form, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("licenceSelect", Set.of("licenceSelect.notValid"))
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenResourceNotOnLicence_WhenInPreviousSubmission(ValidationType validationType) {
    var form = getValidForm();
    form.setLicences(List.of("1"));
    form.setLicenceSelect(null);
    form.setResourceOnALicence(false);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var generalDetail = new GeneralDetail();
    generalDetail.setResourceOnALicence(true);

    generalDetailsFormValidator.validate(
        form,
        bindingResult,
        new GeneralDetailsFormValidatorHints(validationType, submissionDetail, generalDetail,
            LicensePresent.LICENSE_PRESENT, OnshoreOffshore.OFFSHORE));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("resourceOnALicence", Set.of("resourceOnALicence.required"))
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenNoLicenceSelected_WhenInPreviousSubmission(ValidationType validationType) {
    var form = getValidForm();
    form.setLicences(List.of());
    form.setLicenceSelect(null);
    form.setResourceOnALicence(true);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var generalDetail = new GeneralDetail();
    generalDetail.setResourceOnALicence(true);

    generalDetailsFormValidator.validate(
        form,
        bindingResult,
        new GeneralDetailsFormValidatorHints(validationType, submissionDetail, generalDetail,
            LicensePresent.LICENSE_PRESENT, OnshoreOffshore.OFFSHORE));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedErrorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("licenceSelect", Set.of("licenceSelect.required"))
        );
    assertThat(extractedErrorMessages)
        .containsExactly(
            entry("licenceSelect", Set.of("You cannot remove all licences from a resource that has " +
                "a licence on an earlier approved submission"))
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenRelinquishmentDatePartiallyFilled_whenRelinquishment_assertErrors(ValidationType validationType) {
    var form = getValidForm();
    form.setRelinquishmentYear(null);
    form.setRelinquishmentDay(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail.setSubmissionType(SubmissionType.RELINQUISHMENT_PROSPECT);
    GeneralDetail nullGeneralDetail = null;
    generalDetailsFormValidator.validate(
        form,
        bindingResult,
        new GeneralDetailsFormValidatorHints(validationType, submissionDetail, nullGeneralDetail,
            LicensePresent.LICENSE_PRESENT, OnshoreOffshore.OFFSHORE));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedErrorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("relinquishmentDay", Set.of("relinquishmentDay.required")),
            entry("relinquishmentYear", Set.of("relinquishmentYear.required"))
        );
    assertThat(extractedErrorMessages)
        .containsExactly(
            entry("relinquishmentDay", Set.of("Enter a complete date of relinquishment")),
            entry("relinquishmentYear", Set.of(""))
        );
  }

  @Test
  void validate_whenRelinquishmentDateEmpty_whenPartial_whenRelinquishment_assertEmpty() {
    var form = getValidForm();
    form.setRelinquishmentYear(null);
    form.setRelinquishmentDay(null);
    form.setRelinquishmentMonth(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail.setSubmissionType(SubmissionType.RELINQUISHMENT_PROSPECT);
    GeneralDetail nullGeneralDetail = null;
    generalDetailsFormValidator.validate(
        form,
        bindingResult,
        new GeneralDetailsFormValidatorHints(ValidationType.PARTIAL, submissionDetail, nullGeneralDetail,
            LicensePresent.LICENSE_PRESENT, OnshoreOffshore.OFFSHORE));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .isEmpty();
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenRelinquishmentDateInvalidValues_whenRelinquishment_assertErrors(ValidationType validationType) {
    var form = getValidForm();
    form.setRelinquishmentYear("-2555");
    form.setRelinquishmentDay("36");
    form.setRelinquishmentMonth("13");
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail.setSubmissionType(SubmissionType.RELINQUISHMENT_DISCOVERY);
    GeneralDetail nullGeneralDetail = null;
    generalDetailsFormValidator.validate(
        form,
        bindingResult,
        new GeneralDetailsFormValidatorHints(validationType, submissionDetail, nullGeneralDetail,
            LicensePresent.LICENSE_PRESENT, OnshoreOffshore.OFFSHORE));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedErrorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("relinquishmentDay", Set.of("relinquishmentDay.invalid")),
            entry("relinquishmentMonth", Set.of("relinquishmentMonth.invalid")),
            entry("relinquishmentYear", Set.of("relinquishmentYear.invalid"))
        );
    assertThat(extractedErrorMessages)
        .containsExactly(
            entry("relinquishmentDay", Set.of("Date of relinquishment must be a real date")),
            entry("relinquishmentMonth", Set.of("")),
            entry("relinquishmentYear", Set.of(""))
        );
  }

  @Test
  void validate_whenNoLicenceSelected_WhenNotInPreviousSubmission() {
    var form = getValidForm();
    form.setLicences(List.of());
    form.setLicenceSelect(null);
    form.setResourceOnALicence(true);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var generalDetail = new GeneralDetail();
    generalDetail.setResourceOnALicence(true);

    generalDetailsFormValidator.validate(
        form,
        bindingResult,
        new GeneralDetailsFormValidatorHints(ValidationType.FULL, submissionDetail, generalDetail,
            LicensePresent.LICENSE_NOT_PRESENT, OnshoreOffshore.OFFSHORE));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedErrorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("licenceSelect", Set.of("licenceSelect.required"))
        );
    assertThat(extractedErrorMessages)
        .containsExactly(
            entry("licenceSelect", Set.of("You must select at least one licence"))
        );
  }

  @Test
  void validate_whenResourceNotOnLicence_WhenInPreviousSubmission() {
    var form = getValidForm();
    form.setLicences(List.of());
    form.setLicenceSelect(null);
    form.setResourceOnALicence(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var generalDetail = new GeneralDetail();
    generalDetail.setResourceOnALicence(true);

    generalDetailsFormValidator.validate(
        form,
        bindingResult,
        new GeneralDetailsFormValidatorHints(ValidationType.FULL, submissionDetail, generalDetail,
            LicensePresent.LICENSE_PRESENT, OnshoreOffshore.OFFSHORE));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    var extractedErrorMessages = ValidatorTestingUtil.extractErrorMessages(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("resourceOnALicence", Set.of("resourceOnALicence.required"))
        );
    assertThat(extractedErrorMessages)
        .containsExactly(
            entry("resourceOnALicence", Set.of("You cannot remove all licences from a resource that has a" +
                " licence on an earlier approved submission"))
        );
  }

  @Test
  void validate_whenNoHintsProvided_assertThrow() {
    assertThatThrownBy(() -> generalDetailsFormValidator
        .validate(new GeneralDetailsForm(), ReverseRouter.emptyBindingResult()))
        .hasMessage("Must provide GeneralDetailsFormValidatorHints")
        .isExactlyInstanceOf(ActionNotAllowedException.class);
  }

  private BindingResult validateOffshoreGeneralDetailsForm(GeneralDetailsForm form, ValidationType validationType) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    GeneralDetail nullGeneralDetail = null;
    generalDetailsFormValidator.validate(
        form,
        bindingResult,
        new GeneralDetailsFormValidatorHints(validationType, submissionDetail,
            nullGeneralDetail, LicensePresent.LICENSE_NOT_PRESENT, OnshoreOffshore.OFFSHORE));
    return bindingResult;
  }

  private BindingResult validateOnshoreGeneralDetailsForm(GeneralDetailsForm form, ValidationType validationType) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    GeneralDetail nullGeneralDetail = null;
    generalDetailsFormValidator.validate(
        form,
        bindingResult,
        new GeneralDetailsFormValidatorHints(validationType, submissionDetail,
            nullGeneralDetail, LicensePresent.LICENSE_NOT_PRESENT, OnshoreOffshore.ONSHORE));
    return bindingResult;
  }

  private GeneralDetailsForm getValidForm() {
    var form = new GeneralDetailsForm();
    form.setResourceLocatedInUkcs(false);
    form.setPercentageLocatedInUkcs(new DecimalInput("50.0"));
    form.setInformationSource(InformationSource.WELL_INVESTMENT_DECISION);
    form.setQuadrantBlocks(List.of("27/10"));
    form.setLicences(List.of("122"));
    form.setResourceOnALicence(true);
    form.setRelinquishmentDay("12");
    form.setRelinquishmentMonth("6");
    form.setRelinquishmentYear("2011");
    return form;
  }
}
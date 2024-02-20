package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmitterType;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class GeneralDetailsFormValidatorTest {

  @Mock
  private LicenceQueryService licenceQueryService;

  @Mock
  private OnshoreOffshoreService onshoreOffshoreService;

  private GeneralDetailsFormValidator generalDetailsFormValidator;

  private SubmissionDetail submissionDetail;
  private GeneralDetail generalDetail;

  @BeforeEach
  void setup() {
    generalDetailsFormValidator = new GeneralDetailsFormValidator(licenceQueryService, onshoreOffshoreService);
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    generalDetail = new GeneralDetail();
    generalDetail.setSubmissionDetail(submissionDetail);
    when(onshoreOffshoreService.getOnshoreOffshore(submissionDetail)).thenReturn(OnshoreOffshore.OFFSHORE);
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
            entry("licenceSelect", Set.of("licenceSelect.required")),
            entry("resourceLocatedInUkcs", Set.of("resourceLocatedInUkcs.required")),
            entry("informationSource", Set.of("informationSource.required")),
            entry("quadrantBlockSelect", Set.of("quadrantBlockSelect.notEmpty"))
        );
  }

  @Test
  void validate_whenOnshoreInvalidForm_thenValidationErrors() {
    when(onshoreOffshoreService.getOnshoreOffshore(submissionDetail)).thenReturn(OnshoreOffshore.ONSHORE);
    var form = new GeneralDetailsForm();
    var bindingResult = validateOnshoreGeneralDetailsForm(form, ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("licenceSelect", Set.of("licenceSelect.required")),
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
  void validate_whenInValidForm_noQuadrantBlockAndLicences_thenValidationErrors() {
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

  @Test
  void validate_whenInValidForm_noQuadrantBlocks_thenValidationErrors() {
    var form = getValidForm();
    form.setQuadrantBlocks(Collections.emptyList());
    form.setLicences(List.of("1", "2"));
    var bindingResult = validateOffshoreGeneralDetailsForm(form, ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
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
    when(onshoreOffshoreService.getOnshoreOffshore(submissionDetail)).thenReturn(OnshoreOffshore.ONSHORE);
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
  void validateLicences_whenNotExtantLicence_andIndustrySubmission(ValidationType validationType) {
    var form = getValidForm();
    form.setLicences(List.of("1", "2"));
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
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
  void validateLicences_whenNotExtantLicence_andRegulatorSubmission(ValidationType validationType) {
    var form = getValidForm();
    form.setLicences(List.of("1", "2"));
    submissionDetail.setSubmitterType(SubmitterType.REGULATOR);
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
  void validateLicences_whenCorrection_doNotValidateLicenceStatus(ValidationType validationType) {
    submissionDetail.setCorrection(true);
    var form = getValidForm();
    var bindingResult = validateOffshoreGeneralDetailsForm(form, validationType);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    verifyNoInteractions(licenceQueryService);

    assertThat(extractedErrors).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenRelinquishmentDatePartiallyFilled_whenRelinquishment_assertErrors(ValidationType validationType) {
    var form = getValidForm();
    form.setRelinquishmentYear(null);
    form.setRelinquishmentDay(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail.setSubmissionType(SubmissionType.RELINQUISHMENT_PROSPECT);
    generalDetailsFormValidator.isValid(
        form,
        bindingResult,
        submissionDetail,
        validationType);
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
    generalDetailsFormValidator.isValid(
        form,
        bindingResult,
        submissionDetail,
        ValidationType.PARTIAL);
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
    generalDetailsFormValidator.isValid(
        form,
        bindingResult,
        submissionDetail,
        validationType);
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
  void validate_whenNoLicenceSelected_asIndustrySubmitter() {
    var form = getValidForm();
    form.setLicences(List.of());
    form.setLicenceSelect(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    generalDetailsFormValidator.isValid(
        form,
        bindingResult,
        submissionDetail,
        ValidationType.FULL);
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
  void validate_whenNoLicenceSelected_fieldSubmission_thenNoErrors() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);

    var form = getValidForm();
    form.setLicences(List.of());
    form.setLicenceSelect(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail.setSubmitterType(SubmitterType.INDUSTRY);
    assertTrue(generalDetailsFormValidator.isValid(
        form,
        bindingResult,
        submissionDetail,
        ValidationType.FULL));
  }

  @Test
  void validate_whenNoLicenceSelected_asRegulatorThenNoErrors() {
    var form = getValidForm();
    form.setLicences(List.of());
    form.setLicenceSelect(null);
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    submissionDetail.setSubmitterType(SubmitterType.REGULATOR);
    generalDetailsFormValidator.isValid(
        form,
        bindingResult,
        submissionDetail,
        ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void validatePartial_validFieldSubmission_selectedDriveMechanism(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    var form = getValidForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(generalDetailsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void validateFull_validFieldSubmission_selectedDriveMechanism(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDP);

    var form = getValidForm();
    form.setCurrentDriveMechanism(DriveMechanism.DEPLETION);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    generalDetailsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void validateFull_fieldSubmission_emptyOtherDriveMechanism(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDP);

    var form = getValidForm();
    form.setCurrentDriveMechanism(DriveMechanism.OTHER);
    form.setOtherDriveMechanism(null);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertFalse(generalDetailsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(entry("otherDriveMechanism", Set.of("otherDriveMechanism.required")));
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void validatePartial_fieldSubmission_emptyOtherDriveMechanism(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDP);

    var form = getValidForm();
    form.setCurrentDriveMechanism(DriveMechanism.OTHER);
    form.setOtherDriveMechanism(null);

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(generalDetailsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_FDP", "CREATE_FDPA"}
  )
  void validateFull_FdpAndFdpa_invalidForm(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    var form = getInvalidForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertFalse(generalDetailsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("currentDriveMechanism", Set.of("currentDriveMechanism.required")),
            entry("quadrantBlockSelect", Set.of("quadrantBlockSelect.notEmpty")),
            entry("resourceLocatedInUkcs", Set.of("resourceLocatedInUkcs.required")),
            entry("capitalProjectScopeDelivered", Set.of("capitalProjectScopeDelivered.required"))
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_FDP", "CREATE_FDPA"}
  )
  void validatePartial_FdpAndFdpa_invalidForm(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    var form = getInvalidForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertTrue(generalDetailsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).isEmpty();
  }

  @Test
  void validateFull_Cop_invalidForm() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);

    var form = getInvalidForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertFalse(generalDetailsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("currentDriveMechanism", Set.of("currentDriveMechanism.required")),
            entry("quadrantBlockSelect", Set.of("quadrantBlockSelect.notEmpty")),
            entry("resourceLocatedInUkcs", Set.of("resourceLocatedInUkcs.required")),
            entry("fieldStoppedProducingDay", Set.of("fieldStoppedProducingDay.invalid")),
            entry("fieldStoppedProducingMonth", Set.of("fieldStoppedProducingMonth.invalid")),
            entry("fieldStoppedProducingYear", Set.of("fieldStoppedProducingYear.invalid"))
        );
  }

  @Test
  void validatePartial_Cop_invalidForm() {
    submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);

    var form = getInvalidForm();

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    assertFalse(generalDetailsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.PARTIAL));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("fieldStoppedProducingDay", Set.of("fieldStoppedProducingDay.invalid")),
            entry("fieldStoppedProducingMonth", Set.of("fieldStoppedProducingMonth.invalid")),
            entry("fieldStoppedProducingYear", Set.of("fieldStoppedProducingYear.invalid"))
        );
  }

  @Nested
  class GivenAskingForFieldReport {

    private GeneralDetailsForm form;
    private BindingResult bindingResult;

    @BeforeEach
    void setUp() {
      form = getValidFormNoFileUploaded();
      bindingResult = new BeanPropertyBindingResult(form, "form");
    }

    @Nested
    class GivenFieldSubmissionIsCop {

      @BeforeEach
      void setUp() {
        submissionDetail.setSubmissionType(SubmissionType.CREATE_COP);
      }

      @ParameterizedTest
      @EnumSource(ValidationType.class)
      void whenNoFileUploaded_thenNoError(ValidationType validationType) {
        assertTrue(generalDetailsFormValidator.isValid(
            form,
            bindingResult,
            submissionDetail,
            validationType
        ));
      }
    }

    @Nested
    class GivenFieldSubmissionIsNonCop {

      @BeforeEach
      void setUp() {
        submissionDetail.setSubmissionType(SubmissionType.CREATE_CSR);
      }

      @Test
      void whenNoFileUploaded_partialValidation_thenNoError() {
        assertTrue(generalDetailsFormValidator.isValid(
            form,
            bindingResult,
            submissionDetail,
            ValidationType.PARTIAL
        ));
      }

      @Test
      void whenNoFileUploaded_fullValidation_thenError() {
        generalDetailsFormValidator.isValid(
            form,
            bindingResult,
            submissionDetail,
            ValidationType.FULL
        );

        var errors = ValidatorTestingUtil.extractErrors(bindingResult);

        assertThat(errors)
            .containsExactly(
                entry("fieldReport", Set.of("fieldReport.belowThreshold"))
            );
      }

      @Test
      void whenFileUploaded_noFileDescription_thenError() {
        var uploadedFile = new UploadedFileForm();
        uploadedFile.setFileName("file.docx");
        form.setFieldReport(List.of(uploadedFile));

        generalDetailsFormValidator.isValid(
            form,
            bindingResult,
            submissionDetail,
            ValidationType.FULL
        );

        var errors = ValidatorTestingUtil.extractErrors(bindingResult);

        assertThat(errors)
            .containsExactly(
                entry("fieldReport[0].uploadedFileDescription", Set.of("fieldReport[0].uploadedFileDescription.required"))
            );
      }

      @Test
      void whenFileUploaded_multipleFiles_thenError() {
        var uploadedFile1 = new UploadedFileForm();
        uploadedFile1.setFileName("file1.docx");
        uploadedFile1.setFileDescription("File 1");

        var uploadedFile2 = new UploadedFileForm();
        uploadedFile2.setFileName("file2.docx");
        uploadedFile2.setFileDescription("File 2");

        form.setFieldReport(List.of(uploadedFile1, uploadedFile2));

        generalDetailsFormValidator.isValid(
            form,
            bindingResult,
            submissionDetail,
            ValidationType.FULL
        );

        var errors = ValidatorTestingUtil.extractErrors(bindingResult);

        assertThat(errors)
            .containsExactly(
                entry("fieldReport", Set.of("fieldReport.limitExceeded"))
            );
      }
    }

    private GeneralDetailsForm getValidFormNoFileUploaded() {
      var form = new GeneralDetailsForm();
      form.setResourceLocatedInUkcs(false);
      form.setPercentageLocatedInUkcs(new DecimalInput("50.0"));
      form.setInformationSource(InformationSource.WELL_INVESTMENT_DECISION);
      form.setQuadrantBlocks(List.of("27/10"));
      form.setLicences(List.of("122"));
      form.setRelinquishmentDay("12");
      form.setRelinquishmentMonth("6");
      form.setRelinquishmentYear("2011");
      form.setCapitalProjectScopeDelivered("test summary of scope");
      form.setCurrentDriveMechanism(DriveMechanism.DEPLETION);
      form.setFieldStoppedProducingDay("01");
      form.setFieldStoppedProducingMonth("05");
      form.setFieldStoppedProducingYear("1999");
      return form;
    }
  }

  private BindingResult validateOffshoreGeneralDetailsForm(GeneralDetailsForm form, ValidationType validationType) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    generalDetailsFormValidator.isValid(
        form,
        bindingResult,
        submissionDetail,
        validationType);
    return bindingResult;
  }

  private BindingResult validateOnshoreGeneralDetailsForm(GeneralDetailsForm form, ValidationType validationType) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    generalDetailsFormValidator.isValid(
        form,
        bindingResult,
        submissionDetail,
        validationType);
    return bindingResult;
  }

  private GeneralDetailsForm getInvalidForm() {
    var invalidDecimalInput = new DecimalInput("a");
    var form = new GeneralDetailsForm();
    form.setResourceLocatedInUkcs(null);
    form.setPercentageLocatedInUkcs(invalidDecimalInput);
    form.setInformationSource(null);
    form.setQuadrantBlocks(List.of());
    form.setLicences(List.of());
    form.setRelinquishmentDay("a");
    form.setRelinquishmentMonth("a");
    form.setRelinquishmentYear("a");
    form.setCapitalProjectScopeDelivered(" ");
    form.setCurrentDriveMechanism(null);
    form.setFieldStoppedProducingDay("a");
    form.setFieldStoppedProducingMonth("a");
    form.setFieldStoppedProducingYear("a");
    return form;
  }

  private GeneralDetailsForm getValidForm() {
    var form = new GeneralDetailsForm();
    form.setResourceLocatedInUkcs(false);
    form.setPercentageLocatedInUkcs(new DecimalInput("50.0"));
    form.setInformationSource(InformationSource.WELL_INVESTMENT_DECISION);
    form.setQuadrantBlocks(List.of("27/10"));
    form.setLicences(List.of("122"));
    form.setRelinquishmentDay("12");
    form.setRelinquishmentMonth("6");
    form.setRelinquishmentYear("2011");
    form.setCapitalProjectScopeDelivered("test summary of scope");
    form.setCurrentDriveMechanism(DriveMechanism.DEPLETION);
    form.setFieldStoppedProducingDay("01");
    form.setFieldStoppedProducingMonth("05");
    form.setFieldStoppedProducingYear("1999");

    var fieldReport = new UploadedFileForm();
    fieldReport.setFileName("file.pdf");
    fieldReport.setFileDescription("A test file");
    fieldReport.setFileName("file");

    form.setFieldReport(List.of(fieldReport));
    return form;
  }

  private static Stream<Arguments> allFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(SubmissionDetailUtil::isFieldSubmission)
        .map(Arguments::of);
  }
}
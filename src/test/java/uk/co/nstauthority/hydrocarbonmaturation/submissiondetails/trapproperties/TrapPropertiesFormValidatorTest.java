package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.util.NonSupportedClass;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class TrapPropertiesFormValidatorTest {

  private TrapPropertiesFormValidator trapPropertiesFormValidator;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    trapPropertiesFormValidator = new TrapPropertiesFormValidator();
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @Test
  void supports_whenTrapPropertiesFormClass_thenTrue() {
    var supportedClass = TrapPropertiesForm.class;
    assertTrue(trapPropertiesFormValidator.supports(supportedClass));
  }

  @Test
  void supports_whenNotTrapPropertiesFormClass_thenFalse() {
    var nonSupportedClass = NonSupportedClass.class;
    assertFalse(trapPropertiesFormValidator.supports(nonSupportedClass));
  }

  @Test
  void validate_whenValidForm_thenNoValidationErrors() {
    var form = getValidForm();

    var bindingResult = validateTrapPropertiesForm(form);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_whenInvalidForm_thenValidationErrors() {
    var form = new TrapPropertiesForm();
    var bindingResult = validateTrapPropertiesForm(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("trapDescription", Set.of("trapDescription.required")),
            entry("trapType", Set.of("trapType.required")),
            entry("coordinateInputForm.latitudeCoordinates.degrees.decimalInputValue",
                Set.of("coordinateInputForm.latitudeCoordinates.degrees.decimalInputValue.required")),
            entry("coordinateInputForm.latitudeCoordinates.minutes.decimalInputValue",
                Set.of("coordinateInputForm.latitudeCoordinates.minutes.decimalInputValue.required")),
            entry("coordinateInputForm.latitudeCoordinates.seconds.decimalInputValue",
                Set.of("coordinateInputForm.latitudeCoordinates.seconds.decimalInputValue.required")),
            entry("coordinateInputForm.longitudeCoordinates.degrees.decimalInputValue",
                Set.of("coordinateInputForm.longitudeCoordinates.degrees.decimalInputValue.required")),
            entry("coordinateInputForm.longitudeCoordinates.minutes.decimalInputValue",
                Set.of("coordinateInputForm.longitudeCoordinates.minutes.decimalInputValue.required")),
            entry("coordinateInputForm.longitudeCoordinates.seconds.decimalInputValue",
                Set.of("coordinateInputForm.longitudeCoordinates.seconds.decimalInputValue.required")),
            entry("coordinateInputForm.longitudeHemisphere", Set.of("coordinateInputForm.longitudeHemisphere.required")),
            entry("datum", Set.of("datum.required")),
            entry("centreOfInterestQuadrantBlock", Set.of("centreOfInterestQuadrantBlock.required")),
            entry("depthToCrest.probabilityDistributionType",
                Set.of("depthToCrest.probabilityDistributionType.required")),
            entry("depthToSpillPoint.probabilityDistributionType",
                Set.of("depthToSpillPoint.probabilityDistributionType.required")),
            entry("productiveArea.probabilityDistributionType",
                Set.of("productiveArea.probabilityDistributionType.required")),
            entry("prospectDepthMap",
                Set.of("prospectDepthMap.belowThreshold"))
        );
  }

  @Test
  void validate_whenInvalidForm_noOtherTypeEntered_thenValidationErrors() {
    var form = getValidForm();
    form.setTrapType(TrapType.OTHER);
    var bindingResult = validateTrapPropertiesForm(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);

    assertThat(extractedErrors)
        .containsExactly(
            entry("otherTrapType", Set.of("otherTrapType.required"))
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"CUSTOM", "NOT_CALCULATED"}
  )
  void validate_whenEmptyExceedanceForm_thenValidationErrors(ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithEmptyDistributions(probabilityDistributionType);
    var bindingResult = validateTrapPropertiesForm(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("depthToCrest." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToCrest." + probabilityDistributionType.getPath() + ".error.error")),
            entry("depthToSpillPoint." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToSpillPoint." + probabilityDistributionType.getPath() + ".error.error")),
            entry("productiveArea." + probabilityDistributionType.getPath() + ".error",
                Set.of("productiveArea." + probabilityDistributionType.getPath() + ".error.error"))
        );
  }

  @ParameterizedTest
  @EnumSource(
      value = ProbabilityDistributionType.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"CUSTOM", "NOT_CALCULATED"}
  )
  void validate_whenInvalidExceedanceForm_thenValidationErrors(ProbabilityDistributionType probabilityDistributionType) {
    var form = getFormWithInvalidDistributions(probabilityDistributionType);
    var bindingResult = validateTrapPropertiesForm(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .contains(
            entry("depthToCrest." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToCrest." + probabilityDistributionType.getPath() + ".error.error")),
            entry("depthToSpillPoint." + probabilityDistributionType.getPath() + ".error",
                Set.of("depthToSpillPoint." + probabilityDistributionType.getPath() + ".error.error")),
            entry("productiveArea." + probabilityDistributionType.getPath() + ".error",
                Set.of("productiveArea." + probabilityDistributionType.getPath() + ".error.error"))
        );
  }

  @Test
  void validate_whenInValidForm_noProspectDepthMapFileDescriptionUploaded_thenValidationErrors() {
    var form = getValidForm();
    form.setProspectDepthMap(List.of(new UploadedFileForm()));

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateTrapPropertiesForm(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("prospectDepthMap[0].uploadedFileDescription", Set.of("prospectDepthMap[0].uploadedFileDescription.required"))
        );
  }

  @Test
  void validate_whenInValidForm_noProspectDepthMapFileUploaded_thenValidationErrors() {
    var form = getValidForm();
    form.setProspectDepthMap(new ArrayList<>());

    var extractedErrors = ValidatorTestingUtil.extractErrors(validateTrapPropertiesForm(form));

    assertThat(extractedErrors)
        .containsExactly(
            entry("prospectDepthMap", Set.of("prospectDepthMap.belowThreshold"))
        );
  }

  @ParameterizedTest
  @EnumSource(ValidationType.class)
  void validate_whenInValidForm_twoProspectDepthMapFilesUploaded_thenValidationErrors(
      ValidationType validationType) {
    var form = getValidForm();
    var fileUploadForm = new UploadedFileForm();
    fileUploadForm.setUploadedFileDescription("description");
    fileUploadForm.setUploadedFileId(UUID.randomUUID());
    form.setProspectDepthMap(List.of(fileUploadForm, fileUploadForm));

    var bindingResult = new BeanPropertyBindingResult(form, "form");
    trapPropertiesFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(validationType, submissionDetail));
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors)
        .containsExactly(
            entry("prospectDepthMap", Set.of("prospectDepthMap.limitExceeded"))
        );
  }

  private CoordinateInput getValidLongitudeCoordinates() {
    var coordinateInput = new CoordinateInput();
    coordinateInput.setDegrees(new DecimalInput("15"));
    coordinateInput.setMinutes(new DecimalInput("15"));
    coordinateInput.setSeconds(new DecimalInput("15"));

    return coordinateInput;
  }

  private CoordinateInput getValidLatitudeCoordinates() {
    var coordinateInput = new CoordinateInput();
    coordinateInput.setDegrees(new DecimalInput("45"));
    coordinateInput.setMinutes(new DecimalInput("15"));
    coordinateInput.setSeconds(new DecimalInput("15"));

    return coordinateInput;
  }

  private TrapPropertiesForm getValidForm() {
    var form = new TrapPropertiesForm();
    form.setTrapDescription("description");
    form.setTrapType(TrapType.COMBINATION);
    form.getCoordinateInputForm().setLatitudeCoordinates(getValidLatitudeCoordinates());
    form.getCoordinateInputForm().setLongitudeCoordinates(getValidLongitudeCoordinates());
    form.getCoordinateInputForm().setLongitudeHemisphere(Hemisphere.EAST);
    form.setDepthToCrest(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
    form.setDepthToSpillPoint(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
    form.setProductiveArea(ProbabilityDistributionTestUtil.getValidNormalProbabilityDistributionForm());
    form.setCentreOfInterestQuadrantBlock("15/10");
    form.setDatum(ReservoirDatum.ED50);
    UploadedFileForm fileUploadForm = new UploadedFileForm();
    fileUploadForm.setUploadedFileId(UUID.randomUUID());
    fileUploadForm.setUploadedFileDescription("File description");
    form.setProspectDepthMap(List.of(fileUploadForm));
    return form;
  }

  private TrapPropertiesForm getFormWithEmptyDistributions(ProbabilityDistributionType probabilityDistributionType) {
    var form = getValidForm();
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            probabilityDistributionType,
            new ProbabilityOfExceedanceForm());
    form.setDepthToCrest(probabilityDistributionForm);
    form.setDepthToSpillPoint(probabilityDistributionForm);
    form.setProductiveArea(probabilityDistributionForm);
    return form;
  }

  private TrapPropertiesForm getFormWithInvalidDistributions(ProbabilityDistributionType probabilityDistributionType) {
    var form = getValidForm();
    var probabilityDistributionForm =
        ProbabilityDistributionTestUtil.getFormWithExceedance(
            probabilityDistributionType,
            ProbabilityDistributionTestUtil.getInvalidProbabilityOfExceedanceForm());
    form.setDepthToCrest(probabilityDistributionForm);
    form.setDepthToSpillPoint(probabilityDistributionForm);
    form.setProductiveArea(probabilityDistributionForm);
    return form;
  }

  private BindingResult validateTrapPropertiesForm(TrapPropertiesForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    trapPropertiesFormValidator.validate(
        form, bindingResult, new SubmissionFormValidatorHints(ValidationType.FULL, submissionDetail));
    return bindingResult;
  }
}
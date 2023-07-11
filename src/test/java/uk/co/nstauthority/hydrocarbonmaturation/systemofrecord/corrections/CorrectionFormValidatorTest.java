package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistoryQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.util.ValidatorTestingUtil;

@ExtendWith(MockitoExtension.class)
class CorrectionFormValidatorTest {

  @Mock
  private SubmissionDetailService submissionDetailService;

  @Mock
  private SubmissionHistoryQueryService submissionHistoryQueryService;

  private CorrectionFormValidator correctionFormValidator;

  @BeforeEach
  void setUp() {
    correctionFormValidator = new CorrectionFormValidator(submissionDetailService, submissionHistoryQueryService);
  }

  @Test
  void validate_withValidSubmissionCorrectionForm() {
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.SUBMISSION_CORRECTION);
    form.setSubmissionIdForCorrectingInformation(UUID.randomUUID().toString());
    var bindingResult = validateCorrectionForm(form);
    assertFalse(bindingResult.hasErrors());
  }

  @Test
  void validate_withNoCorrectionTypeSelected() {
    var form = new CorrectionForm();
    var bindingResult = validateCorrectionForm(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("correctionType", Set.of("correctionType.required"))
    );
  }

  @Test
  void validate_withInvalidSubmissionCorrectionForm() {
    var form = new CorrectionForm();
    form.setCorrectionType(CorrectionType.SUBMISSION_CORRECTION);
    var bindingResult = validateCorrectionForm(form);
    var extractedErrors = ValidatorTestingUtil.extractErrors(bindingResult);
    assertThat(extractedErrors).containsExactly(
        entry("submissionIdForCorrectingInformation", Set.of("submissionIdForCorrectingInformation.required"))
    );
  }

  private BindingResult validateCorrectionForm(CorrectionForm form) {
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    correctionFormValidator.isValid(form, new Resource(), bindingResult);
    return bindingResult;
  }
}

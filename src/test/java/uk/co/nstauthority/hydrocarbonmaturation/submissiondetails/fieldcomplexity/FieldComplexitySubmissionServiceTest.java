package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class FieldComplexitySubmissionServiceTest {
  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private FieldComplexityFormValidator fieldComplexityFormValidator;

  @Mock
  private FieldComplexityService fieldComplexityService;

  @InjectMocks
  private FieldComplexitySubmissionService fieldComplexitySubmissionService;

  @ParameterizedTest
  @MethodSource("allNonFieldSubmissionTypes")
  void isSectionSubmittable_nonFieldSubmission_thenTrue(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    assertTrue(fieldComplexitySubmissionService.isSectionSubmittable(submissionDetail, USER));
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void isSectionSubmittable_fieldSubmission_validSubmission_thenTrue(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var fieldComplexityForm = mock(FieldComplexityForm.class);
    when(fieldComplexityService.submissionDetailToForm(submissionDetail))
        .thenReturn(fieldComplexityForm);
    when(fieldComplexityFormValidator
        .isValid(eq(fieldComplexityForm), any(BindingResult.class), eq(submissionDetail), eq(ValidationType.FULL)))
        .thenReturn(true);

    assertTrue(fieldComplexitySubmissionService.isSectionSubmittable(submissionDetail, USER));
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void isSectionSubmittable_fieldSubmission_invalidSubmission_thenFalse(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var fieldComplexityForm = mock(FieldComplexityForm.class);
    when(fieldComplexityService.submissionDetailToForm(submissionDetail))
        .thenReturn(fieldComplexityForm);
    when(fieldComplexityFormValidator
        .isValid(eq(fieldComplexityForm), any(BindingResult.class), eq(submissionDetail), eq(ValidationType.FULL)))
        .thenReturn(false);

    assertFalse(fieldComplexitySubmissionService.isSectionSubmittable(submissionDetail, USER));
  }

  private static Stream<Arguments> allFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(SubmissionDetailUtil::isFieldSubmission)
        .map(Arguments::of);
  }

  private static Stream<Arguments> allNonFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> !SubmissionDetailUtil.isFieldSubmission(submissionType))
        .map(Arguments::of);
  }
}
package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class PlannedSanctionedWellsSubmissionServiceTest {
  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private PlannedSanctionedWellsFormValidator plannedSanctionedWellsFormValidator;

  @Mock
  private PlannedSanctionedWellsService plannedSanctionedWellsService;

  @InjectMocks
  private PlannedSanctionedWellsSubmissionService plannedSanctionedWellsSubmissionService;

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_FDP", "CREATE_FDPA"},
      mode = EnumSource.Mode.EXCLUDE
  )
  void isSectionSubmittable_nonApplicableSubmission_thenTrue(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    assertTrue(plannedSanctionedWellsSubmissionService.isSectionSubmittable(submissionDetail, USER));

    verifyNoInteractions(plannedSanctionedWellsFormValidator);
    verifyNoInteractions(plannedSanctionedWellsService);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_FDP", "CREATE_FDPA"}
  )
  void isSectionSubmittable_validForm_thenTrue(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var form = mock(PlannedSanctionedWellsForm.class);
    when(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail))
        .thenReturn(form);

    when(plannedSanctionedWellsFormValidator.isValid(eq(form), any(BindingResult.class), eq(submissionDetail), eq(ValidationType.FULL)))
        .thenReturn(true);

    assertTrue(plannedSanctionedWellsSubmissionService.isSectionSubmittable(submissionDetail, USER));
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_FDP", "CREATE_FDPA"}
  )
  void isSectionSubmittable_invalidForm_thenFalse(SubmissionType submissionType) {
    var submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var form = mock(PlannedSanctionedWellsForm.class);
    when(plannedSanctionedWellsService.submissionDetailToForm(submissionDetail))
        .thenReturn(form);

    when(plannedSanctionedWellsFormValidator.isValid(eq(form), any(BindingResult.class), eq(submissionDetail), eq(ValidationType.FULL)))
        .thenReturn(false);

    assertFalse(plannedSanctionedWellsSubmissionService.isSectionSubmittable(submissionDetail, USER));
  }
}

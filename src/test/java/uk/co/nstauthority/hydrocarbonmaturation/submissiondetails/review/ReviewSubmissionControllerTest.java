package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionIntegrationTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = ReviewSubmissionController.class)
class ReviewSubmissionControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private ReviewSubmissionService reviewSubmissionService;
  @MockBean
  private SubmissionSummaryModelService submissionSummaryModelService;

  private final SubmissionRecordSummaryView SUBMISSION_RECORD_VIEW = SubmissionIntegrationTestUtils.emptySubmissionRecordSummaryView(
      UUID.randomUUID(), 1, SubmissionType.CREATE_PROSPECT);

  @BeforeEach
  void setUp() {
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    when(reviewSubmissionService.getSubmissionRecordOrThrow(SUBMISSION_DETAIL_ID)).thenReturn(SUBMISSION_RECORD_VIEW);
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_RECORD_VIEW.submissionDetailId()))
        .thenReturn(submissionDetail);
    doReturn(new ModelAndView())
        .when(submissionSummaryModelService).createSummaryForSubmissionTypeToModelAndView(eq(submissionDetail), anyString());
  }

  @SecurityTest
  void showReviewPage_NoUser_assertStatusUnauthorized() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(ReviewSubmissionController.class).showReviewPage(SUBMISSION_DETAIL_ID))))
        .andExpect(status().isUnauthorized());
  }

  @SecurityTest
  void showReviewPage_UserCannotReview_assertStatusUnauthorized() throws Exception {
    when(reviewSubmissionService.canViewSubmission(SUBMISSION_RECORD_VIEW, USER, submissionDetail)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(ReviewSubmissionController.class).showReviewPage(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showReviewPage_UserCanReview_assertStatusAuthorized() throws Exception {
    when(reviewSubmissionService.canViewSubmission(SUBMISSION_RECORD_VIEW, USER, submissionDetail)).thenReturn(true);

    mockMvc.perform(
            get(ReverseRouter.route(on(ReviewSubmissionController.class).showReviewPage(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionDetailStatus.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"SUBMITTED"})
  void showReviewPage_UserCanReview_SubmissionStatus_assertStatusForbidden(SubmissionDetailStatus status) throws Exception {
    submissionDetail.setStatus(status);
    when(reviewSubmissionService.canViewSubmission(SUBMISSION_RECORD_VIEW, USER, submissionDetail)).thenReturn(true);

    mockMvc.perform(
            get(ReverseRouter.route(on(ReviewSubmissionController.class).showReviewPage(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  @SuppressWarnings("unchecked")
  void showSubmissionModelAndView_assertModelProperties() throws Exception {
    when(reviewSubmissionService.canViewSubmission(SUBMISSION_RECORD_VIEW, USER, submissionDetail)).thenReturn(true);
    when(reviewSubmissionService.canReviewSubmission(SUBMISSION_RECORD_VIEW, USER))
        .thenReturn(true);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ReviewSubmissionController.class).showReviewPage(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("submissionRecord", SUBMISSION_RECORD_VIEW),
            entry("canReview", true),
            entry("approveUrl", ReverseRouter.route(on(ApproveSubmissionController.class)
                .showApproveConfirmationPage(submissionDetail.getId()))),
            entry("submissionType", submissionDetail.getSubmissionType().getTypeName()),
            entry("regulatorManagedInformationUrl",
                ReverseRouter.route(on(RegulatorManagedInformationController.class)
                    .getRegulatorManagedInformationPage(
                        submissionDetail.getResourceId().getId(), SUBMISSION_DETAIL_ID))),
            entry("submissionType", submissionDetail.getSubmissionType().getTypeName()),
            entry("withdrawUrl", ReverseRouter.route(on(WithdrawSubmissionController.class)
                .showWithdrawConfirmationPage(submissionDetail.getId())))
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()),
            WorkAreaController.WORK_AREA_TITLE)
    );
  }
}
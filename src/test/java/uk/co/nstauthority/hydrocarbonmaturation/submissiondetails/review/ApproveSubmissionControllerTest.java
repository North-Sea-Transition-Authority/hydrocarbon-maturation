package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionApprovalService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = ApproveSubmissionController.class)
class ApproveSubmissionControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private SubmissionResourceService submissionResourceService;

  @MockBean
  private SubmissionApprovalService submissionApprovalService;

  @MockBean
  private SubmissionSummaryModelService submissionSummaryModelService;

  @BeforeEach
  void setUp() {
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
        .thenReturn(submissionDetail);
    doReturn(new ModelAndView())
        .when(submissionSummaryModelService).createSummaryForSubmissionTypeToModelAndView(eq(submissionDetail), anyString());
  }

  @SecurityTest
  void showApproveConfirmationPage_NoUser_assertStatusUnauthorized() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(ApproveSubmissionController.class)
                .showApproveConfirmationPage(SUBMISSION_DETAIL_ID))))
        .andExpect(status().isUnauthorized());
  }

  @SecurityTest
  void showApproveConfirmationPage_UserCannotApprove_assertStatusUnauthorized() throws Exception {
    when(submissionApprovalService.canApproveSubmission(submissionDetail)).thenReturn(false);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER))).thenReturn(true);
    setUpWhenIsLatestExtantSubmissionVersion_passSecurity(submissionDetail);

    mockMvc.perform(
            get(ReverseRouter.route(on(ApproveSubmissionController.class)
                .showApproveConfirmationPage(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showApproveConfirmationPage_UserCanApprove_assertStatusAuthorized() throws Exception {
    when(submissionApprovalService.canApproveSubmission(submissionDetail)).thenReturn(true);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER))).thenReturn(true);
    when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionDetail))
        .thenReturn("name");
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail))
        .thenReturn(submissionDetail.getSubmissionType().getTypeName());
    setUpWhenIsLatestExtantSubmissionVersion_passSecurity(submissionDetail);
    mockMvc.perform(
            get(ReverseRouter.route(on(ApproveSubmissionController.class)
                .showApproveConfirmationPage(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionDetailStatus.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"SUBMITTED"})
  void showApproveConfirmationPage_whenNotSubmittedStatus_assertStatusForbidden(SubmissionDetailStatus status) throws Exception {
    submissionDetail.setStatus(status);

    mockMvc.perform(
            get(ReverseRouter.route(on(ApproveSubmissionController.class).showApproveConfirmationPage(
                SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void showApproveConfirmationPageModelAndView_assertModelProperties() throws Exception {
    when(submissionApprovalService.canApproveSubmission(submissionDetail)).thenReturn(true);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER))).thenReturn(true);
    var expectedName = "Res name 123";
    when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionDetail))
        .thenReturn(expectedName);
    var expectedTypeName = "Type name";
    when(submissionDetailService.getSubmissionTypeNameLowercaseWithAcronym(submissionDetail))
        .thenReturn(expectedTypeName);
    setUpWhenIsLatestExtantSubmissionVersion_passSecurity(submissionDetail);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ApproveSubmissionController.class).showApproveConfirmationPage(
                SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("pageName", "Are you sure you want to accept this %s?".formatted(expectedTypeName)),
            entry("resourceName", expectedName),
            entry("actionUrl", ReverseRouter.route(on(ApproveSubmissionController.class)
                .approveSubmission(SUBMISSION_DETAIL_ID, null))),
            entry("cancelUrl", ReverseRouter.route(on(ReviewSubmissionController.class)
                .showReviewPage(submissionDetail.getSubmission().getResource().getId())))
        );

    @SuppressWarnings(value="unchecked")
    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)),
            WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(ReviewSubmissionController.class)
                .showReviewPage(submissionDetail.getSubmission().getResource().getId())),
            expectedName)
    );
  }

  @SecurityTest
  void approveSubmission_canApprove_verifyMethodCall() throws Exception{
    when(submissionApprovalService.canApproveSubmission(submissionDetail)).thenReturn(true);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER))).thenReturn(true);
    var expectedName = "Res name 123";
    when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionDetail))
        .thenReturn(expectedName);
    var expectedTypeName = "Type name";
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail))
        .thenReturn(expectedTypeName);
    setUpWhenIsLatestExtantSubmissionVersion_passSecurity(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(ApproveSubmissionController.class)
            .approveSubmission(SUBMISSION_DETAIL_ID, null)))
            .with(user(USER)).with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ViewResourceRecordController.class)
            .showResourceRecord(submissionDetail.getSubmission().getResource().getId()))));

    verify(submissionApprovalService).approveSubmissionOrThrow(submissionDetail);
  }

  @SecurityTest
  void approveSubmission_canNotApprove() throws Exception{
    doAnswer(invocation -> {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Submission cannot be accepted by user with ID %s "
          .formatted("1"));
    }).when(submissionApprovalService).approveSubmissionOrThrow(submissionDetail);
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    setUpWhenIsLatestExtantSubmissionVersion_passSecurity(submissionDetail);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER))).thenReturn(true);

    mockMvc.perform(
            post(ReverseRouter.route(on(ApproveSubmissionController.class)
                .approveSubmission(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER)).with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @SecurityTest
  void approveSubmission_whenDoesNotHaveRegulatorPermission() throws Exception{
    setUpWhenIsLatestExtantSubmissionVersion_passSecurity(submissionDetail);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER))).thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(ApproveSubmissionController.class)
                .approveSubmission(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER)).with(csrf()))
        .andExpect(status().isForbidden());
    verifyNoInteractions(submissionApprovalService);

  }

  @SecurityTest
  void approveSubmission_whenIsNotLatestExtantVersion_failSecurity() throws Exception{
    submissionDetail.setVersionNumber(4);
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER))).thenReturn(false);
    when(submissionDetailService.isLatestSubmissionDetailForSubmissionWithStatuses(
        submissionDetail,
        Set.of(SubmissionDetailStatus.SUBMITTED, SubmissionDetailStatus.ACCEPTED)
    )).thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(ApproveSubmissionController.class)
                .approveSubmission(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER)).with(csrf()))
        .andExpect(status().isForbidden());
    verifyNoInteractions(submissionApprovalService);
  }
}
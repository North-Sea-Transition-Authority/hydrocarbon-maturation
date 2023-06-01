package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = WithdrawSubmissionController.class)
class WithdrawSubmissionControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private SubmissionResourceService submissionResourceService;

  @MockBean
  private WithdrawSubmissionService withdrawSubmissionService;

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
  void showWithdrawConfirmationPage_NoUser_assertStatusUnauthorized() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(WithdrawSubmissionController.class)
                .showWithdrawConfirmationPage(SUBMISSION_DETAIL_ID))))
        .andExpect(status().isUnauthorized());
  }

  @SecurityTest
  void showWithdrawConfirmationPage_UserCannotWithdraw_assertStatusUnauthorized() throws Exception {
    when(withdrawSubmissionService.canWithdrawSubmission(submissionDetail)).thenReturn(false);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER))).thenReturn(true);

    mockMvc.perform(
            get(ReverseRouter.route(on(WithdrawSubmissionController.class)
                .showWithdrawConfirmationPage(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showWithdrawConfirmationPage_UserCanWithdraw_assertStatusAuthorized() throws Exception {
    when(withdrawSubmissionService.canWithdrawSubmission(submissionDetail)).thenReturn(true);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER))).thenReturn(true);
    when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionDetail))
        .thenReturn("name");
    when(submissionDetailService.getSubmissionTypeName(submissionDetail))
        .thenReturn(submissionDetail.getSubmissionType().getTypeName());
    setUpWhenIsLatestExtantSubmissionVersion_passSecurity(submissionDetail);

    mockMvc.perform(
            get(ReverseRouter.route(on(WithdrawSubmissionController.class)
                .showWithdrawConfirmationPage(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionDetailStatus.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"SUBMITTED"})
  void showWithdrawConfirmationPage_whenNotSubmittedStatus_assertStatusForbidden(SubmissionDetailStatus status) throws Exception {
    submissionDetail.setStatus(status);

    mockMvc.perform(
            get(ReverseRouter.route(on(WithdrawSubmissionController.class).showWithdrawConfirmationPage(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  @SuppressWarnings("unchecked")
  void showWithdrawConfirmationPageModelAndView_assertModelProperties() throws Exception {
    when(withdrawSubmissionService.canWithdrawSubmission(submissionDetail)).thenReturn(true);
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER))).thenReturn(true);
    var expectedName = "Res name 123";
    when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionDetail))
        .thenReturn(expectedName);
    var expectedTypeName = "Type name";
    when(submissionDetailService.getSubmissionTypeName(submissionDetail))
        .thenReturn(expectedTypeName);
    setUpWhenIsLatestExtantSubmissionVersion_passSecurity(submissionDetail);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(WithdrawSubmissionController.class).showWithdrawConfirmationPage(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("pageName", "Are you sure you want to withdraw this %s?".formatted(expectedTypeName.toLowerCase())),
            entry("resourceName", expectedName),
            entry("actionUrl", ReverseRouter.route(on(WithdrawSubmissionController.class)
                .withdrawSubmission(SUBMISSION_DETAIL_ID, null, null, null))),
            entry("cancelUrl", ReverseRouter.route(on(ReviewSubmissionController.class)
                .showReviewPage(submissionDetail.getSubmission().getResource().getId())))
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()),
            WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(ReviewSubmissionController.class)
                .showReviewPage(submissionDetail.getSubmission().getResource().getId())),
            expectedName)
    );
  }

  @Test
  void withdrawSubmission_canWithdraw_verifyMethodCall() throws Exception{
    when(withdrawSubmissionService.canWithdrawSubmission(submissionDetail)).thenReturn(true);
    var expectedName = "Res name 123";
    when(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionDetail))
        .thenReturn(expectedName);
    var expectedTypeName = "Type name";
    when(submissionDetailService.getSubmissionTypeName(submissionDetail))
        .thenReturn(expectedTypeName);
    when(withdrawSubmissionService.validate(any(), any()))
        .thenReturn(ReverseRouter.emptyBindingResult());
    setUpWhenIsLatestExtantSubmissionVersion_passSecurity(submissionDetail);
    mockMvc.perform(
            post(ReverseRouter.route(on(WithdrawSubmissionController.class)
                .withdrawSubmission(SUBMISSION_DETAIL_ID, null, null, null)))
                .with(user(USER)).with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(WorkAreaController.class)
            .getWorkArea())));

    verify(withdrawSubmissionService).withdrawSubmissionOrThrow(eq(submissionDetail), any());
  }

  @SecurityTest
  void withdrawSubmission_canNotWithdraw() throws Exception{
    doAnswer(invocation -> {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Submission cannot be withdrawn by user with ID %s "
          .formatted(USER.wuaId().toString()));
    }).when(withdrawSubmissionService).withdrawSubmissionOrThrow(eq(submissionDetail), any());
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER))).thenReturn(true);
    when(withdrawSubmissionService.validate(any(), any()))
        .thenReturn(ReverseRouter.emptyBindingResult());
    when(submissionDetailService.getSubmissionTypeName(submissionDetail))
        .thenReturn(submissionDetail.getSubmissionType().getTypeName());
    setUpWhenIsLatestExtantSubmissionVersion_passSecurity(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(WithdrawSubmissionController.class)
                .withdrawSubmission(SUBMISSION_DETAIL_ID, null, null, null)))
                .with(user(USER)).with(csrf()))
        .andExpect(status().isUnauthorized());
  }

  @SecurityTest
  void withdrawSubmission_whenDoesNotHaveRegulatorPermission() throws Exception{
    when(permissionService.hasPermission(USER, Set.of(RolePermission.EXPLORATION,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER, RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER))).thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(WithdrawSubmissionController.class)
                .withdrawSubmission(SUBMISSION_DETAIL_ID, null, null, null)))
                .with(user(USER)).with(csrf()))
        .andExpect(status().isForbidden());
    verifyNoInteractions(withdrawSubmissionService);
  }
}
package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = RequestUpdateController.class)
class RequestUpdateControllerTest extends AbstractSubmissionControllerTest {
  private static final User USER = UserTestUtil.Builder().build();
  private static final String RESOURCE_NAME = "Resource name here";
  private static final String TYPE_NAME = "Type name here";

  @MockBean
  private SubmissionResourceService submissionResourceService;

  @MockBean
  private SubmissionSummaryModelService submissionSummaryModelService;

  @MockBean
  private SubmissionUpdateService submissionUpdateService;

  @MockBean
  private SubmissionUpdatePersistenceService submissionUpdatePersistenceService;

  @BeforeEach
  void setUp() {
    submissionDetail.setStatus(SubmissionDetailStatus.SUBMITTED);
    when(submissionDetailService.getSubmissionDetail(SUBMISSION_DETAIL_ID))
        .thenReturn(Optional.of(submissionDetail));

    doReturn(new ModelAndView())
        .when(submissionSummaryModelService).createSummaryForSubmissionTypeToModelAndView(eq(submissionDetail), anyString(), any());
  }

  @SecurityTest
  void showRequestUpdateConfirmationPage_NoUser_assertStatusUnauthorized() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(RequestUpdateController.class)
                .showRequestUpdateConfirmationPage(SUBMISSION_DETAIL_ID, null))))
        .andExpect(status().isUnauthorized());
  }

  @SecurityTest
  void showRequestUpdateConfirmationPage_UserCannotRequest_assertStatusUnauthorized() throws Exception {
    when(submissionUpdateService.canRequestUpdateOnSubmission(submissionDetail, USER)).thenReturn(false);
    setUpWhenUserIsResponsibleTeam_passSecurity();

    mockMvc.perform(
            get(ReverseRouter.route(on(RequestUpdateController.class)
                .showRequestUpdateConfirmationPage(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showRequestUpdateConfirmationPage_UserCanRequest_assertStatusAuthorized() throws Exception {
    when(submissionUpdateService.canRequestUpdateOnSubmission(submissionDetail, USER)).thenReturn(true);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(submissionDetail))
        .thenReturn(RESOURCE_NAME);
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail))
        .thenReturn(submissionDetail.getSubmissionType().getTypeName());
    mockMvc.perform(
            get(ReverseRouter.route(on(RequestUpdateController.class)
                .showRequestUpdateConfirmationPage(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionDetailStatus.class,
      mode = EnumSource.Mode.EXCLUDE,
      names = {"SUBMITTED"})
  void showRequestUpdateConfirmationPage_whenNotSubmittedStatus_assertStatusForbidden(SubmissionDetailStatus status) throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    submissionDetail.setStatus(status);

    mockMvc.perform(
            get(ReverseRouter.route(on(RequestUpdateController.class)
                .showRequestUpdateConfirmationPage(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  @SuppressWarnings("unchecked")
  void showRequestUpdateConfirmationPage_assertModelProperties() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(submissionUpdateService.canRequestUpdateOnSubmission(submissionDetail, USER))
        .thenReturn(true);
    when(submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(submissionDetail))
        .thenReturn(RESOURCE_NAME);
    when(submissionDetailService.getSubmissionTypeNameLowercaseWithAcronym(submissionDetail))
        .thenReturn(TYPE_NAME);

    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(RequestUpdateController.class)
                .showRequestUpdateConfirmationPage(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("submissionType", submissionDetail.getSubmissionType()),
            entry("pageName", "Are you sure you want to request an update for this %s?".formatted(TYPE_NAME)),
            entry("resourceName", RESOURCE_NAME),
            entry("actionUrl", ReverseRouter.route(on(RequestUpdateController.class)
                .requestUpdate(SUBMISSION_DETAIL_ID, null, null, null, null))),
            entry("cancelUrl", ReverseRouter.route(on(ReviewSubmissionController.class)
                .showReviewPage(submissionDetail.getSubmission().getResource().getId(), null)))
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)),
            WorkAreaController.WORK_AREA_TITLE),
        entry(ReverseRouter.route(on(ReviewSubmissionController.class)
                .showReviewPage(submissionDetail.getSubmission().getResource().getId(), null)),
            RESOURCE_NAME)
    );
  }

  @Test
  void requestUpdate_canRequest_verifyMethodCall() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(submissionUpdateService.canRequestUpdateOnSubmission(submissionDetail, USER)).thenReturn(true);
    when(submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(submissionDetail))
        .thenReturn(RESOURCE_NAME);
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail))
        .thenReturn(TYPE_NAME);
    when(submissionUpdateService.validate(any(), any()))
        .thenReturn(ReverseRouter.emptyBindingResult());

    mockMvc.perform(
            post(ReverseRouter.route(on(RequestUpdateController.class)
                .requestUpdate(SUBMISSION_DETAIL_ID, null, null, null, null)))
                .with(user(USER)).with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(ReviewSubmissionController.class)
            .showReviewPage(submissionDetail.getSubmission().getResource().getId(), null))));

    verify(submissionUpdatePersistenceService).requestUpdate(eq(submissionDetail), any(), any());
  }

  @SecurityTest
  void requestUpdate_canNotRequest() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(true);
    when(submissionUpdateService.validate(any(), any()))
        .thenReturn(ReverseRouter.emptyBindingResult());
    when(submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail))
        .thenReturn(submissionDetail.getSubmissionType().getTypeName());
    when(submissionUpdateService.canRequestUpdateOnSubmission(submissionDetail, USER))
        .thenReturn(false);
    mockMvc.perform(
            post(ReverseRouter.route(on(RequestUpdateController.class)
                .requestUpdate(SUBMISSION_DETAIL_ID, null, null, null, null)))
                .with(user(USER)).with(csrf()))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void requestUpdate_whenDoesNotHaveRegulatorPermission() throws Exception {
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.REGULATOR, Role.EXPLORATION)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.NORTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.SOUTHERN_OFFSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);
    when(teamQueryService.userHasRoleInTeamType(user.wuaId(), TeamType.ONSHORE_AREA, Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM)).thenReturn(false);

    mockMvc.perform(
            post(ReverseRouter.route(on(RequestUpdateController.class)
                .requestUpdate(SUBMISSION_DETAIL_ID, null, null, null, null)))
                .with(user(USER)).with(csrf()))
        .andExpect(status().isForbidden());
    verifyNoInteractions(submissionUpdatePersistenceService);
  }
}
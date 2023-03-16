package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;

@ContextConfiguration(classes = ViewResourceRecordController.class)
class ViewResourceRecordControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private ViewResourceRecordService viewResourceRecordService;
  @MockBean
  private SubmissionSummaryModelService submissionSummaryModelService;

  private final UUID RESOURCE_ID = UUID.randomUUID();
  private final ResourceRecordSummaryView RESOURCE_RECORD_VIEW = SubmissionIntegrationTestUtils.emptyResourceRecordSummaryView(
      UUID.randomUUID(), 1);
  private final SubmissionDetail SUBMISSION_DETAIL = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

  @BeforeEach
  void setUp() {
    when(viewResourceRecordService.getResourceRecordOrThrow(RESOURCE_ID)).thenReturn(RESOURCE_RECORD_VIEW);
    when(submissionDetailService.getSubmissionDetailForIdOrError(RESOURCE_RECORD_VIEW.submissionDetailId()))
        .thenReturn(SUBMISSION_DETAIL);
    doReturn(new ModelAndView())
        .when(submissionSummaryModelService).createSummaryForSubmissionTypeToModelAndView(eq(SUBMISSION_DETAIL), anyString());
  }

  @SecurityTest
  void showResourceRecord_NoUser_assertStatusUnauthorized() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(RESOURCE_ID))))
        .andExpect(status().isUnauthorized());
  }

  @SecurityTest
  void showResourceRecord_UserCannotViewRecord_assertStatusUnauthorized() throws Exception {
    when(viewResourceRecordService.canViewResource(RESOURCE_RECORD_VIEW, USER)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(RESOURCE_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void showResourceRecord_UserCanViewRecord_assertStatusUnauthorized() throws Exception {
    when(viewResourceRecordService.canViewResource(RESOURCE_RECORD_VIEW, USER)).thenReturn(true);

    mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(RESOURCE_ID)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }

  @Test
  @SuppressWarnings("unchecked")
  void showResourceRecordModelAndView_assertModelProperties() throws Exception {
    when(viewResourceRecordService.canViewResource(RESOURCE_RECORD_VIEW, USER)).thenReturn(true);
    when(viewResourceRecordService.isInResponsibleRegulatorTeam(USER))
        .thenReturn(true);
    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(RESOURCE_ID)))
                .with(user(USER)))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("resourceRecord", RESOURCE_RECORD_VIEW),
            entry("submissionType", SUBMISSION_DETAIL.getSubmissionType()),
            entry("isRegulatorInResponsibleTeam", true),
            entry("regulatorManagedInformationUrl",
                ReverseRouter.route(on(RegulatorManagedInformationController.class)
                    .getRegulatorManagedInformationPage(SUBMISSION_DETAIL.getResourceId().getId(), null)))
        );

    var breadcrumbsMap = ((Map<String, String>) modelMap.get("breadcrumbsMap"));

    assertThat(breadcrumbsMap.entrySet()).containsExactly(
        entry(ReverseRouter.route(on(SearchResourceRecordsController.class).showSearchResourceRecordsScreen()),
            SearchResourceRecordsController.PAGE_TITLE)
    );
  }
}
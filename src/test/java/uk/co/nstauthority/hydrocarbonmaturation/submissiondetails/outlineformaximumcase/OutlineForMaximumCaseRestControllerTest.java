package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ResourceRecordSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordService;

@ContextConfiguration(classes = OutlineForMaximumCaseRestController.class)
class OutlineForMaximumCaseRestControllerTest extends AbstractSubmissionControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;

  @MockBean
  ViewResourceRecordService viewResourceRecordService;
  private UUID submissionDetailId;

  public static final ResourceRecordSummaryView resourceRecordSummaryView = new ResourceRecordSummaryView(UUID.randomUUID(),
      "resource_name_and_centre_of_interest", 1369, "organisation_responsible", List.of(1020), List.of("licences"),
      ResponsibleTeamType.EXPLORATION, "parent name", "field status");

  @BeforeEach
  void setUp() {
    submissionDetailId = submissionDetail.getId();
    when(submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId)).thenReturn(submissionDetail);
  }

  @SecurityTest
  void getGeoJsonForOutlineForMaximumCase_assertStatusOk() throws Exception {
    when(viewResourceRecordService.getResourceRecordOrThrow(any(UUID.class))).thenReturn(resourceRecordSummaryView);
    when(viewResourceRecordService.canViewResource(resourceRecordSummaryView, USER)).thenReturn(true);

    mockMvc.perform(
            get(ReverseRouter.route(on(OutlineForMaximumCaseRestController.class).getGeoJsonForOutlineForMaximumCase(submissionDetailId)))
                .with(user(USER)))
        .andExpect(status().isOk());
  }
}
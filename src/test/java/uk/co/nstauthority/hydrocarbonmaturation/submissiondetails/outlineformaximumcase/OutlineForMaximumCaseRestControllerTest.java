package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailAccessService;

@ContextConfiguration(classes = OutlineForMaximumCaseRestController.class)
class OutlineForMaximumCaseRestControllerTest extends AbstractSubmissionControllerTest {

  private static final User USER = UserTestUtil.Builder().build();

  @MockBean
  OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;

  @MockBean
  SubmissionDetailAccessService submissionDetailAccessService;

  @BeforeEach
  void setUp() {
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID)).thenReturn(submissionDetail);
  }

  @SecurityTest
  void getGeoJsonForOutlineForMaximumCase_assertForbidden() throws Exception {
    when(submissionDetailAccessService.canViewSubmissionDetail(submissionDetail, USER)).thenReturn(false);

    mockMvc.perform(
            get(ReverseRouter.route(on(OutlineForMaximumCaseRestController.class).getGeoJsonForOutlineForMaximumCase(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @Test
  void getGeoJsonForOutlineForMaximumCase_assertStatusOk() throws Exception {
    when(submissionDetailAccessService.canViewSubmissionDetail(submissionDetail, USER)).thenReturn(true);
    when(outlineForMaximumCasePersistenceService.getGeoJsonForOutlineForMaximumCase(submissionDetail)).thenReturn("{}");

    mockMvc.perform(
            get(ReverseRouter.route(on(OutlineForMaximumCaseRestController.class).getGeoJsonForOutlineForMaximumCase(SUBMISSION_DETAIL_ID)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(MockMvcResultMatchers.content().string("{}"));
  }
}
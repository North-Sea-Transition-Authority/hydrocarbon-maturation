package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StartSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsFormService;

@ContextConfiguration(classes = StartProspectController.class)
class StartProspectControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  GeneralDetailsFormService generalDetailsFormService;

  @MockBean
  StartProspectService startProspectService;

  @SecurityTest
  void getStartPage_assertStatusOk() throws Exception {
    mockMvc.perform(
        get(ReverseRouter.route(on(StartProspectController.class).getStartPage()))
            .with(user(USER)))
        .andExpect(status().isOk());
  }

  @SecurityTest
  void startProspect_assertRedirection() throws Exception {
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setId(UUID.randomUUID());
    when(startProspectService.createInitialProspect()).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(StartProspectController.class).startProspect()))
            .with(user(USER))
            .with(csrf()))
    .andExpect(status().is3xxRedirection())
    .andExpect(redirectedUrl(ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetails(submissionDetail.getId(), null))));

    verify(startProspectService, times(1)).createInitialProspect();
  }

  @Test
  void getStartPage_assertModelProperties() throws Exception {
    var modelAndView = mockMvc.perform(
        get(ReverseRouter.route(on(StartProspectController.class).getStartPage()))
            .with(user(USER)))
        .andReturn()
        .getModelAndView();

    assertThat(modelAndView).isNotNull();
    var model = modelAndView.getModel();
    assertEquals(ReverseRouter.route(on(StartProspectController.class).startProspect()), model.get("actionUrl"));
    assertEquals(ReverseRouter.route(on(StartSubmissionController.class).getSubmissionPurpose()), model.get("backLinkUrl"));
  }
}
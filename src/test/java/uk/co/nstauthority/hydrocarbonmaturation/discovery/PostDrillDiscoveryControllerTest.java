package uk.co.nstauthority.hydrocarbonmaturation.discovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.updateprospect.UpdateProspectForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;

@ContextConfiguration(classes = PostDrillDiscoveryController.class)
class PostDrillDiscoveryControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private StartDiscoveryService startDiscoveryService;

  private static SubmissionDetail submissionDetail;
  private static UUID submissionDetailId;

  @BeforeAll
  static void setup() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetailId = submissionDetail.getId();
  }

  @Test
  void getStartDiscoveryPage_assertModelProperties() throws Exception {
    var form = new StartDiscoveryForm();

    var modelAndView = mockMvc.perform(
        get(ReverseRouter.route(on(PostDrillDiscoveryController.class).getStartDiscoveryPage(null)))
            .flashAttr("form", form)
            .with(user(USER))
        )
        .andReturn()
        .getModelAndView();

    assert modelAndView != null;
    assertThat(modelAndView.getModel())
        .extracting(
            "backLinkUrl",
            "actionUrl",
            "form",
            "prospectApiEndpoint")
        .containsExactly(
            ReverseRouter.route(on(StartPostDrillDiscoveryController.class).getStartPostDrillDiscoveryPage()),
            ReverseRouter.route(on(PostDrillDiscoveryController.class).startDiscovery(null, null)),
            form,
            SearchSelectorService.route(on(ResourceRestController.class).searchProspects(null))
        );
  }

  @Test
  void startDiscovery_asValidForm_assertRedirect() throws Exception {
    var form = new StartDiscoveryForm();

    var bindingResult = new BeanPropertyBindingResult(UpdateProspectForm.class, "form");
    when(startDiscoveryService.validate(any(), any())).thenReturn(bindingResult);
    when(startDiscoveryService.createNewDiscovery(form)).thenReturn(submissionDetail);

    mockMvc.perform(
            post(ReverseRouter.route(on(PostDrillDiscoveryController.class).startDiscovery(null, null)))
                .flashAttr("form", form)
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(
            ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetailId))));
  }

  @Test
  void startDiscovery_asInvalidForm_assertIsOk() throws Exception {
    var form = new StartDiscoveryForm();

    var bindingResult = new BeanPropertyBindingResult(UpdateProspectForm.class, "form");
    bindingResult.addError(new ObjectError("form", "invalid form"));
    when(startDiscoveryService.validate(any(), any())).thenReturn(bindingResult);

    var modelAndView = mockMvc.perform(
            post(ReverseRouter.route(on(PostDrillDiscoveryController.class).startDiscovery(null, null)))
                .flashAttr("form", form)
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    assert modelAndView != null;
    assertThat(modelAndView.getModel())
        .extracting(
            "backLinkUrl",
            "actionUrl",
            "form",
            "prospectApiEndpoint")
        .containsExactly(
            ReverseRouter.route(on(StartPostDrillDiscoveryController.class).getStartPostDrillDiscoveryPage()),
            ReverseRouter.route(on(PostDrillDiscoveryController.class).startDiscovery(null, null)),
            form,
            SearchSelectorService.route(on(ResourceRestController.class).searchProspects(null))
        );

    verify(startDiscoveryService, times(1)).validate(any(), any());
    verify(startDiscoveryService, never()).createNewDiscovery(any());
  }
}
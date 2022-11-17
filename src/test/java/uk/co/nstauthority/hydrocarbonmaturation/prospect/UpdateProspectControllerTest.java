package uk.co.nstauthority.hydrocarbonmaturation.prospect;

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
import static uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter.emptyBindingResult;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.InformationSource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;


@ContextConfiguration(classes = UpdateProspectController.class)
class UpdateProspectControllerTest extends AbstractControllerTest {

  private static final ServiceUserDetail NSTA_USER = ServiceUserDetailTestUtil.Builder().build();

  @MockBean
  private UpdateProspectService updateProspectService;

  private static SubmissionDetail submissionDetail;
  private static UUID submissionDetailId;

  @BeforeAll
  static void setup() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    submissionDetailId = submissionDetail.getId();
  }

  @Test
  void getUpdatePage_assertModelProperties() throws Exception {
    var form = new UpdateProspectForm();

    var modelAndView = mockMvc.perform(
        get(ReverseRouter.route(on(UpdateProspectController.class).getUpdatePage(null)))
            .flashAttr("form", form)
            .with(user(NSTA_USER)))
        .andReturn()
        .getModelAndView();

    assert modelAndView != null;
    assertThat(modelAndView.getModel())
        .extracting(
            "backLinkUrl",
            "actionUrl",
            "informationSources",
            "form",
            "prospectApiEndpoint")
        .containsExactly(ReverseRouter.route(on(StartUpdateProspectController.class).getStartUpdatePage()),
            ReverseRouter.route(on(UpdateProspectController.class).updateProspect(null, emptyBindingResult())),
            InformationSource.getInformationSource(),
            form,
            SearchSelectorService.route(on(ProspectRestController.class).searchProspects(null)));
  }

  @Test
  void updateProspect_asValidForm_assertRedirect() throws Exception {
    var form = new UpdateProspectForm();

    var bindingResult = new BeanPropertyBindingResult(UpdateProspectForm.class, "form");
    when(updateProspectService.validate(any(), any())).thenReturn(bindingResult);
    when(updateProspectService.createUpdateProspect(form)).thenReturn(submissionDetail);

    mockMvc.perform(
        post(ReverseRouter.route(on(UpdateProspectController.class).updateProspect(null, null)))
            .flashAttr("form", form)
            .with(user(NSTA_USER))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(
            TaskListController.class).getTaskList(submissionDetailId))));
  }

  @Test
  void updateProspect_asInvalidForm_assertIsOk() throws Exception {
    var form = new UpdateProspectForm();

    var bindingResult = new BeanPropertyBindingResult(UpdateProspectForm.class, "form");
    bindingResult.addError(new ObjectError("form", "invalid form"));
    when(updateProspectService.validate(any(), any())).thenReturn(bindingResult);

    var modelAndView = mockMvc.perform(
        post(ReverseRouter.route(on(UpdateProspectController.class).updateProspect(null, null)))
            .flashAttr("form", form)
            .with(user(NSTA_USER))
            .with(csrf()))
        .andExpect(status().isOk())
        .andReturn()
        .getModelAndView();

    assert modelAndView != null;
    assertThat(modelAndView.getModel())
        .extracting(
            "backLinkUrl",
            "actionUrl",
            "informationSources",
            "form",
            "prospectApiEndpoint")
        .containsExactly(
            ReverseRouter.route(on(StartUpdateProspectController.class).getStartUpdatePage()),
            ReverseRouter.route(on(UpdateProspectController.class).updateProspect(null, emptyBindingResult())),
            InformationSource.getInformationSource(),
            form,
            SearchSelectorService.route(on(ProspectRestController.class).searchProspects(null))
        );

    verify(updateProspectService, times(1)).validate(any(), any());
    verify(updateProspectService, never()).createUpdateProspect(any());
  }
}
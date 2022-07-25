package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

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

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@WithMockUser
@ContextConfiguration(classes = OpportunityDetailsController.class)
class OpportunityDetailsControllerTest extends AbstractControllerTest {

  @MockBean
  OpportunityDetailsService opportunityDetailsService;

  @Test
  void getOpportunityDetailsPage_assertStatusOk() throws Exception {
    mockMvc.perform(
        get(ReverseRouter.route(on(OpportunityDetailsController.class).getOpportunityDetails())))
        .andExpect(status().isOk());
  }

  @Test
  void saveOpportunityDetailsPage_whenValidForm_assertRedirection() throws Exception {
    var form = new OpportunityDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var modelAndView = new ModelAndView();

    when(opportunityDetailsService.validate(any(), any())).thenReturn(bindingResult);
    when(opportunityDetailsService.getOpportunityDetailsModelAndView(form)).thenReturn(modelAndView);

    mockMvc.perform(
        post(ReverseRouter.route(on(OpportunityDetailsController.class).saveOpportunityDetails(form, bindingResult)))
            .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(WorkAreaController.class).getWorkArea()))); //TODO redirect to task list

    verify(opportunityDetailsService, times(1)).saveOpportunityDetails(any(OpportunityDetailsForm.class));
  }

  @Test
  void saveOpportunityDetailsPage_whenInvalidForm_assertStatusOk() throws Exception {
    var form = new OpportunityDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var modelAndView = new ModelAndView();
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(opportunityDetailsService.validate(any(), any())).thenReturn(bindingResult);
    when(opportunityDetailsService.getOpportunityDetailsModelAndView(any(OpportunityDetailsForm.class))).thenReturn(modelAndView);

    mockMvc.perform(
        post(ReverseRouter.route(on(OpportunityDetailsController.class).saveOpportunityDetails(form, bindingResult)))
            .with(csrf()))
        .andExpect(status().isOk());

    verify(opportunityDetailsService, never()).saveOpportunityDetails(any(OpportunityDetailsForm.class));
  }

}
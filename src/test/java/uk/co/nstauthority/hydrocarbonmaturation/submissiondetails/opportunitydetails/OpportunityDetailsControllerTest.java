package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@WebMvcTest
@ContextConfiguration(classes = OpportunityDetailsController.class)
class OpportunityDetailsControllerTest extends AbstractControllerTest {

  @MockBean
  OpportunityDetailsService opportunityDetailsService;

  @Test
  void getOpportunityDetailsPage_assertStatusOk() throws Exception {
    mockMvc.perform(
        get(ReverseRouter.route(on(OpportunityDetailsController.class).getOpportunityDetailsPage())))
        .andExpect(status().isOk());
  }

  @Test
  void saveOpportunityDetailsPage_whenValidForm_assertRedirection() throws Exception {
    //TODO when task list is created assert redirection

    var form = new OpportunityDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var modelAndView = new ModelAndView();

    when(opportunityDetailsService.validate(any(), any())).thenReturn(bindingResult);
    when(opportunityDetailsService.getOpportunityDetailsModelAndView(form)).thenReturn(modelAndView);

    mockMvc.perform(
        post(ReverseRouter.route(on(OpportunityDetailsController.class).saveOpportunityDetailsPage(form, bindingResult))))
        .andExpect(status().isOk());
  }

  @Test
  void saveOpportunityDetailsPage_whenInValidForm_assertStatusOk() throws Exception {
    var form = new OpportunityDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var modelAndView = new ModelAndView();
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));

    when(opportunityDetailsService.validate(any(), any())).thenReturn(bindingResult);
    when(opportunityDetailsService.getOpportunityDetailsModelAndView(any(OpportunityDetailsForm.class))).thenReturn(modelAndView);

    mockMvc.perform(
        post(ReverseRouter.route(on(OpportunityDetailsController.class).saveOpportunityDetailsPage(form, bindingResult))))
        .andExpect(status().isOk());
  }

}
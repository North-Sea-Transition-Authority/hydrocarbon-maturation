package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Objects;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.StartProspectController;

@WithMockUser
@ContextConfiguration(classes = GeneralDetailsController.class)
class GeneralDetailsControllerTest extends AbstractControllerTest {

  @MockBean
  GeneralDetailsService generalDetailsService;

  private static GeneralDetailsForm form;
  private static BindingResult bindingResult;

  @BeforeAll
  static void setup() {
    form = new GeneralDetailsForm();
    bindingResult = new BeanPropertyBindingResult(form, "form");
  }

  @Test
  void getGeneralDetailsPage_assertStatusOk() throws Exception {
    mockMvc.perform(
        get(ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetailsPage())))
        .andExpect(status().isOk())
        .andExpect(view().name("hm/submissiondetails/generalDetails"));
  }

  @Test
  void saveGeneralDetails_whenValidForm_assertRedirection() throws Exception {
    //TODO when task list is created assert redirection
    when(generalDetailsService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(GeneralDetailsController.class).saveGeneralDetails(form, bindingResult)))
            .with(csrf()))
        .andExpect(status().isOk());
  }

  @Test
  void saveGeneralDetails_whenInValidForm_assertStatusOk() throws Exception {
    bindingResult.addError(new FieldError("Error", "ErrorMessage", "default message"));
    when(generalDetailsService.validate(any(), any())).thenReturn(bindingResult);

    mockMvc.perform(
        post(ReverseRouter.route(on(GeneralDetailsController.class).saveGeneralDetails(form, bindingResult)))
            .with(csrf()))
        .andExpect(status().isOk());
  }

  @Test
  void getGeneralDetailsModelAndView_assertModelProperties() throws Exception {
    var modelMap =     Objects.requireNonNull(mockMvc.perform(
        get(ReverseRouter.route(on(GeneralDetailsController.class).getGeneralDetailsPage())))
        .andReturn()
        .getModelAndView()).getModelMap();

    assertEquals(OnshoreOffshore.getOnshoreOffshore(), modelMap.get("onshoreOffshore"));
    assertEquals(InformationSource.getInformationSource(), modelMap.get("informationSources"));
    assertEquals(GeneralDetailsForm.class, modelMap.get("form").getClass());
    assertEquals("#", modelMap.get("actionUrl"));
    assertEquals(ReverseRouter.route(on(StartProspectController.class).getStartPage()), modelMap.get("backLinkUrl"));
  }
}
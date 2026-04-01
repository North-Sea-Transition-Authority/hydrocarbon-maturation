package uk.co.nstauthority.hydrocarbonmaturation.feedback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static uk.co.nstauthority.hydrocarbonmaturation.authentication.TestUserProvider.user;

import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@ContextConfiguration(classes = FeedbackController.class)
class FeedbackControllerTest extends AbstractControllerTest {

  private final static User USER = UserTestUtil.Builder().build();

  @MockitoBean
  private RemiFeedbackService remiFeedbackService;

  @SecurityTest
  void getFeedback_whenNotLoggedIn() throws Exception {
    mockMvc.perform(
            get(ReverseRouter.route(on(FeedbackController.class).getFeedback(null))))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void getFeedback_assertModelProperties() throws Exception {
    var modelMap = Objects.requireNonNull(mockMvc.perform(
        get(ReverseRouter.route(on(FeedbackController.class).getFeedback(null)))
            .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/feedback/feedback"))
        .andExpect(model().attributeExists("form"))
        .andReturn().getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("actionUrl", ReverseRouter.route(on(FeedbackController.class)
                    .submitFeedback(null, null, null, null))),
            entry("serviceRatings", DisplayableEnumOptionUtil.getDisplayableOptions(ServiceFeedbackRating.class)),
            entry("cancelUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)))
        );
  }

  @Test
  void submitFeedback_assertRedirect() throws Exception{
    when(remiFeedbackService.validateFeedbackForm(any(), any()))
        .thenReturn(ReverseRouter.emptyBindingResult());
    mockMvc.perform(
            post(ReverseRouter.route(on(FeedbackController.class).getFeedback(null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null))));

    verify(remiFeedbackService, times(1)).saveFeedback(any(FeedbackForm.class), eq(USER));
  }
}
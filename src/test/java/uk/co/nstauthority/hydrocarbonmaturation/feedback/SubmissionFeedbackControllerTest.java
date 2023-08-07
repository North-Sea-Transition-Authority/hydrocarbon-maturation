package uk.co.nstauthority.hydrocarbonmaturation.feedback;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import uk.co.nstauthority.hydrocarbonmaturation.AbstractSubmissionControllerTest;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecurityTest;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;


@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = SubmissionFeedbackController.class)
class SubmissionFeedbackControllerTest extends AbstractSubmissionControllerTest {

  @MockBean
  private RemiFeedbackService remiFeedbackService;

  @BeforeEach
  void setUp() {
    when(submissionDetailService.getSubmissionDetailForIdOrError(SUBMISSION_DETAIL_ID))
        .thenReturn(submissionDetail);
  }

  @Test
  void getFeedback_assertModelProperties() throws Exception{
    var modelMap = Objects.requireNonNull(mockMvc.perform(
            get(ReverseRouter.route(on(SubmissionFeedbackController.class)
                .getFeedback(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER)))
        .andExpect(status().isOk())
        .andExpect(view().name("remi/feedback/feedback"))
        .andExpect(model().attributeExists("form"))
        .andReturn().getModelAndView()).getModelMap();

    assertThat(modelMap)
        .contains(
            entry("actionUrl", ReverseRouter.route(on(SubmissionFeedbackController.class)
                .submitFeedback(SUBMISSION_DETAIL_ID, null, null, null))),
            entry("serviceRatings", DisplayableEnumOptionUtil.getDisplayableOptions(ServiceFeedbackRating.class)),
            entry("cancelUrl", ReverseRouter.route(on(ReviewSubmissionController.class)
                .showReviewPage(submissionDetail.getSubmission().getResource().getId())))
        );
  }

  @SecurityTest
  void getFeedback_whenNotResponsibleTeam() throws Exception{
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);

    mockMvc.perform(
            get(ReverseRouter.route(on(SubmissionFeedbackController.class)
                .getFeedback(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER)))
        .andExpect(status().isForbidden());
  }

  @SecurityTest
  void submitFeedback_whenNotResponsibleTeam() throws Exception{
    setUpWhenUserIsNotResponsibleTeamOrOrganisationSubmitter_failSecurity(USER);

    mockMvc.perform(
            post(ReverseRouter.route(on(SubmissionFeedbackController.class)
                .getFeedback(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().isForbidden());

    verifyNoInteractions(remiFeedbackService);
  }

  @Test
  void submitFeedback_assertRedirect() throws Exception{
    when(remiFeedbackService.validateFeedbackForm(any(), any()))
        .thenReturn(ReverseRouter.emptyBindingResult());
    mockMvc.perform(
            post(ReverseRouter.route(on(SubmissionFeedbackController.class)
                .getFeedback(SUBMISSION_DETAIL_ID, null)))
                .with(user(USER))
                .with(csrf()))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null))));

    verify(remiFeedbackService, times(1))
        .saveFeedback(eq(submissionDetail), any(FeedbackForm.class), eq(USER));
  }
}
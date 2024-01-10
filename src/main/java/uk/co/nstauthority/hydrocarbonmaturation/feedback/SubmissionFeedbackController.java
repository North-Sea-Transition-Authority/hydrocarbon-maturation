package uk.co.nstauthority.hydrocarbonmaturation.feedback;


import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/feedback")
@IsFromResponsibleTeam
public class SubmissionFeedbackController {

  private final RemiFeedbackService remiFeedbackService;
  private final ControllerHelperService controllerHelperService;
  private final UserService userService;
  private final SubmissionDetailService submissionDetailService;

  @Autowired
  public SubmissionFeedbackController(RemiFeedbackService remiFeedbackService,
                                      ControllerHelperService controllerHelperService,
                                      UserService userService,
                                      SubmissionDetailService submissionDetailService) {
    this.remiFeedbackService = remiFeedbackService;
    this.controllerHelperService = controllerHelperService;
    this.userService = userService;
    this.submissionDetailService = submissionDetailService;
  }

  @GetMapping
  public ModelAndView getFeedback(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                  @ModelAttribute("form") FeedbackForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getFeedbackModelAndView(form, submissionDetail);
  }

  @PostMapping
  public ModelAndView submitFeedback(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                     @ModelAttribute("form") FeedbackForm form,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    bindingResult = remiFeedbackService.validateFeedbackForm(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getFeedbackModelAndView(form, submissionDetail),
        () -> {
          remiFeedbackService.saveFeedback(submissionDetail, form, userService.getUser());
          NotificationBannerUtil.addSuccessNotification(
              redirectAttributes, "Success", "Your feedback has been submitted");
          return ReverseRouter.redirect(on(WorkAreaController.class).getWorkArea(null));
        }
    );
  }

  private ModelAndView getFeedbackModelAndView(FeedbackForm feedbackForm, SubmissionDetail submissionDetail) {
    return new ModelAndView("remi/feedback/feedback")
        .addObject("form", feedbackForm)
        .addObject("actionUrl", ReverseRouter.route(on(SubmissionFeedbackController.class)
            .submitFeedback(submissionDetail.getId(), null, null, null)))
        .addObject("cancelUrl", ReverseRouter.route(on(ReviewSubmissionController.class)
                .showReviewPage(submissionDetail.getSubmission().getResource().getId())))
        .addObject("serviceRatings",
            DisplayableEnumOptionUtil.getDisplayableOptions(ServiceFeedbackRating.class));
  }
}
package uk.co.nstauthority.hydrocarbonmaturation.feedback;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@Controller
@RequestMapping("/feedback")
@AccessibleByServiceUsers
public class FeedbackController {

  private final RemiFeedbackService remiFeedbackService;
  private final ControllerHelperService controllerHelperService;
  private final UserDetailService userDetailService;

  @Autowired
  public FeedbackController(RemiFeedbackService remiFeedbackService,
                            ControllerHelperService controllerHelperService,
                            UserDetailService userDetailService) {
    this.remiFeedbackService = remiFeedbackService;
    this.controllerHelperService = controllerHelperService;
    this.userDetailService = userDetailService;
  }

  @GetMapping
  public ModelAndView getFeedback(@ModelAttribute("form") FeedbackForm form) {
    return getFeedbackModelAndView(form);
  }

  @PostMapping
  public ModelAndView submitFeedback(@ModelAttribute("form") FeedbackForm form,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes) {

    bindingResult = remiFeedbackService.validateFeedbackForm(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getFeedbackModelAndView(form),
        () -> {
          remiFeedbackService.saveFeedback(form, userDetailService.getUserDetail());
          NotificationBannerUtil.addSuccessNotification(
              redirectAttributes, "Success", "Your feedback has been submitted");
          return ReverseRouter.redirect(on(WorkAreaController.class).getWorkArea(null));
        }
    );
  }

  private ModelAndView getFeedbackModelAndView(FeedbackForm feedbackForm) {
    return new ModelAndView("remi/feedback/feedback")
        .addObject("form", feedbackForm)
        .addObject("actionUrl", ReverseRouter.route(on(FeedbackController.class)
            .submitFeedback(null, null, null)))
        .addObject("cancelUrl", ReverseRouter.route(on(WorkAreaController.class).getWorkArea(null)))
        .addObject("serviceRatings",
            DisplayableEnumOptionUtil.getDisplayableOptions(ServiceFeedbackRating.class));
  }
}

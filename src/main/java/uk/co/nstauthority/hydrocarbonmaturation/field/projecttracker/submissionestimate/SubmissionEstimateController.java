package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimate;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsExtantProject;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsRegulatorForProjectField;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectTrackerControllerService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ViewProjectController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@Controller
@RequestMapping("/view-resource-record/{resourceId}/project-tracker/{projectId}")
@IsExtantProject
@IsRegulatorForProjectField
class SubmissionEstimateController {
  static final String ESTIMATED_CSR = "Estimated CSR first draft submitted date";
  public static final String CSR_ESTIMATE_DATE = "estimated CSR first draft submission date";

  private final ProjectTrackerControllerService projectTrackerControllerService;
  private final SubmissionEstimateFormValidator submissionEstimateFormValidator;
  private final SubmissionEstimateService submissionEstimateService;

  @Autowired
  SubmissionEstimateController(ProjectTrackerControllerService projectTrackerControllerService,
                               SubmissionEstimateFormValidator submissionEstimateFormValidator,
                               SubmissionEstimateService submissionEstimateService) {
    this.projectTrackerControllerService = projectTrackerControllerService;
    this.submissionEstimateFormValidator = submissionEstimateFormValidator;
    this.submissionEstimateService = submissionEstimateService;
  }

  @GetMapping("/csr-submission-estimates")
  ModelAndView getCsrSubmissionEstimate(@PathVariable("resourceId") UUID resourceId,
                                        @PathVariable("projectId") UUID projectId) {
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    return getSubmissionEstimateModelAndView(ESTIMATED_CSR, project, new SubmissionEstimateForm());
  }

  @PostMapping("/csr-submission-estimates")
  ModelAndView saveCsrSubmissionEstimate(@PathVariable("resourceId") UUID resourceId,
                                         @PathVariable("projectId") UUID projectId,
                                         @ModelAttribute("form") SubmissionEstimateForm form,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes) {
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    if (submissionEstimateFormValidator.isValid(form, bindingResult)) {
      submissionEstimateService.saveSubmissionEstimateFromForm(form, project, SubmissionEstimateType.CSR);

      NotificationBannerUtil.addSuccessNotification(
          redirectAttributes, "Success", "Saved estimated CSR first draft information");

      return ReverseRouter.redirect(on(ViewProjectController.class)
          .getViewProject(resourceId, projectId, null));
    }

    return getSubmissionEstimateModelAndView(ESTIMATED_CSR, project, form);
  }

  @GetMapping("/{submissionEstimateId}/delete")
  ModelAndView getRemoveConfirmation(@PathVariable("resourceId") UUID resourceId,
                                     @PathVariable("projectId") UUID projectId,
                                     @PathVariable("submissionEstimateId") UUID submissionEstimateId) {
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    var estimates = submissionEstimateService.getSubmissionEstimate(submissionEstimateId)
        .orElseThrow(() -> new RemiEntityNotFoundException("There is no submission estimate with id %s"
            .formatted(submissionEstimateId)));

    if (!estimates.getProject().getId().equals(project.getId())) {
      throw new RemiEntityNotFoundException("There is no submissionEstimate with id %s for project with id %s"
          .formatted(submissionEstimateId, projectId));
    }

    return new ModelAndView("remi/field/projecttracker/removeEstimateConfirmation")
        .addObject("pageTitle", "Are you sure you want to remove this " + CSR_ESTIMATE_DATE + "?")
        .addObject("pageCaption", project.getProjectName())
        .addObject("estimates", SubmissionEstimateView.from(estimates))
        .addObject("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
            .getViewProject(project.getField().getResource().getId(), project.getId(), null)));
  }

  @PostMapping("/{submissionEstimateId}/delete")
  ModelAndView removeEstimate(@PathVariable("resourceId") UUID resourceId,
                              @PathVariable("projectId") UUID projectId,
                              @PathVariable("submissionEstimateId") UUID submissionEstimateId,
                              RedirectAttributes redirectAttributes) {
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    var submissionEstimate = submissionEstimateService.getSubmissionEstimate(submissionEstimateId)
        .orElseThrow(() -> new RemiEntityNotFoundException("There is no submission estimate with id %s"
            .formatted(submissionEstimateId)));

    if (!submissionEstimate.getProject().getId().equals(project.getId())) {
      throw new RemiEntityNotFoundException("There is no submissionEstimate with id %s for project with id %s"
          .formatted(submissionEstimateId, projectId));
    }

    submissionEstimateService.removeEstimate(submissionEstimate);

    NotificationBannerUtil.addSuccessNotification(
        redirectAttributes,
        "Success",
        "Removed CSR first draft submission estimate"
    );

    return ReverseRouter.redirect(on(ViewProjectController.class)
        .getViewProject(resourceId, projectId, null));
  }

  private static ModelAndView getSubmissionEstimateModelAndView(String pageTitle,
                                                                Project project,
                                                                SubmissionEstimateForm form) {
    return new ModelAndView("remi/field/projecttracker/addSubmissionEstimate")
        .addObject("pageTitle", pageTitle)
        .addObject("pageCaption", project.getProjectName())
        .addObject("form", form)
        .addObject("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
            .getViewProject(project.getField().getResource().getId(), project.getId(), null)));
  }
}

package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimate;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
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
  static final String CSR_ESTIMATE_TITLE = "Estimated CSR first draft submitted date";
  static final String FDP_ESTIMATE_TITLE = "Estimated FDP first draft submitted date";

  static final String CSR_SUBMITTED_DISPLAY = "estimated CSR first draft submission date";
  static final String FDP_SUBMITTED_DISPLAY = "estimated FDP first draft submission date";

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

  @GetMapping("/estimates")
  ModelAndView getCreateSubmissionEstimate(
      @PathVariable("resourceId") UUID resourceId,
      @PathVariable("projectId") UUID projectId,
      @RequestParam("type") String type) {
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    var submissionEstimateType = parseSubmissionEstimateTypeOrThrow(type);

    var pageTitle = getEstimatePageTitle(submissionEstimateType);

    return getSubmissionEstimateModelAndView(pageTitle, project, new SubmissionEstimateForm());
  }

  @PostMapping("/estimates")
  ModelAndView saveSubmissionEstimate(@PathVariable("resourceId") UUID resourceId,
                                      @PathVariable("projectId") UUID projectId,
                                      @RequestParam("type") String type,
                                      @ModelAttribute("form") SubmissionEstimateForm form,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {
    var submissionEstimateType = parseSubmissionEstimateTypeOrThrow(type);
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    if (submissionEstimateFormValidator.isValid(form, bindingResult)) {
      submissionEstimateService.saveSubmissionEstimateFromForm(form, project, submissionEstimateType);

      NotificationBannerUtil.addSuccessNotification(
          redirectAttributes, "Success", "Saved %s".formatted(getEstimateSubmittedDisplay(submissionEstimateType)));

      return ReverseRouter.redirect(on(ViewProjectController.class)
          .getViewProject(resourceId, projectId, null));
    }

    return getSubmissionEstimateModelAndView(getEstimatePageTitle(submissionEstimateType), project, form);
  }

  @GetMapping("/{submissionEstimateId}/delete")
  ModelAndView getRemoveConfirmation(@PathVariable("resourceId") UUID resourceId,
                                     @PathVariable("projectId") UUID projectId,
                                     @PathVariable("submissionEstimateId") UUID submissionEstimateId,
                                     @RequestParam("type") String type) {
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    var estimates = submissionEstimateService.getSubmissionEstimate(submissionEstimateId)
        .orElseThrow(() -> new RemiEntityNotFoundException("There is no submission estimate with id %s"
            .formatted(submissionEstimateId)));

    if (!estimates.getProject().getId().equals(project.getId())) {
      throw new RemiEntityNotFoundException("There is no submissionEstimate with id %s for project with id %s"
          .formatted(submissionEstimateId, projectId));
    }

    var submissionEstimateType = parseSubmissionEstimateTypeOrThrow(type);

    return new ModelAndView("remi/field/projecttracker/submissionestimates/removeEstimateConfirmation")
        .addObject("pageTitle", "Are you sure you want to remove this " +
            getEstimateSubmittedDisplay(submissionEstimateType) + "?")
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
        "Removed %s".formatted(getEstimateSubmittedDisplay(submissionEstimate.getSubmissionEstimateType()))
    );

    return ReverseRouter.redirect(on(ViewProjectController.class)
        .getViewProject(resourceId, projectId, null));
  }

  private static ModelAndView getSubmissionEstimateModelAndView(String pageTitle,
                                                                Project project,
                                                                SubmissionEstimateForm form) {
    return new ModelAndView("remi/field/projecttracker/submissionestimates/addEstimate")
        .addObject("pageTitle", pageTitle)
        .addObject("pageCaption", project.getProjectName())
        .addObject("form", form)
        .addObject("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
            .getViewProject(project.getField().getResource().getId(), project.getId(), null)));
  }

  private SubmissionEstimateType parseSubmissionEstimateTypeOrThrow(String type) {
    SubmissionEstimateType submissionEstimateType;
    try {
      submissionEstimateType = SubmissionEstimateType.valueOf(type);
      return submissionEstimateType;
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Type %s does not exist".formatted(type));
    }
  }

  private String getEstimatePageTitle(SubmissionEstimateType submissionEstimateType) {
    return switch (submissionEstimateType) {
      case CSR -> CSR_ESTIMATE_TITLE;
      case FDP -> FDP_ESTIMATE_TITLE;
      default -> "";
    };
  }

  private String getEstimateSubmittedDisplay(SubmissionEstimateType submissionEstimateType) {
    return switch (submissionEstimateType) {
      case CSR -> CSR_SUBMITTED_DISPLAY;
      case FDP -> FDP_SUBMITTED_DISPLAY;
      default -> "";
    };
  }
}

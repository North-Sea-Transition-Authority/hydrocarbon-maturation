package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.submissionestimates;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectTrackerControllerService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ViewProjectController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/view-resource-record/{resourceId}/project-tracker/{projectId}")
@HasPermission(permissions = RolePermission.PROJECT_ANALYST)
class SubmissionEstimatesController {
  static final String ESTIMATED_CSR = "Estimated CSR first draft submitted date";

  private final ProjectTrackerControllerService projectTrackerControllerService;
  private final SubmissionEstimatesFormValidator submissionEstimatesFormValidator;
  private final SubmissionEstimatesService submissionEstimatesService;

  @Autowired
  SubmissionEstimatesController(ProjectTrackerControllerService projectTrackerControllerService,
                                SubmissionEstimatesFormValidator submissionEstimatesFormValidator,
                                SubmissionEstimatesService submissionEstimatesService) {
    this.projectTrackerControllerService = projectTrackerControllerService;
    this.submissionEstimatesFormValidator = submissionEstimatesFormValidator;
    this.submissionEstimatesService = submissionEstimatesService;
  }

  @GetMapping("/csr-submission-estimates")
  ModelAndView getCsrSubmissionEstimates(@PathVariable("resourceId") UUID resourceId,
                                         @PathVariable("projectId") UUID projectId) {
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    return getSubmissionEstimatesModelAndView(ESTIMATED_CSR, project, new SubmissionEstimatesForm());
  }

  @PostMapping("/csr-submission-estimates")
  ModelAndView saveCsrSubmissionEstimates(@PathVariable("resourceId") UUID resourceId,
                                          @PathVariable("projectId") UUID projectId,
                                          @ModelAttribute("form") SubmissionEstimatesForm form,
                                          BindingResult bindingResult,
                                          RedirectAttributes redirectAttributes) {
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    if (submissionEstimatesFormValidator.isValid(form, bindingResult)) {
      submissionEstimatesService.saveSubmissionEstimateFromForm(form, project, SubmissionEstimatesType.CSR);

      NotificationBannerUtil.addSuccessNotification(
          redirectAttributes, "Success", "Saved estimated CSR first draft information");

      return ReverseRouter.redirect(on(ViewProjectController.class)
          .getViewProject(resourceId, projectId));
    }

    return getSubmissionEstimatesModelAndView(ESTIMATED_CSR, project, form);
  }

  private static ModelAndView getSubmissionEstimatesModelAndView(String pageTitle,
                                                                 Project project,
                                                                 SubmissionEstimatesForm form) {
    return new ModelAndView("remi/field/projecttracker/addSubmissionEstimates")
        .addObject("pageTitle", pageTitle)
        .addObject("pageCaption", project.getProjectName())
        .addObject("form", form)
        .addObject("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
            .getViewProject(project.getField().getResource().getId(), project.getId())));
  }
}

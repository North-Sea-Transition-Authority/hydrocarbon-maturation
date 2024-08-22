package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.environmentalactivity;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsExtantProject;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectTrackerControllerService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ViewProjectController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Controller
@RequestMapping("/view-resource-record/{resourceId}/project-tracker/{projectId}")
@HasAnyRole(role = Role.PROJECT_ANALYST, teamType = TeamType.REGULATOR)
@IsExtantProject
class EnvironmentalActivityController {
  public static final String PAGE_TITLE = "Environment statement dates";
  private final ProjectTrackerControllerService projectTrackerControllerService;
  private final EnvironmentalActivityService environmentalActivityService;
  private final EnvironmentalActivityFormValidator environmentalActivityFormValidator;

  @Autowired
  EnvironmentalActivityController(ProjectTrackerControllerService projectTrackerControllerService,
                                  EnvironmentalActivityService environmentalActivityService,
                                  EnvironmentalActivityFormValidator environmentalActivityFormValidator) {
    this.projectTrackerControllerService = projectTrackerControllerService;
    this.environmentalActivityService = environmentalActivityService;
    this.environmentalActivityFormValidator = environmentalActivityFormValidator;
  }

  @GetMapping("/environmental-activity")
  public ModelAndView getEnvironmentalActivity(@PathVariable("resourceId") UUID resourceId,
                                               @PathVariable("projectId") UUID projectId) {
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    return getEnvironmentalActivityModelAndView(environmentalActivityService.getEnvironmentalActivityForm(project), project);
  }

  @PostMapping("/environmental-activity")
  public ModelAndView saveEnvironmentalActivity(@PathVariable("resourceId") UUID resourceId,
                                                @PathVariable("projectId") UUID projectId,
                                                @ModelAttribute("form") EnvironmentalActivityForm form,
                                                BindingResult bindingResult,
                                                RedirectAttributes redirectAttributes) {
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    if (environmentalActivityFormValidator.isValid(form, bindingResult)) {
      environmentalActivityService.saveEnvironmentalActivity(form, project);

      NotificationBannerUtil.addSuccessNotification(
          redirectAttributes, "Success", "Saved environmental statement activities");

      return ReverseRouter.redirect(on(ViewProjectController.class)
          .getViewProject(resourceId, projectId, null));
    }

    return getEnvironmentalActivityModelAndView(form, project);
  }

  private ModelAndView getEnvironmentalActivityModelAndView(EnvironmentalActivityForm form, Project project) {
    return new ModelAndView("remi/field/projecttracker/environmentalActivity")
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("pageCaption", project.getProjectName())
        .addObject("form", form)
        .addObject("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
            .getViewProject(project.getField().getResource().getId(), project.getId(), null)));
  }
}

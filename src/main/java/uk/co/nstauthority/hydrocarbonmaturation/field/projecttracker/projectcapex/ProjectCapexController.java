package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectcapex;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsExtantProject;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectTrackerControllerService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ViewProjectController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/view-resource-record/{resourceId}/project-tracker/{projectId}")
@HasPermission(permissions = RolePermission.PROJECT_ANALYST)
@IsExtantProject
class ProjectCapexController {
  public static final String PAGE_TITLE = "Total project CAPEX";

  private final ProjectTrackerControllerService projectTrackerControllerService;
  private final ProjectCapexFormValidator projectCapexFormValidator;

  @Autowired
  ProjectCapexController(ProjectTrackerControllerService projectTrackerControllerService,
                         ProjectCapexFormValidator projectCapexFormValidator) {
    this.projectTrackerControllerService = projectTrackerControllerService;
    this.projectCapexFormValidator = projectCapexFormValidator;
  }

  @GetMapping("/project-capex")
  public ModelAndView getProjectCapex(@PathVariable("resourceId") UUID resourceId,
                                      @PathVariable("projectId") UUID projectId) {
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    return getProjectCapexModelAndView(new ProjectCapexForm(), project);
  }

  @PostMapping("/project-capex")
  public ModelAndView saveProjectCapex(@PathVariable("resourceId") UUID resourceId,
                                       @PathVariable("projectId") UUID projectId,
                                       @ModelAttribute("form") ProjectCapexForm form,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes) {
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    if (projectCapexFormValidator.isValid(form, bindingResult)) {
      NotificationBannerUtil.addSuccessNotification(
          redirectAttributes, "Success", "Saved total project CAPEX");

      return ReverseRouter.redirect(on(ViewProjectController.class)
          .getViewProject(resourceId, projectId, null));
    }

    return getProjectCapexModelAndView(form, project);
  }

  private ModelAndView getProjectCapexModelAndView(ProjectCapexForm form, Project project) {
    return new ModelAndView("remi/field/projecttracker/addProjectCapex")
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("pageCaption", project.getProjectName())
        .addObject("form", form)
        .addObject("projectCapexSourceOptions",
            ProjectCapexSource.getDisplayableOptionsForProjectType(project.getProjectType()))
        .addObject("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
            .getViewProject(project.getField().getResource().getId(), project.getId(), null)));
  }
}

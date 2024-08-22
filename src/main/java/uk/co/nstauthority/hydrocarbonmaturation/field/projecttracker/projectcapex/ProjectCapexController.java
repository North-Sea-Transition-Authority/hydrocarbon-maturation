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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsExtantProject;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectTrackerControllerService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ViewProjectController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Controller
@RequestMapping("/view-resource-record/{resourceId}/project-tracker/{projectId}/project-capex")
@HasAnyRole(role = Role.PROJECT_ANALYST, teamType = TeamType.REGULATOR)
@IsExtantProject
class ProjectCapexController {
  public static final String ADD_PAGE_TITLE = "Total project CAPEX";
  public static final String REMOVE_PAGE_TITLE = "Are you sure you want to remove this total project CAPEX?";

  private final ProjectTrackerControllerService projectTrackerControllerService;
  private final ProjectCapexFormValidator projectCapexFormValidator;
  private final ProjectCapexService projectCapexService;

  @Autowired
  ProjectCapexController(ProjectTrackerControllerService projectTrackerControllerService,
                         ProjectCapexFormValidator projectCapexFormValidator,
                         ProjectCapexService projectCapexService) {
    this.projectTrackerControllerService = projectTrackerControllerService;
    this.projectCapexFormValidator = projectCapexFormValidator;
    this.projectCapexService = projectCapexService;
  }

  @GetMapping
  public ModelAndView getAddProjectCapex(@PathVariable("resourceId") UUID resourceId,
                                         @PathVariable("projectId") UUID projectId) {
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    return getAddProjectCapexModelAndView(new ProjectCapexForm(), project);
  }

  @PostMapping
  public ModelAndView saveProjectCapex(@PathVariable("resourceId") UUID resourceId,
                                       @PathVariable("projectId") UUID projectId,
                                       @ModelAttribute("form") ProjectCapexForm form,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes) {
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    if (projectCapexFormValidator.isValid(form, bindingResult)) {
      projectCapexService.saveNewProjectCapex(form, project);

      NotificationBannerUtil.addSuccessNotification(
          redirectAttributes, "Success", "Saved total project CAPEX");

      return ReverseRouter.redirect(on(ViewProjectController.class)
          .getViewProject(resourceId, projectId, null));
    }

    return getAddProjectCapexModelAndView(form, project);
  }

  @GetMapping("/{projectCapexId}/remove")
  public ModelAndView getRemoveProjectCapex(@PathVariable("resourceId") UUID resourceId,
                                            @PathVariable("projectId") UUID projectId,
                                            @PathVariable("projectCapexId") UUID projectCapexId) {
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    var projectCapexView = projectCapexService.getProjectCapexView(projectCapexId)
        .orElseThrow(() -> new RemiEntityNotFoundException("There is no projectCapex with id %s"
            .formatted(projectCapexId)));

    return new ModelAndView("remi/field/projecttracker/removeProjectCapexConfirmation")
        .addObject("pageTitle", REMOVE_PAGE_TITLE)
        .addObject("pageCaption", project.getProjectName())
        .addObject("projectCapex", projectCapexView)
        .addObject("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
            .getViewProject(project.getField().getResource().getId(), project.getId(), null)));
  }

  @PostMapping("/{projectCapexId}/remove")
  public ModelAndView removeProjectCapex(@PathVariable("resourceId") UUID resourceId,
                                         @PathVariable("projectId") UUID projectId,
                                         @PathVariable("projectCapexId") UUID projectCapexId,
                                         RedirectAttributes redirectAttributes) {
    projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    var projectCapex = projectCapexService.getProjectCapex(projectCapexId)
        .orElseThrow(() -> new RemiEntityNotFoundException("There is no projectCapex with id %s"
            .formatted(projectCapexId)));

    projectCapexService.removeProjectCapex(projectCapex);

    NotificationBannerUtil.addSuccessNotification(
        redirectAttributes, "Success", "Removed total project CAPEX");

    return ReverseRouter.redirect(on(ViewProjectController.class)
        .getViewProject(resourceId, projectId, null));
  }

  private ModelAndView getAddProjectCapexModelAndView(ProjectCapexForm form, Project project) {
    return new ModelAndView("remi/field/projecttracker/addProjectCapex")
        .addObject("pageTitle", ADD_PAGE_TITLE)
        .addObject("pageCaption", project.getProjectName())
        .addObject("form", form)
        .addObject("projectCapexSourceOptions",
            ProjectCapexSource.getDisplayableOptionsForProjectType(project.getProjectType()))
        .addObject("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
            .getViewProject(project.getField().getResource().getId(), project.getId(), null)));
  }
}

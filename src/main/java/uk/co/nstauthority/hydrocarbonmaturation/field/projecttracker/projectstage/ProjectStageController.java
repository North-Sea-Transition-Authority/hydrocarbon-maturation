package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage;

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
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ViewProjectController;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory.ProjectStageHistory;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.projectstagehistory.ProjectStageHistoryService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@Controller
@RequestMapping("/view-resource-record/{resourceId}/project-tracker/{projectId}")
@HasPermission(permissions = RolePermission.PROJECT_ANALYST)
@IsExtantProject
class ProjectStageController {
  static final String PROJECT_STAGE_TITLE = "Project stage";
  static final String REMOVE_PROJECT_STAGE_TITLE = "Are you sure you want to remove this project stage entry?";
  public static final String NO_RESOURCE_FO_PROJECT = "There is no resource with id %s related to the project with id %s";

  private final ProjectService projectService;
  private final ProjectStageService projectStageService;
  private final ProjectStageHistoryService projectStageHistoryService;
  private final ProjectStageFormValidator projectStageFormValidator;

  @Autowired
  ProjectStageController(ProjectStageService projectStageService,
                         ProjectService projectService,
                         ProjectStageHistoryService projectStageHistoryService,
                         ProjectStageFormValidator projectStageFormValidator) {
    this.projectStageService = projectStageService;
    this.projectService = projectService;
    this.projectStageHistoryService = projectStageHistoryService;
    this.projectStageFormValidator = projectStageFormValidator;
  }

  @GetMapping("/project-stage")
  ModelAndView getCreateProjectStageUpdate(@PathVariable("resourceId") UUID resourceId,
                                           @PathVariable("projectId") UUID projectId) {
    var project = getProjectForResourceOrThrow(resourceId, projectId);

    return getCreateProjectStageUpdateModelAndView(new ProjectStageForm(), project);
  }

  @PostMapping("/project-stage")
  ModelAndView saveProjectStageUpdate(@PathVariable("resourceId") UUID resourceId,
                                      @PathVariable("projectId") UUID projectId,
                                      @ModelAttribute("form") ProjectStageForm form,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes) {
    var project = getProjectForResourceOrThrow(resourceId, projectId);

    if (projectStageFormValidator.isValid(form, bindingResult, project)) {
      projectStageService.saveProjectStage(project, form);

      NotificationBannerUtil.addSuccessNotification(
          redirectAttributes, "Success", "Added stage update '%s'"
              .formatted(form.getStage().getDisplayName()));

      return ReverseRouter.redirect(on(ViewProjectController.class)
          .getViewProject(resourceId, projectId, null));
    }

    return getCreateProjectStageUpdateModelAndView(form, project);
  }

  @GetMapping("/project-stage/{projectStageHistoryId}/remove")
  ModelAndView getRemoveProjectStageConfirmation(@PathVariable("resourceId") UUID resourceId,
                                                 @PathVariable("projectId") UUID projectId,
                                                 @PathVariable("projectStageHistoryId") UUID projectStageHistoryId) {
    var projectStageHistory = projectStageHistoryService.getProjectStageHistory(projectStageHistoryId)
        .orElseThrow(() -> new RemiEntityNotFoundException("There is no projectStageHistory with id %s"
            .formatted(projectStageHistoryId)));

    var project = projectStageHistory.getProject();
    if (!project.getId().equals(projectId)) {
      throw new RemiEntityNotFoundException("There is no project with id %s related to the projectStageHistory with id %s"
          .formatted(projectId, projectStageHistoryId));
    }

    if (!project.getField().getResource().getId().equals(resourceId)) {
      throw new RemiEntityNotFoundException(NO_RESOURCE_FO_PROJECT.formatted(resourceId, projectId));
    }

    return new ModelAndView("remi/field/projecttracker/removeProjectStageHistoryConfirmation")
        .addObject("pageTitle", REMOVE_PROJECT_STAGE_TITLE)
        .addObject("pageCaption", project.getProjectName())
        .addObject("projectStageHistory", getProjectStageView(projectStageHistory))
        .addObject("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
            .getViewProject(resourceId, projectId, null)));
  }

  @PostMapping("/project-stage/{projectStageHistoryId}/remove")
  ModelAndView removeProjectStage(@PathVariable("resourceId") UUID resourceId,
                                  @PathVariable("projectId") UUID projectId,
                                  @PathVariable("projectStageHistoryId") UUID projectStageHistoryId,
                                  RedirectAttributes redirectAttributes) {
    var projectStageHistory = projectStageHistoryService.getProjectStageHistory(projectStageHistoryId)
        .orElseThrow(() -> new RemiEntityNotFoundException("There is no projectStageHistory with id %s"
            .formatted(projectStageHistoryId)));

    var project = projectStageHistory.getProject();
    if (!project.getId().equals(projectId)) {
      throw new RemiEntityNotFoundException("There is no project with id %s related to the projectStageHistory with id %s"
          .formatted(projectId, projectStageHistoryId));
    }

    if (!project.getField().getResource().getId().equals(resourceId)) {
      throw new RemiEntityNotFoundException(NO_RESOURCE_FO_PROJECT
          .formatted(resourceId, projectId));
    }

    projectStageHistoryService.removeProjectStage(projectStageHistory);

    NotificationBannerUtil.addSuccessNotification(
        redirectAttributes,
        "Success",
        "Removed stage update '%s' on %s".formatted(
            projectStageHistory.getStage().getDisplayName(),
            DateUtil.formatLongDate(projectStageHistory.getStageDate()))
    );

    return ReverseRouter.redirect(on(ViewProjectController.class)
        .getViewProject(resourceId, projectId, null));
  }

  private ModelAndView getCreateProjectStageUpdateModelAndView(ProjectStageForm projectStageForm, Project project) {
    return new ModelAndView("remi/field/projecttracker/createProjectStageHistory")
        .addObject("pageTitle", PROJECT_STAGE_TITLE)
        .addObject("pageCaption", project.getProjectName())
        .addObject("form", projectStageForm)
        .addObject("projectStageOptions", DisplayableEnumOptionUtil.getDisplayableOptions(ProjectStage.class))
        .addObject("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
            .getViewProject(project.getField().getResource().getId(), project.getId(), null)));
  }

  private Project getProjectForResourceOrThrow(UUID resourceId, UUID projectId) {
    var project = projectService.getProjectOrThrow(projectId);

    if (!resourceId.equals(project.getField().getResource().getId())) {
      throw new RemiEntityNotFoundException(NO_RESOURCE_FO_PROJECT
          .formatted(resourceId, projectId));
    }
    return project;
  }

  private static ProjectStageView getProjectStageView(ProjectStageHistory projectStageHistory) {
    return ProjectStageView.from(projectStageHistory);
  }
}

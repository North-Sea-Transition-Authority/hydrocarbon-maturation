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
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ViewProjectController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/view-resource-record/{resourceId}/project-tracker/{projectId}")
@HasPermission(permissions = RolePermission.PROJECT_ANALYST)
class ProjectStageController {
  static final String PAGE_TITLE = "Project stage";
  private final ProjectService projectService;
  private final ProjectStageService projectStageService;
  private final ProjectStageFormValidator projectStageFormValidator;

  @Autowired
  ProjectStageController(ProjectStageService projectStageService,
                         ProjectService projectService,
                         ProjectStageFormValidator projectStageFormValidator) {
    this.projectStageService = projectStageService;
    this.projectService = projectService;
    this.projectStageFormValidator = projectStageFormValidator;
  }

  @GetMapping("/project-stage")
  ModelAndView getCreateProjectStageUpdate(@PathVariable("resourceId") UUID resourceId,
                                                  @PathVariable("projectId") UUID projectId) {
    var project = projectService.getProjectOrThrow(projectId);

    if (!resourceId.equals(project.getField().getResource().getId())) {
      throw new RemiEntityNotFoundException("There is no resource with id %s related to the project with id %s"
          .formatted(resourceId, projectId));
    }

    return getCreateProjectStageUpdateModelAndView(new ProjectStageForm(), project);
  }

  @PostMapping("/project-stage")
  ModelAndView saveProjectStageUpdate(@PathVariable("resourceId") UUID resourceId,
                                             @PathVariable("projectId") UUID projectId,
                                             @ModelAttribute("form") ProjectStageForm form,
                                             BindingResult bindingResult,
                                             RedirectAttributes redirectAttributes) {
    var project = projectService.getProjectOrThrow(projectId);

    if (!resourceId.equals(project.getField().getResource().getId())) {
      throw new RemiEntityNotFoundException("There is no resource with id %s related to the project with id %s"
          .formatted(resourceId, projectId));
    }

    if (projectStageFormValidator.isValid(form, bindingResult)) {
      projectStageService.saveProjectStage(project, form);

      NotificationBannerUtil.addSuccessNotification(
          redirectAttributes, "Success", "Added stage update '%s'"
              .formatted(form.getStage().getDisplayName()));

      return ReverseRouter.redirect(on(ViewProjectController.class)
          .getViewProject(resourceId, projectId));
    }

    return getCreateProjectStageUpdateModelAndView(form, project);
  }

  private ModelAndView getCreateProjectStageUpdateModelAndView(ProjectStageForm projectStageForm, Project project) {
    return new ModelAndView("remi/field/projecttracker/createProjectStageHistory")
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("pageCaption", project.getProjectName())
        .addObject("form", projectStageForm)
        .addObject("projectStageOptions", DisplayableEnumOptionUtil.getDisplayableOptions(ProjectStage.class))
        .addObject("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
            .getViewProject(project.getField().getResource().getId(), project.getId())));
  }
}

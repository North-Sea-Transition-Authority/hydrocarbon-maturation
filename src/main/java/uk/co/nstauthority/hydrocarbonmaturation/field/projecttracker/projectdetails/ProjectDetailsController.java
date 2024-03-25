package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails;

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
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.Project;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectForm;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectListController;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectPhase;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectType;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ViewProjectController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/view-resource-record/{resourceId}/project-tracker")
@HasPermission(permissions = RolePermission.PROJECT_ANALYST)
@IsExtantProject
public class ProjectDetailsController {
  public static final String CREATE_PROJECT_TITLE = "Create a new project";
  public static final String UPDATE_PROJECT_TITLE = "Update project";

  private final FieldPersistenceService fieldPersistenceService;
  private final ProjectService projectService;
  private final ProjectFormValidator projectFormValidator;

  @Autowired
  public ProjectDetailsController(FieldPersistenceService fieldPersistenceService,
                                  ProjectService projectService,
                                  ProjectFormValidator projectFormValidator) {
    this.fieldPersistenceService = fieldPersistenceService;
    this.projectService = projectService;
    this.projectFormValidator = projectFormValidator;
  }

  @GetMapping("/create-project")
  public ModelAndView getCreateProject(@PathVariable("resourceId") UUID resourceId) {
    var field = fieldPersistenceService.getFieldOrThrow(resourceId);

    return getCreateProjectModelAndView(new ProjectForm(), field);
  }

  @GetMapping("/{projectId}/update-project")
  public ModelAndView getUpdateProject(@PathVariable("resourceId") UUID resourceId,
                                       @PathVariable("projectId") UUID projectId) {
    var project = projectService.getProjectOrThrow(projectId);

    if (!resourceId.equals(project.getField().getResource().getId())) {
      throw new RemiEntityNotFoundException("There is no resource with id %s related to the project with id %s"
          .formatted(resourceId, projectId));
    }

    return getUpdateProjectModelAndView(projectService.projectToForm(project), project);
  }

  @PostMapping("/create-project")
  public ModelAndView saveCreatedProject(@PathVariable("resourceId") UUID resourceId,
                                         @ModelAttribute("form") ProjectForm form,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes) {
    var field = fieldPersistenceService.getFieldOrThrow(resourceId);

    if (projectFormValidator.isValidNewProject(bindingResult)) {
      var savedProject = projectService.saveNewProject(field, form);

      NotificationBannerUtil.addSuccessNotification(
          redirectAttributes, "Success", "Created project '%s'"
              .formatted(savedProject.getProjectName()));

      return ReverseRouter.redirect(on(ProjectListController.class).getProjectList(resourceId, null));
    }

    return getCreateProjectModelAndView(form, field);
  }

  @PostMapping("{projectId}/update-project")
  public ModelAndView saveUpdatedProject(@PathVariable("resourceId") UUID resourceId,
                                         @PathVariable("projectId") UUID projectId,
                                         @ModelAttribute("form") ProjectForm form,
                                         BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    var project = projectService.getProjectOrThrow(projectId);

    if (!resourceId.equals(project.getField().getResource().getId())) {
      throw new RemiEntityNotFoundException("There is no resource with id %s related to the project with id %s"
          .formatted(resourceId, projectId));
    }

    if (projectFormValidator.isValidProjectUpdate(project, form, bindingResult)) {
      var savedProject = projectService.saveProjectUpdate(project, form);

      NotificationBannerUtil.addSuccessNotification(
          redirectAttributes, "Success", "Updated project details for '%s'"
              .formatted(savedProject.getProjectName()));

      return ReverseRouter.redirect(on(ViewProjectController.class).getViewProject(resourceId, projectId, null));
    }

    return getUpdateProjectModelAndView(form, project);
  }

  private ModelAndView getCommonModelAndView(ProjectForm form, Field field) {
    return new ModelAndView("remi/field/projecttracker/projectDetails")
        .addObject("pageCaption", field.getCorrectedLegalFieldName())
        .addObject("form", form)
        .addObject("projectTypeOptions", DisplayableEnumOptionUtil.getDisplayableOptions(ProjectType.class))
        .addObject("projectStatusOptions", DisplayableEnumOptionUtil.getDisplayableOptions(ProjectPhase.class))
        .addObject("acceptedSubmissionOptions", projectService.getAcceptedSubmissionsMapForField(field));
  }

  private ModelAndView getCreateProjectModelAndView(ProjectForm form, Field field) {
    return getCommonModelAndView(form, field)
        .addObject("pageTitle", CREATE_PROJECT_TITLE)
        .addObject("cancelUrl",
            ReverseRouter.route(on(ProjectListController.class).getProjectList(field.getResource().getId(), null)))
        .addObject("isUpdate", false);
  }

  private ModelAndView getUpdateProjectModelAndView(ProjectForm form, Project project) {
    return getCreateProjectModelAndView(form, project.getField())
        .addObject("pageTitle", UPDATE_PROJECT_TITLE)
        .addObject("cancelUrl",
            ReverseRouter.route(on(ViewProjectController.class)
                .getViewProject(project.getField().getResource().getId(), project.getId(), null)))
        .addObject("isUpdate", true)
        .addObject("projectType", project.getProjectType());
  }
}

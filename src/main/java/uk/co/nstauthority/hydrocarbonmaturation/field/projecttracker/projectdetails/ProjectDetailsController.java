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
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectForm;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectFormValidator;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectListController;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/view-resource-record/{resourceId}/project-tracker")
@HasPermission(permissions = RolePermission.PROJECT_ANALYST)
public class ProjectDetailsController {
  public static final String PAGE_TITLE = "Create a new project";

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

  @PostMapping("/create-project")
  public ModelAndView saveCreatedProject(@PathVariable("resourceId") UUID resourceId,
                                         @ModelAttribute("form") ProjectForm form,
                                         BindingResult bindingResult,
                                         RedirectAttributes redirectAttributes) {
    var field = fieldPersistenceService.getFieldOrThrow(resourceId);

    if (projectFormValidator.isValid(bindingResult)) {
      var savedProject = projectService.saveNewProject(field, form);

      NotificationBannerUtil.addSuccessNotification(
          redirectAttributes, "Success", "Created project '%s'"
              .formatted(savedProject.getProjectName()));

      return ReverseRouter.redirect(on(ProjectListController.class).getProjectList(resourceId, null));
    }

    return getCreateProjectModelAndView(form, field);
  }

  private ModelAndView getCreateProjectModelAndView(ProjectForm form, Field field) {
    return new ModelAndView("remi/field/projecttracker/projectDetails")
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("pageCaption", field.getCorrectedLegalFieldName())
        .addObject("form", form)
        .addObject("projectTypeOptions", DisplayableEnumOptionUtil.getDisplayableOptions(ProjectType.class))
        .addObject("projectStatusOptions", DisplayableEnumOptionUtil.getDisplayableOptions(ProjectStatus.class))
        .addObject("acceptedSubmissionOptions", projectService.getAcceptedSubmissionsMapForField(field))
        .addObject("cancelUrl",
            ReverseRouter.route(on(ProjectListController.class).getProjectList(field.getResource().getId(), null)));
  }
}

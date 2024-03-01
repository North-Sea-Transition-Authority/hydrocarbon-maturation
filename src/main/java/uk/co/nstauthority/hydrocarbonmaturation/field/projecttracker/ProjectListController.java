package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByRegulatorUsers;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbItem;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectDetailsController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchFieldRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/view-resource-record/{resourceId}/project-tracker")
@AccessibleByRegulatorUsers
public class ProjectListController {
  static final String PAGE_TITLE = "Project tracker";

  private final FieldPersistenceService fieldPersistenceService;
  private final ProjectService projectService;
  private final PermissionService permissionService;

  @Autowired
  public ProjectListController(FieldPersistenceService fieldPersistenceService,
                               ProjectService projectService,
                               PermissionService permissionService) {
    this.fieldPersistenceService = fieldPersistenceService;
    this.projectService = projectService;
    this.permissionService = permissionService;
  }

  @GetMapping
  public ModelAndView getProjectList(@PathVariable("resourceId") UUID resourceId, User user) {
    var field = fieldPersistenceService.getFieldOrThrow(resourceId);

    var modelAndView = new ModelAndView("remi/field/projecttracker/projectList")
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("pageCaption", field.getCorrectedLegalFieldName())
        .addObject("projectViews", projectService.getProjectListViews(field));

    addProjectAnalystObjects(resourceId, user, modelAndView);
    addBreadCrumbs(resourceId, field, modelAndView);

    return modelAndView;
  }

  private void addProjectAnalystObjects(UUID resourceId, User user, ModelAndView modelAndView) {
    if (permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST))) {
      modelAndView
          .addObject("createProjectUrl",
              ReverseRouter.route(on(ProjectDetailsController.class).getCreateProject(resourceId)));
    }
  }

  private void addBreadCrumbs(UUID resourceId, Field field, ModelAndView modelAndView) {
    var breadcrumbs = Breadcrumbs.builder(PAGE_TITLE)
        .addBreadcrumb(new BreadcrumbItem(
            SearchFieldRecordsController.PAGE_TITLE,
            ReverseRouter.route(on(SearchFieldRecordsController.class).searchResourceRecords(null, null))
        ))
        .addBreadcrumb(new BreadcrumbItem(
            field.getCorrectedLegalFieldName(),
            ReverseRouter.route(on(ViewResourceRecordController.class)
                .showResourceRecord(resourceId))
        ))
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);
  }
}

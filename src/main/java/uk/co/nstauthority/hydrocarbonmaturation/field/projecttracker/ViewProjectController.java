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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsExtantProject;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.PermissionService;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbItem;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchFieldRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;

@Controller
@RequestMapping("/view-resource-record/{resourceId}/project-tracker")
@AccessibleByRegulatorUsers
@IsExtantProject
public class ViewProjectController {
  private final ProjectService projectService;
  private final ProjectSummaryService projectSummaryService;
  private final PermissionService permissionService;

  @Autowired
  public ViewProjectController(ProjectService projectService,
                               ProjectSummaryService projectSummaryService,
                               PermissionService permissionService) {
    this.projectService = projectService;
    this.projectSummaryService = projectSummaryService;
    this.permissionService = permissionService;
  }

  @GetMapping("/{projectId}")
  public ModelAndView getViewProject(@PathVariable("resourceId") UUID resourceId,
                                     @PathVariable("projectId") UUID projectId,
                                     User user) {
    var project = projectService.getProjectOrThrow(projectId);

    if (!resourceId.equals(project.getField().getResource().getId())) {
      throw new RemiEntityNotFoundException("There is no resource with id %s related to the project with id %s"
          .formatted(resourceId, projectId));
    }

    return getViewProjectModelAndView(project, user);
  }

  private ModelAndView getViewProjectModelAndView(Project project, User user) {
    var summarySections = projectSummaryService.getSummarySections(project);

    var modelAndView = new ModelAndView("remi/field/projecttracker/viewProject")
        .addObject("pageTitle", project.getProjectName())
        .addObject("pageCaption", project.getField().getCorrectedLegalFieldName())
        .addObject("summarySections", summarySections)
        .addObject("accordionId", project.getId().toString());

    if (permissionService.hasPermission(user, Set.of(RolePermission.PROJECT_ANALYST))) {
      modelAndView.addObject(
          "deleteUrl",
          ReverseRouter.route(on(DeleteProjectController.class)
              .getDeleteProjectConfirmation(project.getField().getResource().getId(), project.getId()))
      );
    }

    addViewProjectBreadcrumbs(project, modelAndView);

    return modelAndView;
  }

  private void addViewProjectBreadcrumbs(Project project, ModelAndView modelAndView) {
    // breadcrumbs = Search field records / {Field name} / Project tracker / {Project name}
    var breadcrumbs = Breadcrumbs.builder(project.getProjectName())
        .addBreadcrumb(new BreadcrumbItem(
            SearchFieldRecordsController.PAGE_TITLE,
            ReverseRouter.route(on(SearchFieldRecordsController.class).searchResourceRecords(null, null))
        ))
        .addBreadcrumb(new BreadcrumbItem(
            project.getField().getCorrectedLegalFieldName(),
            ReverseRouter.route(on(ViewResourceRecordController.class)
                .showResourceRecord(project.getField().getResource().getId()))
        ))
        .addBreadcrumb(new BreadcrumbItem(
            ProjectListController.PAGE_TITLE,
            ReverseRouter.route(on(ProjectListController.class).getProjectList(project.getField().getResource().getId(), null))
        ))
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);
  }
}

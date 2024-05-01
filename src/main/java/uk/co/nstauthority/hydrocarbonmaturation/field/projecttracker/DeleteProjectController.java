package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsExtantProject;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectdetails.ProjectDetailsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;

@Controller
@RequestMapping("/view-resource-record/{resourceId}/project-tracker/{projectId}/delete")
@HasPermission(permissions = RolePermission.PROJECT_ANALYST)
@IsExtantProject
class DeleteProjectController {
  public static final String DELETE_CONFIRMATION_TITLE = "Are you sure you want to delete this project?";

  private final ProjectTrackerControllerService projectTrackerControllerService;
  private final ProjectDetailsSummaryService projectDetailsSummaryService;
  private final ProjectService projectService;

  @Autowired
  DeleteProjectController(ProjectTrackerControllerService projectTrackerControllerService,
                          ProjectDetailsSummaryService projectDetailsSummaryService,
                          ProjectService projectService) {
    this.projectTrackerControllerService = projectTrackerControllerService;
    this.projectDetailsSummaryService = projectDetailsSummaryService;
    this.projectService = projectService;
  }

  @GetMapping
  public ModelAndView getDeleteProjectConfirmation(@PathVariable("resourceId") UUID resourceId,
                                                   @PathVariable("projectId") UUID projectId) {
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    var summaryCard = projectDetailsSummaryService.getProjectSummaryCard(project);

    return new ModelAndView("remi/field/projecttracker/deleteProjectConfirmation")
        .addObject("pageTitle", DELETE_CONFIRMATION_TITLE)
        .addObject("pageCaption", project.getProjectName())
        .addObject("summaryCard", summaryCard)
        .addObject("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
            .getViewProject(resourceId, projectId, null)));
  }

  @PostMapping
  public ModelAndView deleteProject(@PathVariable("resourceId") UUID resourceId,
                                    @PathVariable("projectId") UUID projectId,
                                    RedirectAttributes redirectAttributes) {
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    projectService.deleteProject(project);

    NotificationBannerUtil.addSuccessNotification(
        redirectAttributes, "Success", "Project '%s' has been deleted"
            .formatted(project.getProjectName()));

    return ReverseRouter.redirect(on(ProjectListController.class)
        .getProjectList(resourceId, null));
  }
}

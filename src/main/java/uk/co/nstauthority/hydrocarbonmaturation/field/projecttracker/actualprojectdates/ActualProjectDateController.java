package uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.actualprojectdates;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
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
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@Controller
@RequestMapping("/view-resource-record/{resourceId}/project-tracker/{projectId}")
@IsExtantProject
@HasAnyRole(role = Role.PROJECT_ANALYST, teamType = TeamType.REGULATOR)
class ActualProjectDateController {
  private final ProjectTrackerControllerService projectTrackerControllerService;
  private final ActualProjectDateFormValidator actualProjectDateFormValidator;
  private final ActualProjectDateService actualProjectDateService;

  @Autowired
  ActualProjectDateController(ProjectTrackerControllerService projectTrackerControllerService,
                              ActualProjectDateFormValidator actualProjectDateFormValidator,
                              ActualProjectDateService actualProjectDateService
  ) {
    this.projectTrackerControllerService = projectTrackerControllerService;
    this.actualProjectDateFormValidator = actualProjectDateFormValidator;
    this.actualProjectDateService = actualProjectDateService;
  }

  @GetMapping("/actual-dates")
  ModelAndView getCreateProjectDate(
      @PathVariable("resourceId") UUID resourceId,
      @PathVariable("projectId") UUID projectId,
      @RequestParam("type") String type) {
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    var projectDateType = parseProjectDateTypeOrThrow(type);

    var pageTitle = projectDateType.getDisplayName();

    return getProjectDateModelAndView(pageTitle, project, new ActualProjectDateForm());
  }

  @PostMapping("/actual-dates")
  ModelAndView saveProjectDate(@PathVariable("resourceId") UUID resourceId,
                               @PathVariable("projectId") UUID projectId,
                               @RequestParam("type") String type,
                               @ModelAttribute("form") ActualProjectDateForm form,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
    var projectDateType = parseProjectDateTypeOrThrow(type);
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    if (actualProjectDateFormValidator.isValid(form, bindingResult)) {
      actualProjectDateService.saveDateFromForm(form, project, projectDateType);

      NotificationBannerUtil.addSuccessNotification(
          redirectAttributes, "Success", "Saved %s".formatted(projectDateType.getLowercaseFormatDisplayName()));

      return ReverseRouter.redirect(on(ViewProjectController.class)
          .getViewProject(resourceId, projectId, null));
    }

    return getProjectDateModelAndView(projectDateType.getDisplayName(), project, form);
  }

  @GetMapping("/actual-dates/{projectDateId}/delete")
  ModelAndView getRemoveConfirmation(@PathVariable("resourceId") UUID resourceId,
                                     @PathVariable("projectId") UUID projectId,
                                     @PathVariable("projectDateId") UUID projectDateId,
                                     @RequestParam("type") String type) {
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    var projectDate = getProjectDateOrThrow(project, projectDateId);

    var projectDateType = parseProjectDateTypeOrThrow(type);

    return new ModelAndView("remi/field/projecttracker/actualprojectdates/removeActualDate")
        .addObject("pageTitle", "Are you sure you want to remove the " +
            projectDateType.getLowercaseFormatDisplayName() + "?")
        .addObject("pageCaption", project.getProjectName())
        .addObject("projectDate", DateUtil.formatLongDate(projectDate.getDate()))
        .addObject("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
            .getViewProject(project.getField().getResource().getId(), project.getId(), null)));
  }

  @PostMapping("/actual-dates/{projectDateId}/delete")
  ModelAndView removeProjectDate(@PathVariable("resourceId") UUID resourceId,
                                 @PathVariable("projectId") UUID projectId,
                                 @PathVariable("projectDateId") UUID projectDateId,
                                 RedirectAttributes redirectAttributes) {
    var project = projectTrackerControllerService.getProjectForResourceOrThrow(resourceId, projectId);

    var projectDate = getProjectDateOrThrow(project, projectDateId);

    actualProjectDateService.removeDate(projectDate);

    NotificationBannerUtil.addSuccessNotification(
        redirectAttributes,
        "Success",
        "Removed %s".formatted(projectDate.getProjectDateType().getLowercaseFormatDisplayName())
    );

    return ReverseRouter.redirect(on(ViewProjectController.class)
        .getViewProject(resourceId, projectId, null));
  }

  private ActualProjectDate getProjectDateOrThrow(Project project, UUID projectDateId) {
    var projectDate = actualProjectDateService.getDate(projectDateId)
        .orElseThrow(() -> new RemiEntityNotFoundException("There is no project date with id %s"
            .formatted(projectDateId)));

    if (!projectDate.getProject().getId().equals(project.getId())) {
      throw new RemiEntityNotFoundException("There is no project date with id %s for project with id %s"
          .formatted(projectDateId, project.getId()));
    }

    return projectDate;
  }

  private ActualProjectDateType parseProjectDateTypeOrThrow(String type) {
    ActualProjectDateType actualProjectDateType;
    try {
      actualProjectDateType = ActualProjectDateType.valueOf(type);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "There is no project date type %s".formatted(type));
    }
    return actualProjectDateType;
  }

  private static ModelAndView getProjectDateModelAndView(String pageTitle,
                                                         Project project,
                                                         ActualProjectDateForm form) {
    return new ModelAndView("remi/field/projecttracker/actualprojectdates/addActualDate")
        .addObject("pageTitle", pageTitle)
        .addObject("pageCaption", project.getProjectName())
        .addObject("form", form)
        .addObject("cancelUrl", ReverseRouter.route(on(ViewProjectController.class)
            .getViewProject(project.getField().getResource().getId(), project.getId(), null)));
  }
}

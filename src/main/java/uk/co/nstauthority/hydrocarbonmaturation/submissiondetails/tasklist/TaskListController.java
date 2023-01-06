package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.DeleteSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/task-list")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
public class TaskListController {

  private final SubmissionDetailService submissionDetailService;
  private final TaskListService taskListService;
  public static final String PAGE_NAME = "Task list";

  @Autowired
  public TaskListController(SubmissionDetailService submissionDetailService,
                            TaskListService taskListService) {
    this.submissionDetailService = submissionDetailService;
    this.taskListService = taskListService;
  }

  @GetMapping
  public ModelAndView getTaskList(@PathVariable("submissionDetailId") UUID submissionId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionId);
    var sections = taskListService.getAllSections(submissionDetail);

    var modelAndView = new ModelAndView("remi/submissiondetails/tasklist/taskList")
        .addObject("taskListSections", sections)
        .addObject("submissionTypeName", submissionDetailService.getSubmissionTypeName(submissionDetail))
        .addObject("resourceName", submissionDetailService.getResourceNameOrEmpty(submissionDetail))
        .addObject("deleteSubmissionUrl",
            ReverseRouter.route(on(DeleteSubmissionController.class).showDeleteConfirmationPage(submissionId)));

    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(PAGE_NAME)
        .addWorkAreaBreadcrumb()
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);

    return modelAndView;
  }
}

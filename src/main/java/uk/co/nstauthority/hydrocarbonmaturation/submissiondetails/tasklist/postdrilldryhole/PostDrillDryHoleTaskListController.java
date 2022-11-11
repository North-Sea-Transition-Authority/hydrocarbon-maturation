package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/post-drill-dry-hole-task-list")
public class PostDrillDryHoleTaskListController {

  private final SubmissionDetailService submissionDetailService;
  private final TaskListService taskListService;
  public static final String PAGE_NAME = "Task list";

  @Autowired
  PostDrillDryHoleTaskListController(SubmissionDetailService submissionDetailService,
                                     TaskListService taskListService) {
    this.submissionDetailService = submissionDetailService;
    this.taskListService = taskListService;
  }

  @GetMapping
  public ModelAndView getTaskList(@PathVariable("submissionDetailId") UUID submissionId) {

    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionId);

    var sections = taskListService.getAllSections(submissionDetail);

    var modelAndView = new ModelAndView("remi/prospect/tasklist/taskListDryWell")
        .addObject("taskListSections", sections);

    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(PAGE_NAME)
        .addWorkAreaBreadcrumb()
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);

    return modelAndView;
  }
}

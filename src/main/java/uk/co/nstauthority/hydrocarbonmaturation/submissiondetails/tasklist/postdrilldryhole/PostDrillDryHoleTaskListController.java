package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.postdrilldryhole;

import java.util.List;
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
import uk.co.nstauthority.hydrocarbonmaturation.tasklist.TaskListSectionUtil;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/post-drill-dry-hole-task-list")
public class PostDrillDryHoleTaskListController {

  public static final String PAGE_NAME = "Task list";

  private final SubmissionDetailService submissionDetailService;
  private final List<PostDrillDryHoleTaskListSection> postDrillDryHoleTaskListSections;
  private final List<PostDrillDryHoleTaskListItem> postDrillDryHoleTaskListItems;

  @Autowired
  public PostDrillDryHoleTaskListController(SubmissionDetailService submissionDetailService,
                                            List<PostDrillDryHoleTaskListSection> postDrillDryHoleTaskListSections,
                                            List<PostDrillDryHoleTaskListItem> postDrillDryHoleTaskListItems) {
    this.submissionDetailService = submissionDetailService;
    this.postDrillDryHoleTaskListSections = postDrillDryHoleTaskListSections;
    this.postDrillDryHoleTaskListItems = postDrillDryHoleTaskListItems;
  }

  @GetMapping
  public ModelAndView getTaskList(@PathVariable("submissionDetailId") UUID submissionId) {

    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionId);

    var modelAndView = new ModelAndView("remi/prospect/tasklist/taskList")
        .addObject(
            "taskListSections",
            TaskListSectionUtil.createSectionViews(
                postDrillDryHoleTaskListSections,
                postDrillDryHoleTaskListItems,
                new PostDrillDryHoleTaskListItemType(submissionDetail)
            )
        )
        .addObject("submissionTypeName", submissionDetail.getSubmissionType().getTypeName());

    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(PAGE_NAME)
        .addWorkAreaBreadcrumb()
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);

    return modelAndView;
  }
}

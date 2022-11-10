package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.discovery;

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
@RequestMapping("/submission-details/{submissionDetailId}/discovery-task-list")
public class DiscoveryTaskListController {

  public static final String PAGE_NAME = "Task list";

  private final SubmissionDetailService submissionDetailService;
  private final List<DiscoveryTaskListSection> discoveryTaskListSections;
  private final List<DiscoveryTaskListItem> discoveryTaskListItems;

  @Autowired
  public DiscoveryTaskListController(SubmissionDetailService submissionDetailService,
                                    List<DiscoveryTaskListSection> discoveryTaskListSections,
                                    List<DiscoveryTaskListItem> discoveryTaskListItems) {
    this.submissionDetailService = submissionDetailService;
    this.discoveryTaskListSections = discoveryTaskListSections;
    this.discoveryTaskListItems = discoveryTaskListItems;
  }

  @GetMapping
  public ModelAndView getTaskList(@PathVariable("submissionDetailId") UUID submissionId) {

    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionId);

    var modelAndView = new ModelAndView("remi/prospect/tasklist/taskList")
        .addObject(
            "taskListSections",
            TaskListSectionUtil.createSectionViews(
                discoveryTaskListSections,
                discoveryTaskListItems,
                new DiscoveryTaskListItemType(submissionDetail)
            ))
        .addObject("submissionTypeName", submissionDetail.getSubmissionType().getTypeName());

    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(PAGE_NAME)
        .addWorkAreaBreadcrumb()
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);

    return modelAndView;
  }
}

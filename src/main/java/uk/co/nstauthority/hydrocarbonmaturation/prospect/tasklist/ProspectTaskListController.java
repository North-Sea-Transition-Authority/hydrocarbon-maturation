package uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist;

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
@RequestMapping("/prospect/{submissionId}/task-list")
public class ProspectTaskListController {

  public static final String PAGE_NAME = "Task list";

  private final SubmissionDetailService submissionDetailService;
  private final List<ProspectTaskListSection> prospectTaskListSections;
  private final List<ProspectTaskListItem> prospectTaskListItems;

  @Autowired
  public ProspectTaskListController(SubmissionDetailService submissionDetailService,
                                    List<ProspectTaskListSection> prospectTaskListSections,
                                    List<ProspectTaskListItem> prospectTaskListItems) {
    this.submissionDetailService = submissionDetailService;
    this.prospectTaskListSections = prospectTaskListSections;
    this.prospectTaskListItems = prospectTaskListItems;
  }

  @GetMapping
  public ModelAndView getTaskList(@PathVariable("submissionId") UUID submissionId) {

    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionId);

    var modelAndView = new ModelAndView("hm/prospect/tasklist/taskList")
        .addObject(
            "taskListSections",
            TaskListSectionUtil.createSectionViews(
                prospectTaskListSections,
                prospectTaskListItems,
                new ProspectTaskListItemType(submissionDetail)
            )
        );

    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(PAGE_NAME)
        .addWorkAreaBreadcrumb()
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);

    return modelAndView;
  }
}

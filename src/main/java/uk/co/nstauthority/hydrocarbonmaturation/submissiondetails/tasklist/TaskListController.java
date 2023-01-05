package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/task-list")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
public class TaskListController {

  private final SubmissionDetailService submissionDetailService;
  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final TaskListService taskListService;
  public static final String PAGE_NAME = "Task list";

  @Autowired
  public TaskListController(SubmissionDetailService submissionDetailService,
                            GeneralDetailsPersistenceService generalDetailsPersistenceService,
                            TaskListService taskListService) {
    this.submissionDetailService = submissionDetailService;
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.taskListService = taskListService;
  }

  @GetMapping
  public ModelAndView getTaskList(@PathVariable("submissionDetailId") UUID submissionId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionId);
    var sections = taskListService.getAllSections(submissionDetail);

    var modelAndView = new ModelAndView("remi/submissiondetails/tasklist/taskList")
        .addObject("taskListSections", sections)
        .addObject("submissionTypeName", submissionDetail.getSubmissionType().getTypeName())
        .addObject("resourceName", getResourceName(submissionDetail));

    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(PAGE_NAME)
        .addWorkAreaBreadcrumb()
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);

    return modelAndView;
  }

  private String getResourceName(SubmissionDetail submissionDetail) {
    return generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(submissionDetail)
        .map(GeneralDetail::getResourceName)
        .orElse("");
  }
}

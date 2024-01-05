package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.DeleteSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.SubmissionUpdateService;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/task-list")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@IsFromResponsibleTeam
public class TaskListController {

  private final SubmissionDetailService submissionDetailService;
  private final SubmissionResourceService submissionResourceService;
  private final TaskListService taskListService;
  private final SubmissionUpdateService submissionUpdateService;
  public static final String PAGE_NAME = "Task list";

  @Autowired
  public TaskListController(SubmissionDetailService submissionDetailService,
                            SubmissionResourceService submissionResourceService, TaskListService taskListService,
                            SubmissionUpdateService submissionUpdateService) {
    this.submissionDetailService = submissionDetailService;
    this.submissionResourceService = submissionResourceService;
    this.taskListService = taskListService;
    this.submissionUpdateService = submissionUpdateService;
  }

  @GetMapping
  public ModelAndView getTaskList(@PathVariable("submissionDetailId") UUID submissionId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionId);
    var sections = taskListService.getAllSections(submissionDetail);
    String deleteLinkText;
    if (BooleanUtils.isTrue(submissionDetail.isCorrection())) {
      deleteLinkText = "Delete correction";
    } else if (submissionDetail.isUpdateSubmission()) {
      deleteLinkText = "Delete submission update";
    } else {
      deleteLinkText = "Delete submission";
    }
    var modelAndView = new ModelAndView("remi/submissiondetails/tasklist/taskList")
        .addObject("taskListSections", sections)
        .addObject("submissionTypeName", submissionDetailService.getSubmissionTypeNameWithAcronym(submissionDetail))
        .addObject("resourceName", submissionResourceService.getResourceNameOrDefault(submissionDetail))
        .addObject("deleteLinkText", deleteLinkText)
        .addObject("deleteSubmissionUrl",
            ReverseRouter.route(on(DeleteSubmissionController.class).showDeleteConfirmationPage(submissionId)));

    var submissionUpdateOpt = submissionUpdateService
        .getLatestUnresolvedUpdateForUpdateSubmissionDetail(submissionDetail);

    submissionUpdateOpt.ifPresent(submissionUpdate ->
        modelAndView
            .addObject("submissionUpdateReason", submissionUpdate.getReason()));
    modelAndView.addObject("isSubmissionUpdate", submissionUpdateOpt.isPresent());

    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(PAGE_NAME)
        .addWorkAreaBreadcrumb()
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);

    return modelAndView;
  }
}

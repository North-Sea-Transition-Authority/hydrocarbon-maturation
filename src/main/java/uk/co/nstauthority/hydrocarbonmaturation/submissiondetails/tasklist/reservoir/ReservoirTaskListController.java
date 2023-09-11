package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/reservoir-task-list")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(
    mode = HasSubmissionType.Mode.EXCLUDE,
    submissionTypes = {SecuritySubmissionTypes.PROSPECT_TYPES, SecuritySubmissionTypes.POST_DRILL_DRY_HOLE}
)
@IsFromResponsibleTeam
public class ReservoirTaskListController {

  public static final String PAGE_TITLE = "Reservoirs";

  private final SubmissionDetailService submissionDetailService;
  private final ReservoirTaskListService reservoirTaskListService;

  @Autowired
  public ReservoirTaskListController(SubmissionDetailService submissionDetailService,
                                     ReservoirTaskListService reservoirTaskListService) {
    this.submissionDetailService = submissionDetailService;
    this.reservoirTaskListService = reservoirTaskListService;
  }

  @GetMapping
  public ModelAndView getReservoirTaskList(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                           ServiceUserDetail user) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var reservoirTaskListItems = reservoirTaskListService.getAllReservoirTaskLists(submissionDetail);
    var modelAndView = new ModelAndView("remi/submissiondetails/tasklist/reservoirTaskList")
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("reservoirTaskListItems", reservoirTaskListItems);

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(
        modelAndView,
        PAGE_TITLE,
        submissionDetail
    );

    return modelAndView;
  }
}

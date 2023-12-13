package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedunsanctionedwells;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/planned-unsanctioned-wells")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.FDP_OR_FDPA_SUBMISSION)
@IsFromResponsibleTeam
public class PlannedUnsanctionedWellsController {
  public static final String PAGE_NAME = "Planned unsanctioned wells";

  private final SubmissionDetailService submissionDetailService;
  private final PlannedUnsanctionedWellsService plannedUnsanctionedWellsService;
  private final PlannedUnsanctionedWellsFormValidator plannedUnsanctionedWellsFormValidator;

  @Autowired
  public PlannedUnsanctionedWellsController(SubmissionDetailService submissionDetailService,
                                            PlannedUnsanctionedWellsService plannedUnsanctionedWellsService,
                                            PlannedUnsanctionedWellsFormValidator plannedUnsanctionedWellsFormValidator) {
    this.submissionDetailService = submissionDetailService;
    this.plannedUnsanctionedWellsService = plannedUnsanctionedWellsService;
    this.plannedUnsanctionedWellsFormValidator = plannedUnsanctionedWellsFormValidator;
  }

  @GetMapping
  public ModelAndView getPlannedUnsanctionedWells(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getPlannedUnsanctionedWellsModelAndView(
        plannedUnsanctionedWellsService.submissionDetailToForm(submissionDetail),
        submissionDetail
    );
  }

  @PostMapping
  public ModelAndView savePlannedUnsanctionedWells(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                   @ModelAttribute("form") PlannedUnsanctionedWellsForm form,
                                                   BindingResult bindingResult,
                                                   ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    if (plannedUnsanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType)) {
      plannedUnsanctionedWellsService.savePlannedUnsanctionedWells(form, submissionDetail);
      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
    }
    return getPlannedUnsanctionedWellsModelAndView(form, submissionDetail);
  }

  private ModelAndView getPlannedUnsanctionedWellsModelAndView(PlannedUnsanctionedWellsForm form,
                                                               SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/plannedUnsanctionedWells")
        .addObject("form", form)
        .addObject("pageTitle", PAGE_NAME)
        .addObject("submissionType", submissionDetail.getSubmissionType());

    return TaskListBreadcrumbUtil
        .addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
  }
}

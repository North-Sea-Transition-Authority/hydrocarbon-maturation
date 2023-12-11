package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/planned-sanctioned-wells")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@IsFromResponsibleTeam
public class PlannedSanctionedWellsController {

  public static final String PAGE_NAME = "Planned sanctioned wells";

  private final PlannedSanctionedWellsService plannedSanctionedWellsService;
  private final SubmissionDetailService submissionDetailService;
  private final PlannedSanctionedWellsFormValidator plannedSanctionedWellsFormValidator;

  @Autowired
  public PlannedSanctionedWellsController(PlannedSanctionedWellsService plannedSanctionedWellsService,
                                          SubmissionDetailService submissionDetailService,
                                          PlannedSanctionedWellsFormValidator plannedSanctionedWellsFormValidator) {
    this.plannedSanctionedWellsService = plannedSanctionedWellsService;
    this.submissionDetailService = submissionDetailService;
    this.plannedSanctionedWellsFormValidator = plannedSanctionedWellsFormValidator;
  }

  @GetMapping
  public ModelAndView getPlannedSanctionedWells(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getPlannedSanctionedWellsModelAndView(
        plannedSanctionedWellsService.submissionDetailToForm(submissionDetail),
        submissionDetail
    );
  }

  @PostMapping
  public ModelAndView savePlannedSanctionedWells(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                 @ModelAttribute("form") PlannedSanctionedWellsForm form,
                                                 BindingResult bindingResult,
                                                 ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    if (plannedSanctionedWellsFormValidator.isValid(form, bindingResult, submissionDetail, validationType)) {
      plannedSanctionedWellsService.savePlannedSanctionedWells(form, submissionDetail);
      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
    }
    return getPlannedSanctionedWellsModelAndView(form, submissionDetail);
  }

  private ModelAndView getPlannedSanctionedWellsModelAndView(PlannedSanctionedWellsForm form,
                                                             SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/plannedSanctionedWells")
        .addObject("form", form)
        .addObject("pageTitle", PAGE_NAME)

        .addObject("stimulationOptions",
            DisplayableEnumOptionUtil.getDisplayableOptions(StimulationType.class))
        .addObject("faultOptions",
            DisplayableEnumOptionUtil.getDisplayableOptions(Fault.class))
        .addObject("holeCompletionOptions",
            DisplayableEnumOptionUtil.getDisplayableOptions(HoleCompletionType.class))
        .addObject("plannedCompletionOptions",
            DisplayableEnumOptionUtil.getDisplayableOptions(PlannedCompletionType.class))
        .addObject("artificialLiftMechanismsOptions",
            DisplayableEnumOptionUtil.getDisplayableOptions(ArtificialLiftMechanism.class));

    return TaskListBreadcrumbUtil
        .addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
  }
}
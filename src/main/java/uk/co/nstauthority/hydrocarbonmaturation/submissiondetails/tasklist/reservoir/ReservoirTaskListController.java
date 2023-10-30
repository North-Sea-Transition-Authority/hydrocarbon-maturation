package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir;

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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.ReservoirAdditionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirAdditionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirAdditionsFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

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
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final ReservoirAdditionsFormService reservoirAdditionsFormService;
  private final ReservoirAdditionsFormValidator reservoirAdditionsFormValidator;

  @Autowired
  public ReservoirTaskListController(SubmissionDetailService submissionDetailService,
                                     ReservoirTaskListService reservoirTaskListService,
                                     ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
                                     ReservoirAdditionsFormService reservoirAdditionsFormService,
                                     ReservoirAdditionsFormValidator reservoirAdditionsFormValidator) {
    this.submissionDetailService = submissionDetailService;
    this.reservoirTaskListService = reservoirTaskListService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.reservoirAdditionsFormService = reservoirAdditionsFormService;
    this.reservoirAdditionsFormValidator = reservoirAdditionsFormValidator;
  }

  @GetMapping
  public ModelAndView getReservoirTaskList(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                           ServiceUserDetail user) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getReservoirTaskListModelAndView(
        reservoirAdditionsFormService.getReservoirAdditionsToForm(submissionDetail),
        submissionDetail
    );
  }

  @PostMapping
  public ModelAndView saveReservoirAdditions(@PathVariable UUID submissionDetailId,
                                             @ModelAttribute("form") ReservoirAdditionForm form,
                                             BindingResult bindingResult) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var additionStatus = form.getReservoirAdditionStatus();

    if (additionStatus == null) {
      reservoirAdditionsFormValidator.validateFull(form, bindingResult, submissionDetail);
      return getReservoirTaskListModelAndViewWithError(form, bindingResult, submissionDetail);
    }

    switch (additionStatus) {
      case YES_NOW -> {
        // create new reservoir page
        reservoirAdditionsFormService.updateReservoirAdditionStatus(submissionDetail, ReservoirAdditionStatus.YES_NOW);
        var newReservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
            .createReservoirAndReservoirSubmissionDetail(submissionDetail);
        return ReverseRouter.redirect(on(GeneralReservoirInformationController.class)
            .getGeneralReservoirInformationPage(submissionDetailId, newReservoirSubmissionDetail.getId()));
      }
      case YES_LATER -> {
        // return to task list
        reservoirAdditionsFormService.updateReservoirAdditionStatus(submissionDetail, ReservoirAdditionStatus.YES_LATER);
        return ReverseRouter.redirect(on(TaskListController.class)
            .getTaskList(submissionDetailId));
      }
      case NO -> {
        if (reservoirAdditionsFormValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL)) {
          // update status to no and return
          reservoirAdditionsFormService.updateReservoirAdditionStatus(submissionDetail, ReservoirAdditionStatus.NO);
          return ReverseRouter.redirect(on(TaskListController.class)
              .getTaskList(submissionDetailId));
        } else {
          // return with errors
          return getReservoirTaskListModelAndViewWithError(form, bindingResult, submissionDetail);
        }
      }
      default -> {
        return ReverseRouter.redirect(on(ReservoirTaskListController.class)
            .getReservoirTaskList(submissionDetailId, null));
      }
    }
  }

  private ModelAndView getReservoirTaskListModelAndView(ReservoirAdditionForm form, SubmissionDetail submissionDetail) {
    var reservoirTaskListItems = reservoirTaskListService.getAllReservoirTaskLists(submissionDetail);
    var modelAndView = new ModelAndView("remi/submissiondetails/tasklist/reservoirTaskList")
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("form", form)
        .addObject("actionUrl", ReverseRouter.route(on(ReservoirTaskListController.class)
            .saveReservoirAdditions(submissionDetail.getId(), form, null)))
        .addObject("reservoirTaskListItems", reservoirTaskListItems)
        .addObject("reservoirAdditionStatusOptions",
            DisplayableEnumOptionUtil.getDisplayableOptions(ReservoirAdditionStatus.class));
    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(
        modelAndView,
        PAGE_TITLE,
        submissionDetail
    );

    return modelAndView;
  }

  private ModelAndView getReservoirTaskListModelAndViewWithError(ReservoirAdditionForm form,
                                                                 BindingResult bindingResult,
                                                                 SubmissionDetail submissionDetail) {
    var modelAndView = getReservoirTaskListModelAndView(form, submissionDetail);

    if (bindingResult.hasErrors()) {
      var message = bindingResult.getAllErrors().get(0)
          .getDefaultMessage();
      modelAndView
          .addObject("singleErrorMessage", message);
    }

    return modelAndView;
  }
}

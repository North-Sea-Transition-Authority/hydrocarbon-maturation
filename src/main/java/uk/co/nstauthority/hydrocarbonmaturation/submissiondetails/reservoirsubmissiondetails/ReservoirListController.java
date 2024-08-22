package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/add-reservoirs")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.DISCOVERY_AND_FIELD)
@IsFromResponsibleTeam
public class ReservoirListController {

  private final SubmissionDetailService submissionDetailService;
  private final ReservoirListFormService reservoirListFormService;
  private final ReservoirListValidator reservoirListValidator;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final ControllerHelperService controllerHelperService;

  @Autowired
  public ReservoirListController(SubmissionDetailService submissionDetailService,
                                 ReservoirListFormService reservoirListFormService,
                                 ReservoirListValidator reservoirListValidator,
                                 ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
                                 ControllerHelperService controllerHelperService) {
    this.submissionDetailService = submissionDetailService;
    this.reservoirListFormService = reservoirListFormService;
    this.reservoirListValidator = reservoirListValidator;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.controllerHelperService = controllerHelperService;
  }

  @GetMapping
  public ModelAndView getReservoirs(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                    User user) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    if (reservoirSubmissionDetailPersistenceService.submissionHasReservoirs(submissionDetail)) {
      return ReverseRouter.redirect(on(ReservoirTaskListController.class).getReservoirTaskList(
          submissionDetail.getId(), null));
    }
    var form = reservoirListFormService.getReservoirForm(submissionDetail);
    return getReservoirsModelAndView(submissionDetail, form);
  }

  @PostMapping
  public ModelAndView saveReservoirs(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                     @ModelAttribute("form") ReservoirListForm form,
                                     BindingResult bindingResult,
                                     User user) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    if (!reservoirListValidator.isValid(form, bindingResult, submissionDetail, ValidationType.FULL)) {
      var modelAndView = getReservoirsModelAndView(submissionDetail, form);
      modelAndView.addObject("errorList", controllerHelperService.getErrorItemsFromBindingResult(bindingResult));
      return modelAndView;
    }
    reservoirSubmissionDetailPersistenceService.saveReservoirSubmissionDetails(form, submissionDetail);
    return ReverseRouter.redirect(on(ReservoirTaskListController.class).getReservoirTaskList(
        submissionDetail.getId(), null)
    );
  }

  private ModelAndView getReservoirsModelAndView(SubmissionDetail submissionDetail, ReservoirListForm form) {
    var modelAndView = new ModelAndView("/remi/submissiondetails/reservoir/reservoirList");

    var pageName = String.format("Add each reservoir that exists on this %s",
        submissionDetail.getSubmission().getResource().getType().getDisplayName().toLowerCase()
    );
    modelAndView
        .addObject("form", form)
        .addObject("pageTitle", pageName)
        .addObject("reservoirCount", form.getReservoirs().size())
        .addObject("backLinkUrl",
            ReverseRouter.route(on(TaskListController.class).getTaskList(submissionDetail.getId(), null)));

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, "Reservoirs", submissionDetail);
    return modelAndView;
  }
}

package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

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
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist.ProspectTaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist.ProspectTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/reservoir-properties")
public class ReservoirPropertiesController {

  private final ControllerHelperService controllerHelperService;
  private final ReservoirPropertiesFormService reservoirPropertiesFormService;
  private final ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;
  private final SubmissionDetailService submissionDetailService;

  static final String PAGE_NAME = "Reservoir details";

  @Autowired
  public ReservoirPropertiesController(ControllerHelperService controllerHelperService,
                                       ReservoirPropertiesFormService reservoirPropertiesFormService,
                                       ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService,
                                       SubmissionDetailService submissionDetailService) {
    this.controllerHelperService = controllerHelperService;
    this.reservoirPropertiesFormService = reservoirPropertiesFormService;
    this.reservoirPropertiesPersistenceService = reservoirPropertiesPersistenceService;
    this.submissionDetailService = submissionDetailService;
  }

  @GetMapping
  public ModelAndView getReservoirProperties(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getReservoirPropertiesModelAndView(
        reservoirPropertiesFormService.getReservoirPropertiesForm(submissionDetail), submissionDetailId);
  }

  @PostMapping
  public ModelAndView saveReservoirProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                              @ModelAttribute("form") ReservoirPropertiesForm form, BindingResult bindingResult) {

    bindingResult = reservoirPropertiesFormService.validate(form, bindingResult);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getReservoirPropertiesModelAndView(form, submissionDetailId),
        () -> {
          var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
          reservoirPropertiesPersistenceService.saveReservoirProperty(form, submissionDetail);
          return ReverseRouter.redirect(on(ProspectTaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getReservoirPropertiesModelAndView(ReservoirPropertiesForm form, UUID submissionDetailId) {
    var modelAndView = new ModelAndView("remi/submissiondetails/reservoirProperties")
        .addObject("form", form);

    ProspectTaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetailId);
    return modelAndView;
  }
}

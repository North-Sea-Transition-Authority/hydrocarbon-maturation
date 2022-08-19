package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.SubmissionTaskListRedirectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/reservoir-properties")
public class ReservoirPropertiesController {

  private final ControllerHelperService controllerHelperService;
  private final ReservoirPropertiesFormService reservoirPropertiesFormService;
  private final ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final SubmissionTaskListRedirectionService submissionTaskListRedirectionService;

  public static final String PAGE_NAME = "Reservoir properties";

  @Autowired
  public ReservoirPropertiesController(ControllerHelperService controllerHelperService,
                                       ReservoirPropertiesFormService reservoirPropertiesFormService,
                                       ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService,
                                       SubmissionDetailService submissionDetailService,
                                       SubmissionTaskListRedirectionService submissionTaskListRedirectionService) {
    this.controllerHelperService = controllerHelperService;
    this.reservoirPropertiesFormService = reservoirPropertiesFormService;
    this.reservoirPropertiesPersistenceService = reservoirPropertiesPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.submissionTaskListRedirectionService = submissionTaskListRedirectionService;
  }

  @GetMapping
  public ModelAndView getReservoirProperties(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getReservoirPropertiesModelAndView(
        reservoirPropertiesFormService.getReservoirPropertiesForm(submissionDetail), submissionDetailId);
  }

  @PostMapping
  public ModelAndView saveReservoirProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                              @ModelAttribute("form") ReservoirPropertiesForm form,
                                              BindingResult bindingResult, ValidationType validationType) {

    bindingResult = reservoirPropertiesFormService.validate(form, bindingResult, validationType);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getReservoirPropertiesModelAndView(form, submissionDetailId),
        () -> {
          var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
          reservoirPropertiesPersistenceService.saveReservoirProperty(form, submissionDetail);
          return submissionTaskListRedirectionService.getTaskListEndpoint(submissionDetail);
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

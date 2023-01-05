package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;


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
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/gas-fluid-properties")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
public class GasFluidPropertyController {

  private final ControllerHelperService controllerHelperService;
  private final GasFluidPropertyFormService gasFluidPropertyFormService;
  private final GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService;
  private final SubmissionDetailService submissionDetailService;

  public static final String PAGE_NAME = "Gas properties";

  @Autowired
  public GasFluidPropertyController(ControllerHelperService controllerHelperService,
                                    GasFluidPropertyFormService gasFluidPropertyFormService,
                                    GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService,
                                    SubmissionDetailService submissionDetailService) {
    this.controllerHelperService = controllerHelperService;
    this.gasFluidPropertyFormService = gasFluidPropertyFormService;
    this.gasFluidPropertyPersistenceService = gasFluidPropertyPersistenceService;
    this.submissionDetailService = submissionDetailService;
  }

  @GetMapping
  public ModelAndView getGasFluidProperties(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getGasFluidPropertiesModelAndView(
        gasFluidPropertyFormService.getGasFluidPropertyForm(submissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView saveGasFluidProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                             @ModelAttribute("form") GasFluidPropertyForm form,
                                             BindingResult bindingResult, ValidationType validationType) {
    bindingResult = gasFluidPropertyFormService.validate(form, bindingResult, validationType);
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getGasFluidPropertiesModelAndView(form, submissionDetail),
        () -> {
          gasFluidPropertyPersistenceService.saveGasFluidProperty(form, submissionDetail);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getGasFluidPropertiesModelAndView(GasFluidPropertyForm form, SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/gasFluidProperties")
        .addObject("form", form);

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);

    return modelAndView;
  }
}

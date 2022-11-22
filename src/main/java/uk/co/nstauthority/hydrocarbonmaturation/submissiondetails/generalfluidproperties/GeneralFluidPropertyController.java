package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;


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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/general-fluid-properties")
public class GeneralFluidPropertyController {

  private final ControllerHelperService controllerHelperService;
  private final GeneralFluidPropertyFormService generalFluidPropertyFormService;
  private final GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService;
  private final SubmissionDetailService submissionDetailService;

  public static final String PAGE_NAME = "General properties";

  @Autowired
  public GeneralFluidPropertyController(ControllerHelperService controllerHelperService,
                                        GeneralFluidPropertyFormService generalFluidPropertyFormService,
                                        GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService,
                                        SubmissionDetailService submissionDetailService) {
    this.controllerHelperService = controllerHelperService;
    this.generalFluidPropertyFormService = generalFluidPropertyFormService;
    this.generalFluidPropertyPersistenceService = generalFluidPropertyPersistenceService;
    this.submissionDetailService = submissionDetailService;
  }

  @GetMapping
  public ModelAndView getGeneralFluidProperties(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getGeneralFluidPropertiesModelAndView(
        generalFluidPropertyFormService.getGeneralFluidPropertyForm(submissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView saveGeneralFluidProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                 @ModelAttribute("form") GeneralFluidPropertyForm form,
                                                 BindingResult bindingResult, ValidationType validationType) {
    bindingResult = generalFluidPropertyFormService.validate(form, bindingResult, validationType);
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getGeneralFluidPropertiesModelAndView(form, submissionDetail),
        () -> {
          generalFluidPropertyPersistenceService.saveGeneralFluidProperty(form, submissionDetail);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getGeneralFluidPropertiesModelAndView(GeneralFluidPropertyForm form, SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/generalFluidProperties")
        .addObject("form", form);

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);

    return modelAndView;
  }
}

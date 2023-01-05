package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

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
@RequestMapping("/submission-details/{submissionDetailId}/oil-fluid-properties")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
public class OilFluidPropertyController {

  private final ControllerHelperService controllerHelperService;
  private final OilFluidPropertyFormService oilFluidPropertyFormService;
  private final OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService;
  private final SubmissionDetailService submissionDetailService;

  public static final String PAGE_NAME = "Oil properties";

  @Autowired
  public OilFluidPropertyController(ControllerHelperService controllerHelperService,
                                    OilFluidPropertyFormService oilFluidPropertyFormService,
                                    OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService,
                                    SubmissionDetailService submissionDetailService) {
    this.controllerHelperService = controllerHelperService;
    this.oilFluidPropertyFormService = oilFluidPropertyFormService;
    this.oilFluidPropertyPersistenceService = oilFluidPropertyPersistenceService;
    this.submissionDetailService = submissionDetailService;
  }

  @GetMapping
  public ModelAndView getOilFluidProperties(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getOilFluidPropertiesModelAndView(
        oilFluidPropertyFormService.getOilFluidPropertyForm(submissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView saveOilFluidProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                             @ModelAttribute("form") OilFluidPropertyForm form,
                                             BindingResult bindingResult, ValidationType validationType) {
    bindingResult = oilFluidPropertyFormService.validate(form, bindingResult, validationType);
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getOilFluidPropertiesModelAndView(form, submissionDetail),
        () -> {
          oilFluidPropertyPersistenceService.saveOilFluidProperty(form, submissionDetail);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getOilFluidPropertiesModelAndView(OilFluidPropertyForm form, SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/oilFluidProperties")
        .addObject("form", form);

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);

    return modelAndView;
  }
}

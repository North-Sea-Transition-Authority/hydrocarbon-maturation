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
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/reservoir-properties")
public class ReservoirPropertiesController {

  private final ControllerHelperService controllerHelperService;
  private final ReservoirPropertiesFormService reservoirPropertiesFormService;
  private final ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  public static final String PAGE_NAME = "Reservoir properties";

  @Autowired
  public ReservoirPropertiesController(ControllerHelperService controllerHelperService,
                                       ReservoirPropertiesFormService reservoirPropertiesFormService,
                                       ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService,
                                       SubmissionDetailService submissionDetailService,
                                       ProbabilityDistributionTemplateService probabilityDistributionTemplateService) {
    this.controllerHelperService = controllerHelperService;
    this.reservoirPropertiesFormService = reservoirPropertiesFormService;
    this.reservoirPropertiesPersistenceService = reservoirPropertiesPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
  }

  @GetMapping
  public ModelAndView getReservoirProperties(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getReservoirPropertiesModelAndView(
        reservoirPropertiesFormService.getReservoirPropertiesForm(submissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView saveReservoirProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                              @ModelAttribute("form") ReservoirPropertiesForm form,
                                              BindingResult bindingResult, ValidationType validationType) {

    bindingResult = reservoirPropertiesFormService.validate(form, bindingResult, validationType);
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getReservoirPropertiesWithErrors(bindingResult, form, submissionDetail),
        () -> {
          reservoirPropertiesPersistenceService.saveReservoirProperty(form, submissionDetail);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getReservoirPropertiesModelAndView(ReservoirPropertiesForm form, SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/reservoirProperties")
        .addObject("form", form);
    modelAndView = addProbabilityTemplates(modelAndView);

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
    return modelAndView;
  }

  private ModelAndView addProbabilityTemplates(ModelAndView modelAndView) {
    return modelAndView.addObject("averageNetPayThicknessTemplate",
            probabilityDistributionTemplateService
                .getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT_DISCOVERY))
        .addObject("averageNetPayThicknessError", ProbabilityDistributionTypeError.emptyError())
        .addObject("grossReservoirThicknessTemplate",
            probabilityDistributionTemplateService
                .getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT_DISCOVERY))
        .addObject("grossReservoirThicknessError", ProbabilityDistributionTypeError.emptyError())
        .addObject("grossRockVolumeTemplate",
            probabilityDistributionTemplateService
                .getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME))
        .addObject("grossRockVolumeError", ProbabilityDistributionTypeError.emptyError())
        .addObject("percentOfGrvOilBearingTemplate",
            probabilityDistributionTemplateService
                .getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING))
        .addObject("percentOfGrvOilBearingError", ProbabilityDistributionTypeError.emptyError())
        .addObject("netToGrossTemplate",
            probabilityDistributionTemplateService
                .getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT_DISCOVERY))
        .addObject("netToGrossError", ProbabilityDistributionTypeError.emptyError())
        .addObject("netPorosityTemplate",
            probabilityDistributionTemplateService
                .getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT_DISCOVERY))
        .addObject("netPorosityError", ProbabilityDistributionTypeError.emptyError())
        .addObject("depthToHydrocarbonContactTemplate",
            probabilityDistributionTemplateService
                .getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT))
        .addObject("depthToHydrocarbonContactError", ProbabilityDistributionTypeError.emptyError());
  }

  private ModelAndView getReservoirPropertiesWithErrors(BindingResult bindingResult,
                                                        ReservoirPropertiesForm form,
                                                        SubmissionDetail submissionDetail) {
    var modelAndView = getReservoirPropertiesModelAndView(form, submissionDetail);
    modelAndView
        .addObject("averageNetPayThicknessError",
        probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
            bindingResult,
            "averageNetPayThickness",
            form.getAverageNetPayThickness()))
        .addObject("grossReservoirThicknessError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "grossReservoirThickness",
                form.getGrossReservoirThickness()))
        .addObject("grossRockVolumeError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "grossRockVolume",
                form.getGrossRockVolume()))
        .addObject("percentOfGrvOilBearingError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "percentOfGrvOilBearing",
                form.getPercentOfGrvOilBearing()))
        .addObject("netToGrossError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "netToGross",
                form.getNetToGross()))
        .addObject("netPorosityError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "netPorosity",
                form.getNetPorosity()))
        .addObject("depthToHydrocarbonContactError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "depthToHydrocarbonContact",
                form.getDepthToHydrocarbonContact()));
    return modelAndView;
  }
}

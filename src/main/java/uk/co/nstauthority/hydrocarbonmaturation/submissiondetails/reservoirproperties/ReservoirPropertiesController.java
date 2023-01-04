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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
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
  private final DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  public static final String PAGE_NAME = "Reservoir properties";

  @Autowired
  public ReservoirPropertiesController(ControllerHelperService controllerHelperService,
                                       ReservoirPropertiesFormService reservoirPropertiesFormService,
                                       ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService,
                                       SubmissionDetailService submissionDetailService,
                                       ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
                                       DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService) {
    this.controllerHelperService = controllerHelperService;
    this.reservoirPropertiesFormService = reservoirPropertiesFormService;
    this.reservoirPropertiesPersistenceService = reservoirPropertiesPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.discoveryWellDetailsPersistenceService = discoveryWellDetailsPersistenceService;
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

    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    bindingResult = reservoirPropertiesFormService.validate(form, bindingResult,
        validationType, submissionDetail);
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
        .addObject("form", form)
        .addObject("submissionType", submissionDetail.getSubmissionType());

    addProbabilityTemplates(modelAndView, submissionDetail);
    if (submissionDetail.isDiscoverySubmission()) {
      modelAndView = addHydrocarbonsDiscoveredAndDiscoveryTemplates(modelAndView, submissionDetail);
    }
    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
    return modelAndView;
  }

  private ModelAndView addHydrocarbonsDiscoveredAndDiscoveryTemplates(ModelAndView modelAndView,
                                                                      SubmissionDetail submissionDetail) {
    return discoveryWellDetailsPersistenceService
        .getDiscoveryWellDetailBySubmissionDetail(submissionDetail)
        .map(discoveryWell -> {
          var gasCase = discoveryWellDetailsPersistenceService.isDiscoveryGasOnly(discoveryWell);
          var oilCase = discoveryWellDetailsPersistenceService.isDiscoveryOilOnly(discoveryWell);
          var mixedCase = discoveryWellDetailsPersistenceService.isDiscoveryMixed(discoveryWell);
          modelAndView
              .addObject("gasCase", gasCase)
              .addObject("oilCase", oilCase)
              .addObject("mixedCase", mixedCase);
          if (oilCase || mixedCase) {
            modelAndView
                .addObject("depthToOilWaterContactTemplate",
                    probabilityDistributionTemplateService
                        .getProbabilityDistributionTemplateForInstance(
                            ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT))
                .addObject("depthToOilWaterContactError", ProbabilityDistributionTypeError.emptyError());
          }
          if (gasCase || mixedCase) {
            modelAndView
                .addObject("depthToGasWaterContactTemplate",
                    probabilityDistributionTemplateService
                        .getProbabilityDistributionTemplateForInstance(
                            ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT))
                .addObject("depthToGasWaterContactError", ProbabilityDistributionTypeError.emptyError());
          }
          if (mixedCase) {
            modelAndView.addObject("depthToGasOilContactTemplate",
                probabilityDistributionTemplateService
                .getProbabilityDistributionTemplateForInstance(
                    ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT))
                .addObject("depthToGasOilContactError", ProbabilityDistributionTypeError.emptyError());
          }
          return modelAndView;
        })
        .orElseGet(() -> modelAndView
            .addObject("gasCase", false)
            .addObject("oilCase", false)
            .addObject("mixedCase", false));
  }

  private void addProbabilityTemplates(ModelAndView modelAndView, SubmissionDetail submissionDetail) {
    modelAndView
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
        .addObject("netPorosityError", ProbabilityDistributionTypeError.emptyError());
    if (submissionDetail.isProspectSubmission()) {
      modelAndView
          .addObject("depthToHydrocarbonContactTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT))
          .addObject("depthToHydrocarbonContactError", ProbabilityDistributionTypeError.emptyError())
          .addObject("averageNetPayThicknessTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT))
          .addObject("averageNetPayThicknessError", ProbabilityDistributionTypeError.emptyError());
    } else if (submissionDetail.isDryWellOrDiscoverySubmission()) {
      modelAndView.addObject("averageNetPayThicknessOptionalTemplate",
          probabilityDistributionTemplateService
              .getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DRY_WELL_DISCOVERY))
          .addObject("averageNetPayThicknessOptionalError", ProbabilityDistributionTypeError.emptyError());
    }
  }

  private ModelAndView getReservoirPropertiesWithErrors(BindingResult bindingResult,
                                                        ReservoirPropertiesForm form,
                                                        SubmissionDetail submissionDetail) {
    var modelAndView = getReservoirPropertiesModelAndView(form, submissionDetail);
    modelAndView
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
                form.getNetPorosity()));

    if (submissionDetail.isProspectSubmission()) {
      modelAndView
          .addObject("depthToHydrocarbonContactError",
          probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
              bindingResult,
              "depthToHydrocarbonContact",
              form.getDepthToHydrocarbonContact()))
          .addObject("averageNetPayThicknessError",
          probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
              bindingResult,
              "averageNetPayThickness",
              form.getAverageNetPayThickness()));
    } else if (submissionDetail.isDryWellOrDiscoverySubmission()) {
      modelAndView.addObject("averageNetPayThicknessOptionalError",
          probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
              bindingResult,
              "averageNetPayThickness",
              form.getAverageNetPayThickness()
          ));
    }

    if (submissionDetail.isDiscoverySubmission()) {
      addDiscoveryTemplatesWithErrors(modelAndView, form, bindingResult);
    }

    return modelAndView;
  }

  private void addDiscoveryTemplatesWithErrors(ModelAndView modelAndView,
                                               ReservoirPropertiesForm form,
                                               BindingResult bindingResult) {
    modelAndView
        .addObject("depthToOilWaterContactError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "depthToOilWaterContact",
                form.getDepthToOilWaterContact()))
        .addObject("depthToGasWaterContactError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "depthToGasWaterContact",
                form.getDepthToGasWaterContact()))
        .addObject("depthToGasOilContactError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "depthToGasOilContact",
                form.getDepthToGasOilContact()
            ));
  }
}

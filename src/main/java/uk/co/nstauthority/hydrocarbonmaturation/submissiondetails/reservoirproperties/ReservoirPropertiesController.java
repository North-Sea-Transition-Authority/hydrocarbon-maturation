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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionStatus;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeriesRestController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeriesSearchService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.FormationRestController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.FormationSearchService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.GroupName;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/" +
    "reservoir-submission-details/{reservoirSubmissionDetailId}/reservoir-properties")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@IsFromResponsibleTeam
public class ReservoirPropertiesController {

  private static final String MIXED_CASE = "mixedCase";
  private static final String GAS_CASE = "gasCase";
  private static final String OIL_CASE = "oilCase";
  public static final String PAGE_NAME = "Reservoir properties";

  private final ControllerHelperService controllerHelperService;
  private final ReservoirPropertiesFormService reservoirPropertiesFormService;
  private final ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;
  private final FormationSearchService formationSearchService;
  private final ReservoirSubUnitSearchService reservoirSubUnitSearchService;
  private final AgeSeriesSearchService ageSeriesSearchService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Autowired
  public ReservoirPropertiesController(ControllerHelperService controllerHelperService,
                                       ReservoirPropertiesFormService reservoirPropertiesFormService,
                                       ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService,
                                       SubmissionDetailService submissionDetailService,
                                       ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
                                       DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService,
                                       HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService,
                                       FormationSearchService formationSearchService,
                                       ReservoirSubUnitSearchService reservoirSubUnitSearchService,
                                       AgeSeriesSearchService ageSeriesSearchService,
                                       ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.controllerHelperService = controllerHelperService;
    this.reservoirPropertiesFormService = reservoirPropertiesFormService;
    this.reservoirPropertiesPersistenceService = reservoirPropertiesPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.discoveryWellDetailsPersistenceService = discoveryWellDetailsPersistenceService;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
    this.formationSearchService = formationSearchService;
    this.reservoirSubUnitSearchService = reservoirSubUnitSearchService;
    this.ageSeriesSearchService = ageSeriesSearchService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
  }

  @GetMapping
  public ModelAndView getReservoirProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                             @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    return getReservoirPropertiesModelAndView(
        reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView saveReservoirProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                              @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
                                              @ModelAttribute("form") ReservoirPropertiesForm form,
                                              BindingResult bindingResult,
                                              ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    bindingResult = reservoirPropertiesFormService.validate(form, bindingResult,
        validationType, submissionDetail);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getReservoirPropertiesWithErrors(bindingResult, form, submissionDetail),
        () -> {
          reservoirPropertiesPersistenceService.saveReservoirProperty(form, reservoirSubmissionDetail);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getReservoirPropertiesModelAndView(ReservoirPropertiesForm form, SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/reservoirProperties")
        .addObject("form", form)
        .addObject("groupNames", GroupName.getGroupNames())
        .addObject("lithologyOptions", ReservoirLithology.getReservoirLithologyOptions())
        .addObject("formationRestEndpoint",
            SearchSelectorService.route(on(FormationRestController.class).getFormationListWithManualEntry(null))
        )
        .addObject("preSelectedFormation", formationSearchService.getPreSelectedFormation(form.getFormationName()))
        .addObject("subUnitRestEndpoint",
            SearchSelectorService.route(on(ReservoirSubUnitRestController.class).getSubUnitListWithManualEntry(null))
        )
        .addObject("preSelectedSubUnit", reservoirSubUnitSearchService.getPreSelectedSubUnit(form.getSubUnit()))
        .addObject("ageSeriesRestEndpoint",
            SearchSelectorService.route(on(AgeSeriesRestController.class).getAgeSeriesList(null))
        )
        .addObject("preSelectedAgeStage", ageSeriesSearchService.getPreSelectedAgeSeries(form.getAgeStageOrSeries()))
        .addObject("discoverySubmission", submissionDetail.isDiscoverySubmission())
        .addObject("prospectSubmission", submissionDetail.isProspectSubmission())
        .addObject("dryHoleSubmission", submissionDetail.isDryWellSubmission());

    addProbabilityTemplates(modelAndView, submissionDetail);
    if (submissionDetail.isDiscoverySubmission()) {
      modelAndView = addHydrocarbonsDiscoveredAndDiscoveryTemplates(modelAndView, submissionDetail);
    } else if (submissionDetail.isProspectSubmission()) {
      modelAndView = addHydrocarbonCase(modelAndView, submissionDetail);
    }
    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
    return modelAndView;
  }

  private ModelAndView addHydrocarbonCase(ModelAndView modelAndView, SubmissionDetail submissionDetail) {
    return hydrocarbonCasePersistenceService
        .getHydrocarbonCaseDetail(submissionDetail)
        .map(hydrocarbonCaseDetail -> {
          var mixedCase = hydrocarbonCasePersistenceService.isMixedOilAndGasPresent(hydrocarbonCaseDetail);
          modelAndView
              .addObject(MIXED_CASE, mixedCase);
          return modelAndView;
        })
        .orElseGet(() -> modelAndView
            .addObject(MIXED_CASE, false));
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
              .addObject(GAS_CASE, gasCase)
              .addObject(OIL_CASE, oilCase)
              .addObject(MIXED_CASE, mixedCase);
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
            .addObject(GAS_CASE, false)
            .addObject(OIL_CASE, false)
            .addObject(MIXED_CASE, false));
  }

  private void addProbabilityTemplates(ModelAndView modelAndView, SubmissionDetail submissionDetail) {
    modelAndView
        .addObject("grossReservoirThicknessError", ProbabilityDistributionTypeError.emptyError())
        .addObject("grossRockVolumeError", ProbabilityDistributionTypeError.emptyError())
        .addObject("percentOfGrvOilBearingError", ProbabilityDistributionTypeError.emptyError())
        .addObject("netToGrossError", ProbabilityDistributionTypeError.emptyError())
        .addObject("netPorosityError", ProbabilityDistributionTypeError.emptyError());

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      modelAndView
          .addObject("grossReservoirThicknessTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_PROSPECT))
          .addObject("grossRockVolumeTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_PROSPECT))
          .addObject("percentOfGrvOilBearingTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_PROSPECT))
          .addObject("netToGrossTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.NET_TO_GROSS_PROSPECT))
          .addObject("netPorosityTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.NET_POROSITY_PROSPECT));
    } else if (resourceType == ResourceType.DISCOVERY) {
      modelAndView
          .addObject("grossReservoirThicknessTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_DISCOVERY))
          .addObject("grossRockVolumeTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_DISCOVERY))
          .addObject("percentOfGrvOilBearingTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_DISCOVERY))
          .addObject("netToGrossTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.NET_TO_GROSS_DISCOVERY))
          .addObject("netPorosityTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.NET_POROSITY_DISCOVERY));
    }

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
    } else if (submissionDetail.isDryWellSubmission()) {
      modelAndView.addObject("averageNetReservoirThicknessTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL))
          .addObject("averageNetReservoirThicknessError", ProbabilityDistributionTypeError.emptyError());
    } else if (submissionDetail.isDiscoverySubmission()) {
      modelAndView.addObject("averageNetPayThicknessOptionalTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY))
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

    var averageNetPayThicknessFormName = "averageNetPayThickness";
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
              averageNetPayThicknessFormName,
              form.getAverageNetPayThickness()));
    } else if (submissionDetail.isDryWellSubmission()) {
      modelAndView.addObject("averageNetReservoirThicknessError",
          probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
              bindingResult,
              averageNetPayThicknessFormName,
              form.getAverageNetPayThickness()));
    } else if (submissionDetail.isDiscoverySubmission()) {
      modelAndView.addObject("averageNetPayThicknessOptionalError",
          probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
              bindingResult,
              averageNetPayThicknessFormName,
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

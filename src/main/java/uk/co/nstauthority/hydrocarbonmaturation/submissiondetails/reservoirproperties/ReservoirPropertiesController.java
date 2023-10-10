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
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeriesRestController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeriesSearchService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.FormationRestController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.FormationSearchService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.GroupName;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCasePersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
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

  private final ReservoirPropertiesFormService reservoirPropertiesFormService;
  private final ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService;
  private final FormationSearchService formationSearchService;
  private final ReservoirSubUnitSearchService reservoirSubUnitSearchService;
  private final AgeSeriesSearchService ageSeriesSearchService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final ReservoirPropertiesFormValidator reservoirPropertiesFormValidator;
  private final GeneralReservoirInformationService generalReservoirInformationService;

  @Autowired
  public ReservoirPropertiesController(ReservoirPropertiesFormService reservoirPropertiesFormService,
                                       ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService,
                                       SubmissionDetailService submissionDetailService,
                                       ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
                                       HydrocarbonCasePersistenceService hydrocarbonCasePersistenceService,
                                       FormationSearchService formationSearchService,
                                       ReservoirSubUnitSearchService reservoirSubUnitSearchService,
                                       AgeSeriesSearchService ageSeriesSearchService,
                                       ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
                                       ReservoirPropertiesFormValidator reservoirPropertiesFormValidator,
                                       GeneralReservoirInformationService generalReservoirInformationService) {
    this.reservoirPropertiesFormService = reservoirPropertiesFormService;
    this.reservoirPropertiesPersistenceService = reservoirPropertiesPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.hydrocarbonCasePersistenceService = hydrocarbonCasePersistenceService;
    this.formationSearchService = formationSearchService;
    this.reservoirSubUnitSearchService = reservoirSubUnitSearchService;
    this.ageSeriesSearchService = ageSeriesSearchService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.reservoirPropertiesFormValidator = reservoirPropertiesFormValidator;
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  @GetMapping
  public ModelAndView getReservoirProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                             @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    return getReservoirPropertiesModelAndView(
        reservoirPropertiesFormService.getReservoirPropertiesForm(reservoirSubmissionDetail), reservoirSubmissionDetail);
  }

  @PostMapping
  public ModelAndView saveReservoirProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                              @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
                                              @ModelAttribute("form") ReservoirPropertiesForm form,
                                              BindingResult bindingResult,
                                              ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    if (!reservoirPropertiesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType)) {
      return getReservoirPropertiesWithErrors(bindingResult, form, reservoirSubmissionDetail);
    } else {
      reservoirPropertiesPersistenceService.saveReservoirProperty(form, reservoirSubmissionDetail);
      if (submissionDetail.isProspectSubmission()) {
        return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
      } else {
        return ReverseRouter.redirect(on(ReservoirTaskListController.class)
            .getReservoirTaskList(submissionDetailId, null));
      }
    }
  }

  private ModelAndView getReservoirPropertiesModelAndView(ReservoirPropertiesForm form,
                                                          ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
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
        .addObject("dryHoleSubmission", submissionDetail.isDryWellSubmission())
        .addObject("conceptSelectReportSubmission", submissionDetail.isConceptSelectReportSubmission())
        .addObject("fieldDevelopmentPlanSubmission", submissionDetail.isFieldDevelopmentPlanSubmission())
        .addObject("fieldDevelopmentPlanAddendumSubmission",
            submissionDetail.isFieldDevelopmentPlanAddendumSubmission())
        .addObject("cessationOfProductionSubmission", submissionDetail.isCessationOfProductionSubmission());

    addProbabilityTemplates(modelAndView, submissionDetail);

    if (submissionDetail.isProspectSubmission()) {
      modelAndView = addHydrocarbonCase(modelAndView, submissionDetail);
    } else if (submissionDetail.isDiscoverySubmission() || submissionDetail.isFieldSubmission()) {
      modelAndView = addHydrocarbonsDiscoveredAndTemplatesForType(modelAndView, reservoirSubmissionDetail);
    }

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
    } else if (resourceType == ResourceType.DISCOVERY || resourceType == ResourceType.FIELD) {
      TaskListBreadcrumbUtil.addReservoirTaskListBreadcrumbsToModelAndView(modelAndView, PAGE_NAME, reservoirSubmissionDetail);
    }
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

  private ModelAndView addHydrocarbonsDiscoveredAndTemplatesForType(ModelAndView modelAndView,
                                                                    ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)
        .map(reservoirHydrocarbons -> {
          var gasCase = generalReservoirInformationService.isGasOnly(reservoirHydrocarbons);
          var oilCase = generalReservoirInformationService.isOilOnly(reservoirHydrocarbons);
          var mixedCase = generalReservoirInformationService.isMixed(reservoirHydrocarbons);
          modelAndView
              .addObject(GAS_CASE, gasCase)
              .addObject(OIL_CASE, oilCase)
              .addObject(MIXED_CASE, mixedCase);

          var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
          if (submissionDetail.isDiscoverySubmission()) {
            addDiscoveryTemplates(gasCase, oilCase, mixedCase, modelAndView);
          } else if (submissionDetail.isFieldSubmission()) {
            if (submissionDetail.isCessationOfProductionSubmission()) {
              addCessationOfProductionTemplates(gasCase, oilCase, mixedCase, modelAndView);
            } else {
              addOtherFieldTypeTemplates(gasCase, oilCase, mixedCase, modelAndView);
            }
          }
          return modelAndView;

        }).orElseGet(() -> modelAndView
            .addObject(GAS_CASE, false)
            .addObject(OIL_CASE, false)
            .addObject(MIXED_CASE, false));
  }

  private ModelAndView addDepthToTemplates(boolean gasCase,
                                           boolean oilCase,
                                           boolean mixedCase,
                                           ModelAndView modelAndView) {
    if (oilCase || mixedCase) {
      modelAndView
          .addObject("depthToOilWaterContactTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.DEPTH_TO_OIL_WATER_CONTACT_FIELD))
          .addObject("depthToOilWaterContactError", ProbabilityDistributionTypeError.emptyError());
    }
    if (gasCase || mixedCase) {
      modelAndView
          .addObject("depthToGasWaterContactTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_WATER_CONTACT_FIELD))
          .addObject("depthToGasWaterContactError", ProbabilityDistributionTypeError.emptyError());
    }
    if (mixedCase) {
      modelAndView.addObject("depthToGasOilContactTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.DEPTH_TO_GAS_OIL_CONTACT_FIELD))
          .addObject("depthToGasOilContactError", ProbabilityDistributionTypeError.emptyError());
    }
    return modelAndView;
  }

  private ModelAndView addDiscoveryTemplates(boolean gasCase,
                                             boolean oilCase,
                                             boolean mixedCase,
                                             ModelAndView modelAndView) {
    return addDepthToTemplates(gasCase, oilCase, mixedCase, modelAndView);
  }

  private ModelAndView addOtherFieldTypeTemplates(boolean gasCase,
                                                  boolean oilCase,
                                                  boolean mixedCase,
                                                  ModelAndView modelAndView) {
    return addDepthToTemplates(gasCase, oilCase, mixedCase, modelAndView);
  }

  private ModelAndView addCessationOfProductionTemplates(boolean gasCase,
                                                         boolean oilCase,
                                                         boolean mixedCase,
                                                         ModelAndView modelAndView) {
    if (oilCase || mixedCase) {
      modelAndView
          .addObject("originalDepthToOilWaterContactTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_OIL_WATER_CONTACT_FIELD))
          .addObject("originalDepthToOilWaterContactError", ProbabilityDistributionTypeError.emptyError());

      modelAndView
          .addObject("depthToMovedOilWaterContactTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_OIL_WATER_CONTACT_FIELD))
          .addObject("depthToMovedOilWaterContactError", ProbabilityDistributionTypeError.emptyError());
    }
    if (gasCase || mixedCase) {
      modelAndView
          .addObject("originalDepthToGasWaterContactTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_WATER_CONTACT_FIELD))
          .addObject("originalDepthToGasWaterContactError", ProbabilityDistributionTypeError.emptyError());

      modelAndView
          .addObject("depthToMovedGasWaterContactTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_WATER_CONTACT_FIELD))
          .addObject("depthToMovedGasWaterContactError", ProbabilityDistributionTypeError.emptyError());
    }
    if (mixedCase) {
      modelAndView.addObject("originalDepthToGasOilContactTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.ORIGINAL_DEPTH_TO_GAS_OIL_CONTACT_FIELD))
          .addObject("originalDepthToGasOilContactError", ProbabilityDistributionTypeError.emptyError());

      modelAndView
          .addObject("depthToMovedGasOilContactTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.DEPTH_TO_MOVED_GAS_OIL_CONTACT_FIELD))
          .addObject("depthToMovedGasOilContactError", ProbabilityDistributionTypeError.emptyError());
    }
    return modelAndView;
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
    } else if (resourceType == ResourceType.FIELD) {
      modelAndView
          .addObject("grossReservoirThicknessTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.GROSS_RESERVOIR_THICKNESS_FIELD))
          .addObject("grossRockVolumeTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.GROSS_ROCK_VOLUME_FIELD))
          .addObject("percentOfGrvOilBearingTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.PERCENT_OF_GRV_OIL_BEARING_FIELD))
          .addObject("netToGrossTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.NET_TO_GROSS_FIELD))
          .addObject("netPorosityTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.NET_POROSITY_FIELD));
    }

    if (submissionDetail.isProspectSubmission()) {
      modelAndView
          .addObject("depthToHydrocarbonContactTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.DEPTH_TO_HYDROCARBON_CONTACT_PROSPECT))
          .addObject("depthToHydrocarbonContactError", ProbabilityDistributionTypeError.emptyError());

      modelAndView
          .addObject("averageNetPayThicknessOptionalTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_PROSPECT))
          .addObject("averageNetPayThicknessOptionalError", ProbabilityDistributionTypeError.emptyError());
    } else if (submissionDetail.isDryWellSubmission()) {
      modelAndView.addObject("averageNetReservoirThicknessTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.AVERAGE_NET_RESERVOIR_THICKNESS_DRY_WELL))
          .addObject("averageNetReservoirThicknessError", ProbabilityDistributionTypeError.emptyError());

      modelAndView
          .addObject("averageNetPayThicknessTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY))
          .addObject("averageNetPayThicknessError", ProbabilityDistributionTypeError.emptyError());
    } else if (submissionDetail.isDiscoverySubmission()) {
      modelAndView
          .addObject("averageNetPayThicknessTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_DISCOVERY))
          .addObject("averageNetPayThicknessError", ProbabilityDistributionTypeError.emptyError());
    } else if (submissionDetail.isFieldSubmission()) {
      modelAndView
          .addObject("averageNetPayThicknessTemplate",
              probabilityDistributionTemplateService
                  .getProbabilityDistributionTemplateForInstance(
                      ProbabilityDistributionFieldInstance.AVERAGE_NET_PAY_THICKNESS_FIELD))
          .addObject("averageNetPayThicknessError", ProbabilityDistributionTypeError.emptyError());
    }
  }

  private ModelAndView getReservoirPropertiesWithErrors(BindingResult bindingResult,
                                                        ReservoirPropertiesForm form,
                                                        ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();

    var modelAndView = getReservoirPropertiesModelAndView(form, reservoirSubmissionDetail);
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
          .addObject("averageNetPayThicknessOptionalError",
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
      modelAndView.addObject("averageNetPayThicknessError",
          probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
              bindingResult,
              averageNetPayThicknessFormName,
              form.getAverageNetPayThickness()
          ));
    } else if (submissionDetail.isDiscoverySubmission()) {
      modelAndView.addObject("averageNetPayThicknessError",
          probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
              bindingResult,
              averageNetPayThicknessFormName,
              form.getAverageNetPayThickness()
          ));
    }

    if (submissionDetail.isDiscoverySubmission()) {
      addDiscoveryTemplatesWithErrors(modelAndView, form, bindingResult);
    } else if (submissionDetail.isFieldSubmission()) {
      addFieldTemplatesWithErrors(modelAndView, form, bindingResult);
    }

    return modelAndView;
  }

  private void addDepthToTemplatesWithErrors(ModelAndView modelAndView,
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

  private void addFieldTemplatesWithErrors(ModelAndView modelAndView, ReservoirPropertiesForm form,
                                           BindingResult bindingResult) {
    modelAndView
        .addObject("originalDepthToOilWaterContactError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "originalDepthToOilWaterContact",
                form.getOriginalDepthToOilWaterContact()))
        .addObject("depthToMovedOilWaterContactError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "depthToMovedOilWaterContact",
                form.getDepthToMovedOilWaterContact()))
        .addObject("originalDepthToGasWaterContactError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "originalDepthToGasWaterContact",
                form.getOriginalDepthToGasWaterContact()))
        .addObject("depthToMovedGasWaterContactError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "depthToMovedGasWaterContact",
                form.getDepthToMovedGasWaterContact()))
        .addObject("originalDepthToGasOilContactError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "originalDepthToGasOilContact",
                form.getOriginalDepthToGasOilContact()))
        .addObject("depthToMovedGasOilContactError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "depthToMovedGasOilContact",
                form.getDepthToMovedGasOilContact()));

    addDepthToTemplatesWithErrors(modelAndView, form, bindingResult);
  }

  private void addDiscoveryTemplatesWithErrors(ModelAndView modelAndView,
                                               ReservoirPropertiesForm form,
                                               BindingResult bindingResult) {
    addDepthToTemplatesWithErrors(modelAndView, form, bindingResult);
  }
}

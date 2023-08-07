package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
import javax.validation.Valid;
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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityOfExceedanceTypeUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/" +
    "reservoir-submission-details/{reservoirSubmissionDetailId}/oil-case-outputs")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.DISCOVERY_AND_PROSPECT)
@IsFromResponsibleTeam
public class OilCaseOutputsController {

  private final SubmissionDetailService submissionDetailService;
  private final ControllerHelperService controllerHelperService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final OilCaseOutputsFormService oilCaseOutputsFormService;
  private final OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  public static final String PAGE_NAME = "Oil case outputs (resources)";

  @Autowired
  public OilCaseOutputsController(SubmissionDetailService submissionDetailService,
                                  ControllerHelperService controllerHelperService,
                                  ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
                                  OilCaseOutputsFormService oilCaseOutputsFormService,
                                  OilCaseOutputsPersistenceService oilCaseOutputsPersistenceService,
                                  ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.submissionDetailService = submissionDetailService;
    this.controllerHelperService = controllerHelperService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.oilCaseOutputsFormService = oilCaseOutputsFormService;
    this.oilCaseOutputsPersistenceService = oilCaseOutputsPersistenceService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
  }

  @GetMapping
  public ModelAndView getOilCaseOutputs(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                        @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    return getOilCaseOutputsModelAndView(
        oilCaseOutputsFormService.getOilCaseOutputsForm(reservoirSubmissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView saveOilCaseOutputs(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                         @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
                                         @Valid @ModelAttribute("form") OilCaseOutputsForm form,
                                         BindingResult bindingResult,
                                         ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    bindingResult = oilCaseOutputsFormService
        .validate(form, bindingResult, validationType, submissionDetail);

    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getOilOutputsModelAndViewWithErrors(bindingResult, form, submissionDetail),
        () -> {
          oilCaseOutputsPersistenceService.saveOilCaseOutputs(form, reservoirSubmissionDetail);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getOilCaseOutputsModelAndView(OilCaseOutputsForm form, SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/oilCaseOutputs")
        .addObject("form", form)
        .addObject("oilInPlaceError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedGasRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilEquivalentRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError());

    var resourceType = submissionDetail.getSubmission().getResource().getType();
    if (resourceType == ResourceType.PROSPECT) {
      modelAndView
          .addObject("oilInPlaceTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_PROSPECT))
          .addObject("oilRecoverableResourcesTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_PROSPECT))
          .addObject("associatedGasRecoverableResourcesTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_PROSPECT))
          .addObject("oilEquivalentRecoverableResourcesTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_PROSPECT));
    } else if (resourceType == ResourceType.DISCOVERY) {
      var contingentUncertaintyCategoryPromptOverrides =
          ProbabilityOfExceedanceTypeUtil.getContingentUncertaintyCategoryPromptOverrides();
      modelAndView
          .addObject("oilInPlaceTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_IN_PLACE_DISCOVERY))
          .addObject("oilRecoverableResourcesTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                  ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_RECOVERABLE_RESOURCES_DISCOVERY,
                  contingentUncertaintyCategoryPromptOverrides))
          .addObject("associatedGasRecoverableResourcesTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                  ProbabilityDistributionFieldInstance.OIL_OUTPUTS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_DISCOVERY,
                  contingentUncertaintyCategoryPromptOverrides))
          .addObject("oilEquivalentRecoverableResourcesTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstanceAndPromptOverrides(
                  ProbabilityDistributionFieldInstance.OIL_OUTPUTS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_DISCOVERY,
                  contingentUncertaintyCategoryPromptOverrides));
    }

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);
    return modelAndView;
  }

  private ModelAndView getOilOutputsModelAndViewWithErrors(BindingResult bindingResult,
                                                           OilCaseOutputsForm form,
                                                           SubmissionDetail submissionDetail) {
    var modelAndView = getOilCaseOutputsModelAndView(form, submissionDetail);

    return modelAndView
        .addObject("form", form)
        .addObject("oilInPlaceError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "oilInPlace",
                form.getOilInPlace()))
        .addObject("oilRecoverableResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "oilRecoverableResources",
                form.getOilRecoverableResources()))
        .addObject("associatedGasRecoverableResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "associatedGasRecoverableResources",
                form.getAssociatedGasRecoverableResources()))
        .addObject("oilEquivalentRecoverableResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "oilEquivalentRecoverableResources",
                form.getOilEquivalentRecoverableResources()));
  }
}

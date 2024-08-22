package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpaincreasedvolumes;

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
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasSubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.IsFromResponsibleTeam;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.SecuritySubmissionTypes;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/fdpa-increased-volumes")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.FDPA_SUBMISSIONS)
@IsFromResponsibleTeam
public class FdpaIncreasedVolumesController {

  static final String PAGE_TITLE = "FDPA increased volumes";

  private final SubmissionDetailService submissionDetailService;
  private final FdpaIncreasedVolumesService fdpaIncreasedVolumesService;
  private final FdpaIncreasedVolumesFormValidator fdpaIncreasedVolumesFormValidator;
  private final ReservoirHydrocarbonCaseService hydrocarbonCaseService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  @Autowired
  public FdpaIncreasedVolumesController(
      SubmissionDetailService submissionDetailService,
      FdpaIncreasedVolumesService fdpaIncreasedVolumesService,
      FdpaIncreasedVolumesFormValidator fdpaIncreasedVolumesFormValidator,
      ReservoirHydrocarbonCaseService hydrocarbonCaseService,
      ProbabilityDistributionTemplateService probabilityDistributionTemplateService) {
    this.submissionDetailService = submissionDetailService;
    this.fdpaIncreasedVolumesService = fdpaIncreasedVolumesService;
    this.fdpaIncreasedVolumesFormValidator = fdpaIncreasedVolumesFormValidator;
    this.hydrocarbonCaseService = hydrocarbonCaseService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
  }

  @GetMapping
  public ModelAndView getFdpaIncreasedVolumes(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var fdpaIncreasedVolumesForm = fdpaIncreasedVolumesService.submissionDetailToForm(submissionDetail);
    return getModelAndViewWithoutErrors(fdpaIncreasedVolumesForm, submissionDetail);
  }

  @PostMapping
  public ModelAndView saveFdpaIncreasedVolumes(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                               @ModelAttribute("form") FdpaIncreasedVolumesForm form,
                                               BindingResult bindingResult, ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    if (fdpaIncreasedVolumesFormValidator.isValid(form, bindingResult, submissionDetail, validationType)) {
      fdpaIncreasedVolumesService.saveFdpaIncreasedVolumes(form, submissionDetail);
      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId, null));
    } else {
      return getModelAndViewWithErrors(form, bindingResult, submissionDetail);
    }
  }

  private ModelAndView getBaseModelAndView(FdpaIncreasedVolumesForm form,
                                           SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/fdpaIncreasedVolumes")
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("form", form)
        .addObject("hydrocarbonCase", hydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
        .addObject("oilInitiallyInPlaceTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA))
        .addObject("gasInitiallyInPlaceTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_INITIALLY_IN_PLACE_INCREASED_VOLUMES_FDPA))
        .addObject("oilRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA))
        .addObject("oilReservesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_RESERVES_INCREASED_VOLUMES_FDPA))
        .addObject("associatedCondensateRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA))
        .addObject("associatedCondensateReservesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_INCREASED_VOLUMES_FDPA))
        .addObject("naturalGasLiquidsRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA))
        .addObject("naturalGasLiquidsReservesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_INCREASED_VOLUMES_FDPA))
        .addObject("salesGasRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.SALES_GAS_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA))
        .addObject("salesGasReservesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_INCREASED_VOLUMES_FDPA))
        .addObject("oilEquivalentRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RECOVERABLE_RESOURCES_INCREASED_VOLUMES_FDPA))
        .addObject("oilEquivalentReservesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_INCREASED_VOLUMES_FDPA));

    return TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_TITLE, submissionDetail);
  }

  private ModelAndView getModelAndViewWithoutErrors(FdpaIncreasedVolumesForm form,
                                                    SubmissionDetail submissionDetail) {
    return getBaseModelAndView(form, submissionDetail)
        .addObject("oilInitiallyInPlaceError", ProbabilityDistributionTypeError.emptyError())
        .addObject("gasInitiallyInPlaceError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilReservesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedCondensateRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedCondensateReservesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("naturalGasLiquidsRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("naturalGasLiquidsReservesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("salesGasRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("salesGasReservesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilEquivalentRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilEquivalentReservesError", ProbabilityDistributionTypeError.emptyError());
  }

  private ModelAndView getModelAndViewWithErrors(FdpaIncreasedVolumesForm form,
                                                 BindingResult bindingResult, SubmissionDetail submissionDetail) {
    return getBaseModelAndView(form, submissionDetail)
        .addObject("oilInitiallyInPlaceError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult, "oilInitiallyInPlace", form.getOilInitiallyInPlace()))
        .addObject("gasInitiallyInPlaceError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult, "gasInitiallyInPlace", form.getGasInitiallyInPlace()))
        .addObject("oilRecoverableResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult, "oilRecoverableResources", form.getOilRecoverableResources()))
        .addObject("oilReservesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult, "oilReserves", form.getOilInitiallyInPlace()))
        .addObject("associatedCondensateRecoverableResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult, "associatedCondensateRecoverableResources",
                form.getAssociatedCondensateRecoverableResources()))
        .addObject("associatedCondensateReservesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult, "associatedCondensateReserves", form.getAssociatedCondensateReserves()))
        .addObject("naturalGasLiquidsRecoverableResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult, "naturalGasLiquidsRecoverableResources", form.getNaturalGasLiquidsRecoverableResources()))
        .addObject("naturalGasLiquidsReservesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult, "naturalGasLiquidsReserves", form.getNaturalGasLiquidsReserves()))
        .addObject("salesGasRecoverableResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult, "salesGasRecoverableResources", form.getSalesGasRecoverableResources()))
        .addObject("salesGasReservesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult, "salesGasReserves", form.getSalesGasReserves()))
        .addObject("oilEquivalentRecoverableResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult, "oilEquivalentRecoverableResources", form.getOilEquivalentRecoverableResources()))
        .addObject("oilEquivalentReservesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult, "oilEquivalentReserves", form.getOilEquivalentReserves()));
  }
}

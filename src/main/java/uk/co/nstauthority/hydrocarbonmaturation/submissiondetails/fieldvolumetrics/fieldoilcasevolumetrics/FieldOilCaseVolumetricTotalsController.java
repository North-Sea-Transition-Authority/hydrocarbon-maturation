package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics;


import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/field-oil-volumetric-totals")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.FIELD_SUBMISSIONS)
@IsFromResponsibleTeam
public class FieldOilCaseVolumetricTotalsController {

  public static final String PAGE_NAME = "Total oil case volumetrics";
  static final List<SubmissionType> VOLUMETRIC_CORRECT_QUESTION_SUBMISSION_TYPES = List.of(SubmissionType.CREATE_FDP,
      SubmissionType.CREATE_FDPA, SubmissionType.CREATE_COP);

  private final SubmissionDetailService submissionDetailService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final FieldOilCaseVolumetricTotalsService fieldOilCaseVolumetricTotalsService;
  private final FieldOilCaseVolumetricTotalsFormValidator fieldOilCaseVolumetricTotalsFormValidator;

  @Autowired
  public FieldOilCaseVolumetricTotalsController(
      SubmissionDetailService submissionDetailService,
      ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
      FieldOilCaseVolumetricTotalsService fieldOilCaseVolumetricTotalsService,
      FieldOilCaseVolumetricTotalsFormValidator fieldOilCaseVolumetricTotalsFormValidator) {
    this.submissionDetailService = submissionDetailService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.fieldOilCaseVolumetricTotalsService = fieldOilCaseVolumetricTotalsService;
    this.fieldOilCaseVolumetricTotalsFormValidator = fieldOilCaseVolumetricTotalsFormValidator;
  }

  @GetMapping
  public ModelAndView getFieldOilCaseVolumetricTotals(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getFieldOilCaseVolumetricTotalsModelAndView(
        fieldOilCaseVolumetricTotalsService.submissionDetailToForm(submissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView saveFieldOilCaseVolumetricTotals(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                       @ModelAttribute("form") OilCaseOutputsForm form,
                                                       BindingResult bindingResult,
                                                       ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    if (!fieldOilCaseVolumetricTotalsFormValidator.isValid(form, bindingResult, submissionDetail, validationType)) {
      return getFieldOilCaseVolumetricTotalsModelAndViewWithErrors(bindingResult, form, submissionDetail);
    } else {
      fieldOilCaseVolumetricTotalsService.saveFieldOilCaseVolumetricTotals(form, submissionDetail);
      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
    }
  }

  private ModelAndView getFieldOilCaseVolumetricTotalsModelAndView(OilCaseOutputsForm form,
                                                                   SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/oilCaseOutputs")
        .addObject("form", form)
        .addObject("resourceType", submissionDetail.getSubmission().getResource().getType().toString())
        .addObject("submissionType", submissionDetail.getSubmissionType().getEnumName())
        .addObject("pageTitle", PAGE_NAME)
        .addObject("oilInPlaceError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilRecoverableReservesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedCondensateRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedCondensateRecoverableReservesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedGasRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedGasRecoverableReservesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilEquivalentRecoverableResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilEquivalentRecoverableReservesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("naturalGasLiquidsResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("naturalGasLiquidsReservesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("salesGasResourcesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("salesGasReservesError", ProbabilityDistributionTypeError.emptyError());

    modelAndView
        .addObject("oilInPlaceTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD))
        .addObject("oilRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESOURCES_FIELD))
        .addObject("associatedCondensateRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESOURCES_FIELD))
        .addObject("associatedGasRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESOURCES_FIELD))
        .addObject("oilEquivalentRecoverableResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESOURCES_FIELD))
        .addObject("naturalGasLiquidsResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESOURCES_FIELD))
        .addObject("salesGasResourcesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESOURCES_FIELD));


    if (!submissionDetail.isConceptSelectReportSubmission()) {
      modelAndView
          .addObject("oilRecoverableReservesTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_OIL_RECOVERABLE_RESERVES_FIELD))
          .addObject("associatedCondensateRecoverableReservesTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_ASSOCIATED_CONDENSATE_RECOVERABLE_RESERVES_FIELD))
          .addObject("associatedGasRecoverableReservesTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.TOTAL_OIL_VOLUMETRICS_ASSOCIATED_GAS_RECOVERABLE_RESERVES_FIELD))
          .addObject("oilEquivalentRecoverableReservesTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_OIL_EQUIVALENT_RECOVERABLE_RESERVES_FIELD))
          .addObject("naturalGasLiquidsReservesTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_NATURAL_GAS_LIQUIDS_RESERVES_FIELD))
          .addObject("salesGasReservesTemplate",
              probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                  ProbabilityDistributionFieldInstance.TOTAL_VOLUMETRICS_SALES_GAS_RESERVES_FIELD));
    }

    if (VOLUMETRIC_CORRECT_QUESTION_SUBMISSION_TYPES.contains(submissionDetail.getSubmissionType())) {
      var fieldSubmissionCarriedForward = submissionDetailService.hasFieldSubmissionDataBeenCarriedForward(
          submissionDetail);
      modelAndView.addObject("fieldSubmissionCarriedForward", fieldSubmissionCarriedForward);
    }

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_NAME, submissionDetail);

    return modelAndView;
  }

  private ModelAndView getFieldOilCaseVolumetricTotalsModelAndViewWithErrors(BindingResult bindingResult,
                                                                             OilCaseOutputsForm form,
                                                                             SubmissionDetail submissionDetail) {
    var modelAndView = getFieldOilCaseVolumetricTotalsModelAndView(form, submissionDetail);

    modelAndView
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
        .addObject("associatedCondensateRecoverableResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "associatedCondensateRecoverableResources",
                form.getAssociatedCondensateRecoverableResources()))
        .addObject("associatedGasRecoverableResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "associatedGasRecoverableResources",
                form.getAssociatedGasRecoverableResources()))
        .addObject("oilEquivalentRecoverableResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "oilEquivalentRecoverableResources",
                form.getOilEquivalentRecoverableResources()))
        .addObject("naturalGasLiquidsResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "naturalGasLiquidsResources",
                form.getNaturalGasLiquidsResources()))
        .addObject("salesGasResourcesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult,
                "salesGasResources",
                form.getSalesGasResources()));

    if (!submissionDetail.isConceptSelectReportSubmission()) {
      modelAndView
          .addObject("oilRecoverableReservesError",
              probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                  bindingResult,
                  "oilRecoverableReserves",
                  form.getOilRecoverableReserves()))
          .addObject("associatedCondensateRecoverableReservesError",
              probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                  bindingResult,
                  "associatedCondensateRecoverableReserves",
                  form.getAssociatedCondensateRecoverableReserves()))
          .addObject("associatedGasRecoverableReservesError",
              probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                  bindingResult,
                  "associatedGasRecoverableReserves",
                  form.getAssociatedGasRecoverableReserves()))
          .addObject("oilEquivalentRecoverableReservesError",
              probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                  bindingResult,
                  "oilEquivalentRecoverableReserves",
                  form.getOilEquivalentRecoverableReserves()))
          .addObject("naturalGasLiquidsReservesError",
              probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                  bindingResult,
                  "naturalGasLiquidsReserves",
                  form.getNaturalGasLiquidsReserves()))
          .addObject("salesGasReservesError",
              probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                  bindingResult,
                  "salesGasReserves",
                  form.getSalesGasReserves()));
    }

    return modelAndView;
  }
}

package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpapromotedvolumes;

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
@RequestMapping("/submission-details/{submissionDetailId}/fdpa-promoted-volumes")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.FDPA_SUBMISSIONS)
@IsFromResponsibleTeam
public class FdpaVolumesController {

  static final String PAGE_TITLE = "FDPA promoted volumes";
  private final FdpaPromotedVolumesService fdpaPromotedVolumesService;
  private final FdpaPromotedVolumesFormValidator fdpaPromotedVolumesFormValidator;
  private final SubmissionDetailService submissionDetailService;
  private final ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;

  @Autowired
  public FdpaVolumesController(
      FdpaPromotedVolumesService fdpaPromotedVolumesService,
      FdpaPromotedVolumesFormValidator fdpaPromotedVolumesFormValidator,
      SubmissionDetailService submissionDetailService,
      ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService,
      ProbabilityDistributionTemplateService probabilityDistributionTemplateService) {
    this.fdpaPromotedVolumesService = fdpaPromotedVolumesService;
    this.fdpaPromotedVolumesFormValidator = fdpaPromotedVolumesFormValidator;
    this.submissionDetailService = submissionDetailService;
    this.reservoirHydrocarbonCaseService = reservoirHydrocarbonCaseService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
  }

  @GetMapping
  public ModelAndView getFdpaPromotedVolumes(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var fdpaPromotedVolumesForm = fdpaPromotedVolumesService.submissionDetailToForm(submissionDetail);

    return getModelAndViewWithoutErrors(fdpaPromotedVolumesForm, submissionDetail);
  }

  @PostMapping
  public ModelAndView saveFdpaPromotedVolumes(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                              @ModelAttribute("form") FdpaPromotedVolumesForm form,
                                              BindingResult bindingResult, ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    if (fdpaPromotedVolumesFormValidator.isValid(form, bindingResult, submissionDetail, validationType)) {
      fdpaPromotedVolumesService.saveFdpaPromotedVolumes(form, submissionDetail);
      return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId, null));
    } else {
      return getModelAndViewWithErrors(form, bindingResult, submissionDetail);
    }
  }

  private ModelAndView getBaseModelAndView(FdpaPromotedVolumesForm form,
                                           SubmissionDetail submissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/fdpaPromotedVolumes")
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("form", form)
        .addObject("hydrocarbonCase", reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(submissionDetail))
        .addObject("oilReservesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_RESERVES_PROMOTED_VOLUMES_FDPA))
        .addObject("associatedCondensateReservesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.ASSOCIATED_CONDENSATE_RESERVES_PROMOTED_VOLUMES_FDPA))
        .addObject("naturalGasLiquidsReservesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.NATURAL_GAS_LIQUIDS_RESERVES_PROMOTED_VOLUMES_FDPA))
        .addObject("salesGasReservesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.SALES_GAS_RESERVES_PROMOTED_VOLUMES_FDPA))
        .addObject("oilEquivalentReservesTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_EQUIVALENT_RESERVES_PROMOTED_VOLUMES_FDPA));

    return TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(modelAndView, PAGE_TITLE, submissionDetail);
  }

  private ModelAndView getModelAndViewWithoutErrors(FdpaPromotedVolumesForm form,
                                                    SubmissionDetail submissionDetail) {
    return getBaseModelAndView(form, submissionDetail)
        .addObject("oilReservesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("associatedCondensateReservesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("naturalGasLiquidsReservesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("salesGasReservesError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilEquivalentReservesError", ProbabilityDistributionTypeError.emptyError());
  }

  private ModelAndView getModelAndViewWithErrors(FdpaPromotedVolumesForm form,
                                                 BindingResult bindingResult, SubmissionDetail submissionDetail) {
    return getBaseModelAndView(form, submissionDetail)
        .addObject("oilReservesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult, "oilReserves", form.getOilReserves()))
        .addObject("associatedCondensateReservesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult, "associatedCondensateReserves", form.getAssociatedCondensateReserves()))
        .addObject("naturalGasLiquidsReservesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult, "naturalGasLiquidsReserves", form.getNaturalGasLiquidsReserves()))
        .addObject("salesGasReservesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult, "salesGasReserves", form.getSalesGasReserves()))
        .addObject("oilEquivalentReservesError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult, "oilEquivalentReserves", form.getOilEquivalentReserves()));
  }
}

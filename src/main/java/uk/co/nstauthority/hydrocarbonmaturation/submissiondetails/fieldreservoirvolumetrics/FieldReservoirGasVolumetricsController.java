package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/" +
    "reservoir-submission-details/{reservoirSubmissionDetailId}/field-gas-volumetrics")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.FIELD_SUBMISSIONS)
@IsFromResponsibleTeam
public class FieldReservoirGasVolumetricsController {
  public static final String GAS_VOLUMETRICS_PAGE_TITLE = "Gas volumetrics";
  static final List<SubmissionType> INITIALLY_IN_PLACE_QUESTION_SUBMISSION_TYPES = List.of(SubmissionType.CREATE_FDP,
      SubmissionType.CREATE_FDPA, SubmissionType.CREATE_COP);

  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final FieldReservoirVolumetricsFormService fieldReservoirVolumetricsFormService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final FieldReservoirVolumetricsFormValidator fieldReservoirVolumetricsFormValidator;

  @Autowired
  public FieldReservoirGasVolumetricsController(
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      FieldReservoirVolumetricsFormService fieldReservoirVolumetricsFormService,
      ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
      FieldReservoirVolumetricsFormValidator fieldReservoirVolumetricsFormValidator) {

    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.fieldReservoirVolumetricsFormService = fieldReservoirVolumetricsFormService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.fieldReservoirVolumetricsFormValidator = fieldReservoirVolumetricsFormValidator;
  }

  @GetMapping
  public ModelAndView getFieldGasVolumetrics(
      @PathVariable("submissionDetailId") UUID submissionDetailId,
      @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId) {

    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);

    return getFieldGasVolumetricsModelAndView(
        fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail),
        reservoirSubmissionDetail
    );
  }

  @PostMapping
  public ModelAndView saveFieldGasVolumetrics(
      @PathVariable("submissionDetailId") UUID submissionDetailId,
      @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
      @ModelAttribute("form") FieldReservoirVolumetricsForm form,
      BindingResult bindingResult,
      ValidationType validationType) {

    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);

    if (!fieldReservoirVolumetricsFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail,
        validationType)) {
      return getFieldGasVolumetricsModelAndViewWithErrors(bindingResult, form, reservoirSubmissionDetail);
    } else {
      fieldReservoirVolumetricsFormService.saveFieldReservoirVolumetrics(form, reservoirSubmissionDetail);
      return ReverseRouter.redirect(on(ReservoirTaskListController.class)
          .getReservoirTaskList(submissionDetailId));
    }
  }

  private ModelAndView getFieldGasVolumetricsModelAndView(
      FieldReservoirVolumetricsForm form, ReservoirSubmissionDetail reservoirSubmissionDetail) {

    var modelAndView = new ModelAndView("remi/submissiondetails/fieldVolumetrics")
        .addObject("pageTitle", GAS_VOLUMETRICS_PAGE_TITLE)
        .addObject("form", form)
        .addObject("hydrocarbonType", HydrocarbonsDiscovered.GAS_ONLY)
        .addObject("gasInPlaceError", ProbabilityDistributionTypeError.emptyError())
        .addObject("gasInPlaceTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.GAS_VOLUMETRICS_GAS_IN_PLACE_FIELD));

    var submissionType = reservoirSubmissionDetail.getSubmissionDetail().getSubmissionType();
    if (INITIALLY_IN_PLACE_QUESTION_SUBMISSION_TYPES.contains(submissionType)) {
      var reservoirCarriedForward = reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(
          reservoirSubmissionDetail.getReservoir()
      );
      modelAndView.addObject("reservoirCarriedForward", reservoirCarriedForward);
    }

    return TaskListBreadcrumbUtil.addReservoirTaskListBreadcrumbsToModelAndView(
        modelAndView, GAS_VOLUMETRICS_PAGE_TITLE, reservoirSubmissionDetail
    );
  }

  private ModelAndView getFieldGasVolumetricsModelAndViewWithErrors(BindingResult bindingResult,
                                                                    FieldReservoirVolumetricsForm form,
                                                                    ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var modelAndView = getFieldGasVolumetricsModelAndView(form, reservoirSubmissionDetail);

    modelAndView.addObject("gasInPlaceError",
        probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
            bindingResult,
            "gasInPlace",
            form.getGasInPlace()));

    return modelAndView;
  }
}

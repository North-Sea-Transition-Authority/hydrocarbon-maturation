package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldreservoirvolumetrics;

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
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplateService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbons;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/" +
    "reservoir-submission-details/{reservoirSubmissionDetailId}/oil-volumetrics")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.FIELD_SUBMISSIONS)
@IsFromResponsibleTeam
public class FieldReservoirOilVolumetricsController {
  public static final String OIL_VOLUMETRICS_PAGE_TITLE = "Oil volumetrics";

  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final GeneralReservoirInformationService generalReservoirInformationService;
  private final FieldReservoirVolumetricsFormService fieldReservoirVolumetricsFormService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final FieldReservoirVolumetricsFormValidator fieldReservoirVolumetricsFormValidator;

  @Autowired
  public FieldReservoirOilVolumetricsController(
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      GeneralReservoirInformationService generalReservoirInformationService,
      FieldReservoirVolumetricsFormService fieldReservoirVolumetricsFormService,
      ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
      FieldReservoirVolumetricsFormValidator fieldReservoirVolumetricsFormValidator) {

    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.generalReservoirInformationService = generalReservoirInformationService;
    this.fieldReservoirVolumetricsFormService = fieldReservoirVolumetricsFormService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.fieldReservoirVolumetricsFormValidator = fieldReservoirVolumetricsFormValidator;
  }

  @GetMapping
  public ModelAndView getFieldReservoirVolumetrics(
      @PathVariable("submissionDetailId") UUID submissionDetailId,
      @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId) {

    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);

    return getFieldReservoirVolumetricsModelAndView(
        fieldReservoirVolumetricsFormService.getFieldReservoirVolumetricsForm(reservoirSubmissionDetail),
        reservoirSubmissionDetail
    );
  }

  @PostMapping
  public ModelAndView saveFieldReservoirVolumetrics(
      @PathVariable("submissionDetailId") UUID submissionDetailId,
      @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
      @ModelAttribute("form") FieldReservoirVolumetricsForm form,
      BindingResult bindingResult,
      ValidationType validationType) {

    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);

    if (!fieldReservoirVolumetricsFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType)) {
      return getFieldReservoirVolumetricsModelAndViewWithErrors(bindingResult, form, reservoirSubmissionDetail);
    } else {
      fieldReservoirVolumetricsFormService.saveFieldReservoirVolumetrics(form, reservoirSubmissionDetail);
      return ReverseRouter.redirect(on(ReservoirTaskListController.class)
          .getReservoirTaskList(submissionDetailId, null));
    }
  }

  private ModelAndView getFieldReservoirVolumetricsModelAndView(
      FieldReservoirVolumetricsForm form, ReservoirSubmissionDetail reservoirSubmissionDetail) {

    var hydrocarbonCase = generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)
        .map(ReservoirHydrocarbons::getHydrocarbonsDiscovered)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("There are no hydrocarbon types for the reservoir submission with id %s"
                .formatted(reservoirSubmissionDetail.getId())));

    var modelAndView = new ModelAndView("remi/submissiondetails/fieldVolumetrics")
        .addObject("pageTitle", OIL_VOLUMETRICS_PAGE_TITLE)
        .addObject("form", form)
        .addObject("hydrocarbonType", hydrocarbonCase)
        .addObject("oilInPlaceError", ProbabilityDistributionTypeError.emptyError())
        .addObject("oilInPlaceTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.OIL_VOLUMETRICS_OIL_IN_PLACE_FIELD));

    return TaskListBreadcrumbUtil.addReservoirTaskListBreadcrumbsToModelAndView(
        modelAndView, OIL_VOLUMETRICS_PAGE_TITLE, reservoirSubmissionDetail
    );
  }

  private ModelAndView getFieldReservoirVolumetricsModelAndViewWithErrors(BindingResult bindingResult,
                                                                          FieldReservoirVolumetricsForm form,
                                                                          ReservoirSubmissionDetail reservoirSubmissionDetail) {

    var modelAndView = getFieldReservoirVolumetricsModelAndView(form, reservoirSubmissionDetail);

    modelAndView.addObject("oilInPlaceError",
        probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
            bindingResult,
            "oilInPlace",
            form.getOilInPlace()));

    return modelAndView;
  }
}

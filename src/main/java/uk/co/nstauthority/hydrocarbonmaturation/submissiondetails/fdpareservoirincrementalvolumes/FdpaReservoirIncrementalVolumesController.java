package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpareservoirincrementalvolumes;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.UUID;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics.ReservoirHydrocarbonCaseService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/" +
    "reservoir-submission-details/{reservoirSubmissionDetailId}/fdpa-incremental-volumes")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.FDPA_SUBMISSIONS)
@IsFromResponsibleTeam
public class FdpaReservoirIncrementalVolumesController {

  static final String PAGE_TITLE = "FDPA incremental in place volumes";

  private final ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final FdpaReservoirIncrementalVolumesService fdpaReservoirIncrementalVolumesService;
  private final ProbabilityDistributionTemplateService probabilityDistributionTemplateService;
  private final FdpaReservoirIncrementalVolumesFormValidator incrementalVolumesFormValidator;

  @Autowired
  public FdpaReservoirIncrementalVolumesController(
      ReservoirHydrocarbonCaseService reservoirHydrocarbonCaseService,
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
      FdpaReservoirIncrementalVolumesService fdpaReservoirIncrementalVolumesService,
      ProbabilityDistributionTemplateService probabilityDistributionTemplateService,
      FdpaReservoirIncrementalVolumesFormValidator incrementalVolumesFormValidator) {
    this.reservoirHydrocarbonCaseService = reservoirHydrocarbonCaseService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.fdpaReservoirIncrementalVolumesService = fdpaReservoirIncrementalVolumesService;
    this.probabilityDistributionTemplateService = probabilityDistributionTemplateService;
    this.incrementalVolumesFormValidator = incrementalVolumesFormValidator;
  }

  @GetMapping
  public ModelAndView getIncrementalVolumes(
      @PathVariable("submissionDetailId") UUID submissionDetailId,
      @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId) {
    var reservoirSubmissionDetail = getCarriedForwardReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    var form = fdpaReservoirIncrementalVolumesService.submissionDetailToForm(reservoirSubmissionDetail);
    return getModelAndViewWithoutErrors(form, reservoirSubmissionDetail);
  }

  @PostMapping
  public ModelAndView saveIncrementalVolumes(
      @PathVariable("submissionDetailId") UUID submissionDetailId,
      @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
      @ModelAttribute("form") FdpaReservoirIncrementalVolumesForm form,
      BindingResult bindingResult, ValidationType validationType) {
    var reservoirSubmissionDetail = getCarriedForwardReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);

    if (incrementalVolumesFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType)) {
      fdpaReservoirIncrementalVolumesService.saveFdpaReservoirIncrementalVolume(form, reservoirSubmissionDetail);
      return ReverseRouter.redirect(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetailId));
    } else {
      return getModelAndViewWithErrors(form, reservoirSubmissionDetail, bindingResult);
    }
  }

  private ModelAndView getBaseModelAndView(FdpaReservoirIncrementalVolumesForm form,
                                           ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var hydrocarbonsDiscovered = reservoirHydrocarbonCaseService.getHydrocarbonsDiscovered(
        List.of(reservoirSubmissionDetail));

    return new ModelAndView("remi/submissiondetails/fdpaReservoirIncrementalVolumes")
        .addObject("pageTitle", PAGE_TITLE)
        .addObject("form", form)
        .addObject("hydrocarbonCase", hydrocarbonsDiscovered)
        .addObject("incrementalIncreaseOilTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.RESERVOIR_OIL_INITIALLY_IN_PLACE_INCREASE_FDPA))
        .addObject("incrementalIncreaseGasTemplate",
            probabilityDistributionTemplateService.getProbabilityDistributionTemplateForInstance(
                ProbabilityDistributionFieldInstance.RESERVOIR_GAS_INITIALLY_IN_PLACE_INCREASE_FDPA));
  }

  private ModelAndView getModelAndViewWithErrors(FdpaReservoirIncrementalVolumesForm form,
                                                 ReservoirSubmissionDetail reservoirSubmissionDetail,
                                                 BindingResult bindingResult) {
    return getBaseModelAndView(form, reservoirSubmissionDetail)
        .addObject("incrementalIncreaseOilError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult, "incrementalIncreaseOil", form.getIncrementalIncreaseOil()))
        .addObject("incrementalIncreaseGasError",
            probabilityDistributionTemplateService.getProbabilityDistributionTypeError(
                bindingResult, "incrementalIncreaseGas", form.getIncrementalIncreaseGas()));
  }

  private ModelAndView getModelAndViewWithoutErrors(FdpaReservoirIncrementalVolumesForm form,
                                                    ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return getBaseModelAndView(form, reservoirSubmissionDetail)
        .addObject("incrementalIncreaseOilError", ProbabilityDistributionTypeError.emptyError())
        .addObject("incrementalIncreaseGasError", ProbabilityDistributionTypeError.emptyError());
  }

  private ReservoirSubmissionDetail getCarriedForwardReservoirSubmissionDetailOrThrow(
      UUID reservoirSubmissionDetailId) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    var reservoir = reservoirSubmissionDetail.getReservoir();

    var reservoirWasCarriedForward = reservoirSubmissionDetailPersistenceService.reservoirWasCarriedForward(reservoir);
    if (BooleanUtils.isFalse(reservoirWasCarriedForward)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Reservoir %s data was not carried forward".formatted(
          reservoir.getId()));
    }
    return reservoirSubmissionDetail;
  }
}

package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;


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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/" +
    "reservoir-submission-details/{reservoirSubmissionDetailId}/gas-fluid-properties")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.DISCOVERY_AND_FIELD)
@IsFromResponsibleTeam
public class GasFluidPropertyController {

  private final GasFluidPropertyFormService gasFluidPropertyFormService;
  private final GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final GasFluidPropertyFormValidator gasFluidPropertyFormValidator;

  public static final String PAGE_NAME = "Gas properties";

  @Autowired
  public GasFluidPropertyController(GasFluidPropertyFormService gasFluidPropertyFormService,
                                    GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService,
                                    ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
                                    GasFluidPropertyFormValidator gasFluidPropertyFormValidator) {
    this.gasFluidPropertyFormService = gasFluidPropertyFormService;
    this.gasFluidPropertyPersistenceService = gasFluidPropertyPersistenceService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.gasFluidPropertyFormValidator = gasFluidPropertyFormValidator;
  }

  @GetMapping
  public ModelAndView getGasFluidProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                            @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);

    return getGasFluidPropertiesModelAndView(
        gasFluidPropertyFormService.getGasFluidPropertyForm(reservoirSubmissionDetail),
        reservoirSubmissionDetail
    );
  }

  @PostMapping
  public ModelAndView saveGasFluidProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                             @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
                                             @ModelAttribute("form") GasFluidPropertyForm form,
                                             BindingResult bindingResult, ValidationType validationType) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);

    if (!gasFluidPropertyFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType)) {
      return getGasFluidPropertiesModelAndView(form, reservoirSubmissionDetail);
    } else {
      gasFluidPropertyPersistenceService.saveGasFluidProperty(form, reservoirSubmissionDetail);
      return ReverseRouter.redirect(on(ReservoirTaskListController.class).getReservoirTaskList(submissionDetailId));
    }
  }

  private ModelAndView getGasFluidPropertiesModelAndView(GasFluidPropertyForm form,
                                                         ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/gasFluidProperties")
        .addObject("form", form);

    TaskListBreadcrumbUtil.addReservoirTaskListBreadcrumbsToModelAndView(modelAndView, PAGE_NAME, reservoirSubmissionDetail);

    return modelAndView;
  }
}

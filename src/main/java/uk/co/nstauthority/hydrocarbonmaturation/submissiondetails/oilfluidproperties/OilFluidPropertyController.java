package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

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
    "reservoir-submission-details/{reservoirSubmissionDetailId}/oil-fluid-properties")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.DISCOVERY_AND_FIELD)
@IsFromResponsibleTeam
public class OilFluidPropertyController {

  private final OilFluidPropertyFormService oilFluidPropertyFormService;
  private final OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final OilFluidPropertyFormValidator oilPropertiesValidator;

  public static final String PAGE_NAME = "Oil properties";

  @Autowired
  public OilFluidPropertyController(OilFluidPropertyFormService oilFluidPropertyFormService,
                                    OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService,
                                    ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
                                    OilFluidPropertyFormValidator oilPropertiesValidator) {
    this.oilFluidPropertyFormService = oilFluidPropertyFormService;
    this.oilFluidPropertyPersistenceService = oilFluidPropertyPersistenceService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.oilPropertiesValidator = oilPropertiesValidator;
  }

  @GetMapping
  public ModelAndView getOilFluidProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                            @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);

    return getOilFluidPropertiesModelAndView(
        oilFluidPropertyFormService.getOilFluidPropertyForm(reservoirSubmissionDetail),
        reservoirSubmissionDetail
    );
  }

  @PostMapping
  public ModelAndView saveOilFluidProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                             @PathVariable UUID reservoirSubmissionDetailId,
                                             @ModelAttribute("form") OilFluidPropertyForm form,
                                             BindingResult bindingResult, ValidationType validationType) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);

    if (!oilPropertiesValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType)) {
      return getOilFluidPropertiesModelAndView(form, reservoirSubmissionDetail);
    } else {
      oilFluidPropertyPersistenceService.saveOilFluidProperty(form, reservoirSubmissionDetail);
      return ReverseRouter.redirect(on(ReservoirTaskListController.class)
          .getReservoirTaskList(submissionDetailId, null));
    }
  }

  private ModelAndView getOilFluidPropertiesModelAndView(OilFluidPropertyForm form,
                                                         ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/oilFluidProperties")
        .addObject("form", form);

    TaskListBreadcrumbUtil.addReservoirTaskListBreadcrumbsToModelAndView(modelAndView, PAGE_NAME, reservoirSubmissionDetail);

    return modelAndView;
  }
}

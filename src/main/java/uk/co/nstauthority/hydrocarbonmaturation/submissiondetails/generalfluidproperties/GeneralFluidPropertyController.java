package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;


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
    "reservoir-submission-details/{reservoirSubmissionDetailId}/general-fluid-properties")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.DISCOVERY_AND_FIELD)
@IsFromResponsibleTeam
public class GeneralFluidPropertyController {

  private final GeneralFluidPropertyFormService generalFluidPropertyFormService;
  private final GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final GeneralFluidPropertyFormValidator generalFluidPropertyFormValidator;

  public static final String PAGE_NAME = "General properties";

  @Autowired
  public GeneralFluidPropertyController(GeneralFluidPropertyFormService generalFluidPropertyFormService,
                                        GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService,
                                        ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
                                        GeneralFluidPropertyFormValidator generalFluidPropertyFormValidator) {
    this.generalFluidPropertyFormService = generalFluidPropertyFormService;
    this.generalFluidPropertyPersistenceService = generalFluidPropertyPersistenceService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.generalFluidPropertyFormValidator = generalFluidPropertyFormValidator;
  }

  @GetMapping
  public ModelAndView getGeneralFluidProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
            .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    return getGeneralFluidPropertiesModelAndView(
        generalFluidPropertyFormService.getGeneralFluidPropertyForm(reservoirSubmissionDetail), reservoirSubmissionDetail);
  }

  @PostMapping
  public ModelAndView saveGeneralFluidProperties(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                 @PathVariable("reservoirSubmissionDetailId") UUID reservoirSubmissionDetailId,
                                                 @ModelAttribute("form") GeneralFluidPropertyForm form,
                                                 BindingResult bindingResult,
                                                 ValidationType validationType) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
            .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);

    if (!generalFluidPropertyFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType)) {
      return getGeneralFluidPropertiesModelAndView(form, reservoirSubmissionDetail);
    } else {
      generalFluidPropertyPersistenceService.saveGeneralFluidProperty(form, reservoirSubmissionDetail);
      return ReverseRouter.redirect(on(ReservoirTaskListController.class)
          .getReservoirTaskList(submissionDetailId));
    }
  }

  private ModelAndView getGeneralFluidPropertiesModelAndView(GeneralFluidPropertyForm form,
                                                             ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var modelAndView = new ModelAndView("remi/submissiondetails/generalFluidProperties")
        .addObject("form", form)
        .addObject("isCop", reservoirSubmissionDetail.getSubmissionDetail().isCessationOfProductionSubmission());

    TaskListBreadcrumbUtil.addReservoirTaskListBreadcrumbsToModelAndView(modelAndView, PAGE_NAME, reservoirSubmissionDetail);

    return modelAndView;
  }
}

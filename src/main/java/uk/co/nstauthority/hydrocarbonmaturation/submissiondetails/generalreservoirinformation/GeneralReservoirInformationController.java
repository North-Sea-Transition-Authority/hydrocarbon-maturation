package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
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
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionSectionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.reservoir.ReservoirTaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/" +
    "reservoir-submission-details/{reservoirSubmissionDetailId}/general-reservoir-information")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.DISCOVERY_AND_FIELD)
@IsFromResponsibleTeam
public class GeneralReservoirInformationController {

  private final GeneralReservoirInformationFormValidator generalReservoirInformationFormValidator;
  private final GeneralReservoirInformationService generalReservoirInformationService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  public static final String PAGE_NAME = "Reservoir details";

  public GeneralReservoirInformationController(
      GeneralReservoirInformationFormValidator generalReservoirInformationFormValidator,
      GeneralReservoirInformationService generalReservoirInformationService,
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.generalReservoirInformationFormValidator = generalReservoirInformationFormValidator;
    this.generalReservoirInformationService = generalReservoirInformationService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
  }

  @GetMapping
  public ModelAndView getGeneralReservoirInformationPage(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                         @PathVariable("reservoirSubmissionDetailId")
                                                         UUID reservoirSubmissionDetailId) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    return getGeneralReservoirInformationModelAndView(
        generalReservoirInformationService.getGeneralReservoirInformationForm(reservoirSubmissionDetail),
        reservoirSubmissionDetail);
  }

  @PostMapping
  public ModelAndView saveGeneralReservoirInformation(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                      @PathVariable("reservoirSubmissionDetailId")
                                                      UUID reservoirSubmissionDetailId,
                                                      @ModelAttribute("form") GeneralReservoirInformationForm form,
                                                      BindingResult bindingResult,
                                                      ValidationType validationType) {
    var reservoirSubmissionDetail = reservoirSubmissionDetailPersistenceService
        .getExtantReservoirSubmissionDetailOrThrow(reservoirSubmissionDetailId);
    if (!generalReservoirInformationFormValidator.isValid(form, bindingResult, reservoirSubmissionDetail, validationType)) {
      return getGeneralReservoirInformationModelAndView(form, reservoirSubmissionDetail);
    } else {
      generalReservoirInformationService.saveGeneralReservoirInformation(form, reservoirSubmissionDetail);
      return ReverseRouter.redirect(on(ReservoirTaskListController.class)
          .getReservoirTaskList(submissionDetailId));
    }
  }

  private ModelAndView getGeneralReservoirInformationModelAndView(GeneralReservoirInformationForm form,
                                                                  ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var isFieldSubmission = submissionDetail.isFieldSubmission();
    var submissionTypeDisplay = submissionDetail.getSubmissionType().getTypeName().toLowerCase();
    var modelAndView = new ModelAndView("remi/submissiondetails/generalreservoirinformation/generalReservoirInformation")
        .addObject("form", form)
        .addObject("pageTitle",
            "%s reservoir".formatted(isFieldSubmission ? "Field" : "Discovery"))
        .addObject("isFieldSubmission", isFieldSubmission)
        .addObject("hydrocarbonsDiscovered",
            DisplayableEnumOptionUtil.getDisplayableOptions(HydrocarbonsDiscovered.class))
        .addObject("reservoirSubmissionSectionTypeOptions",
            ReservoirSubmissionSectionType.getReservoirSectionSubmissionTypeOptions())
        .addObject("isCessationOfProduction", submissionDetail.isCessationOfProductionSubmission())
        .addObject("submissionTypeDisplay", submissionTypeDisplay);

    if (isFieldSubmission) {
      modelAndView
          .addObject("alreadyAddedAssociatedDiscoveryReservoirs", generalReservoirInformationService
              .getPreselectedAssociatedDiscoveryReservoirs(form.getAssociatedDiscoveryReservoirs()))
          .addObject("discoveryReservoirsApiEndpoint",
              SearchSelectorService.route(on(ResourceRestController.class)
                  .searchDiscoveryReservoirs(null, submissionDetail.getId().toString())))
          .addObject("resourceNames",
              generalReservoirInformationService
                  .getDiscoveryResourceNamesForFieldResourceLinkFromSubmissionDetail(submissionDetail));
    }

    return TaskListBreadcrumbUtil.addReservoirTaskListBreadcrumbsToModelAndView(
        modelAndView, PAGE_NAME, reservoirSubmissionDetail
    );
  }
}

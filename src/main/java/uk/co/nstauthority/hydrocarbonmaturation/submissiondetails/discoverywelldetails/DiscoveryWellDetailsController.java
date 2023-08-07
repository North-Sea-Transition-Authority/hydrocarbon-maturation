package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

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
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellRestController;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListBreadcrumbUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/discovery-well-details")
@HasSubmissionStatus(statuses = SubmissionDetailStatus.DRAFT)
@HasSubmissionType(submissionTypes = SecuritySubmissionTypes.DISCOVERY_TYPES)
@IsFromResponsibleTeam
public class DiscoveryWellDetailsController {

  private final DiscoveryWellDetailsFormService discoveryWellDetailsFormService;
  private final ControllerHelperService controllerHelperService;
  private final SubmissionDetailService submissionDetailService;
  private final DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  public static final String PAGE_NAME = "Discovery well details";

  @Autowired
  public DiscoveryWellDetailsController(DiscoveryWellDetailsFormService discoveryWellDetailsFormService,
                                        ControllerHelperService controllerHelperService,
                                        SubmissionDetailService submissionDetailService,
                                        DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService) {
    this.discoveryWellDetailsFormService = discoveryWellDetailsFormService;
    this.controllerHelperService = controllerHelperService;
    this.submissionDetailService = submissionDetailService;
    this.discoveryWellDetailsPersistenceService = discoveryWellDetailsPersistenceService;
  }

  @GetMapping
  public ModelAndView getDiscoveryWellDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                              @ModelAttribute("form") DiscoveryWellDetailsForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    return getDiscoveryWellDetailsModelAndView(
        discoveryWellDetailsFormService.getDiscoveryWellDetailsForm(submissionDetail), submissionDetail);
  }

  @PostMapping
  public ModelAndView saveDiscoveryWellDetails(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                               @ModelAttribute("form") DiscoveryWellDetailsForm form,
                                               BindingResult bindingResult,
                                               ValidationType validationType) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    bindingResult = discoveryWellDetailsFormService.validate(form, bindingResult, validationType, submissionDetail);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getDiscoveryWellDetailsModelAndView(form, submissionDetail),
        () -> {
          discoveryWellDetailsPersistenceService.saveDiscoveryWellDetails(form, submissionDetail);
          return ReverseRouter.redirect(on(TaskListController.class).getTaskList(submissionDetailId));
        }
    );
  }

  private ModelAndView getDiscoveryWellDetailsModelAndView(DiscoveryWellDetailsForm form, SubmissionDetail submissionDetail) {
    var modelAndView =  new ModelAndView("remi/submissiondetails/wellDrillDetails/discoveryWellDrillDetails")
        .addObject("form", form)
        .addObject("datumTypes", DisplayableEnumOptionUtil.getDisplayableOptions(ReservoirDatum.class))
        .addObject("discoveryStatus", DisplayableEnumOptionUtil.getDisplayableOptions(DiscoveryStatus.class))
        .addObject("hydrocarbonsDiscovered", DisplayableEnumOptionUtil.getDisplayableOptions(HydrocarbonsDiscovered.class))
        .addObject("longitudeHemisphere", Hemisphere.getLongitudeHemisphere())
        .addObject("wellEndpoint", SearchSelectorService.route(on(WellRestController.class).searchWonsWells(null)))
        .addObject("well", discoveryWellDetailsFormService.getPreSelectedWell(form.getWellDetailsForm().getWell()));

    TaskListBreadcrumbUtil.addTaskListAndWorkAreaToModelAndView(
        modelAndView, PAGE_NAME, submissionDetail);
    return modelAndView;
  }
}

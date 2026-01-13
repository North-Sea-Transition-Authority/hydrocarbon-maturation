package uk.co.nstauthority.hydrocarbonmaturation.regulatorreportinginformation;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityType;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchProspectAndDiscoveryRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;

@Controller
@RequestMapping("/view-resource-record/resource/{resourceId}/regulator-reporting-information/")
@HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.NORTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.SOUTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.ONSHORE_AREA)
public class RegulatorReportingInformationController {

  public static final String PAGE_TITLE = "Regulator reporting information";

  private final ResourceService resourceService;
  private final RegulatorReportingInformationService regulatorReportingInformationService;
  private final SubmissionResourceService submissionResourceService;
  private final SubmissionDetailService submissionDetailService;
  private final RegulatorReportingInformationValidator regulatorReportingInformationValidator;

  @Autowired
  public RegulatorReportingInformationController(ResourceService resourceService,
                                                 RegulatorReportingInformationService regulatorReportingInformationService,
                                                 SubmissionResourceService submissionResourceService,
                                                 SubmissionDetailService submissionDetailService,
                                                 RegulatorReportingInformationValidator regulatorReportingInformationValidator) {
    this.resourceService = resourceService;
    this.regulatorReportingInformationService = regulatorReportingInformationService;
    this.submissionResourceService = submissionResourceService;
    this.submissionDetailService = submissionDetailService;
    this.regulatorReportingInformationValidator = regulatorReportingInformationValidator;
  }

  @GetMapping
  public ModelAndView renderPage(@PathVariable UUID resourceId, User user) {
    var resource = resourceService.getResourceById(resourceId)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("Could not find resource with id %s"
                .formatted(resourceId.toString())));

    if (Boolean.FALSE.equals(resource.getActive())) {
      throw new RemiEntityNotFoundException("Resource with id %s is not active".formatted(resourceId.toString()));
    }

    var form = regulatorReportingInformationService.getForm(resource);
    return getModelAndView(form, resource);
  }

  @PostMapping
  public ModelAndView submitPage(@PathVariable UUID resourceId,
                                 @ModelAttribute("form") RegulatorReportingInformationForm form,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 User user) {
    var resource = resourceService.getResourceById(resourceId)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("Could not find resource with id %s"
                .formatted(resourceId.toString())));

    if (Boolean.FALSE.equals(resource.getActive())) {
      throw new RemiEntityNotFoundException("Resource with id %s is not active".formatted(resourceId.toString()));
    }

    if (!regulatorReportingInformationValidator.isValid(form, bindingResult)) {
      return getModelAndView(form, resource);
    }

    regulatorReportingInformationService.saveRegulatorReportingInformationForm(form, resource);

    NotificationBannerUtil.addSuccessNotification(redirectAttributes,
        "Success",
        "The regulator reporting information has been updated for the resource %s".formatted(form.getResourceName()));

    return ReverseRouter.redirect(on(ViewResourceRecordController.class).showResourceRecord(resource.getId(), null));
  }

  private ModelAndView getModelAndView(RegulatorReportingInformationForm form, Resource resource) {
    var submissionOnResource = submissionDetailService
        .getLatestApprovedSubmissionDetailForResource(resource)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("Could not find find an accepted submission for resource %s"
                .formatted(resource.getId())));

    var resourceDisplay = submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(
        submissionOnResource
    );
    var cancelUrl = ReverseRouter.route(on(ViewResourceRecordController.class)
        .showResourceRecord(resource.getId(), null));
    var modelAndView = new ModelAndView("remi/review/regulatorReportingInformation")
        .addObject("form", form)
        .addObject("pageName", PAGE_TITLE)
        .addObject("resourceDisplay", resourceDisplay)
        .addObject("wellEndpoint",
            SearchSelectorService.route(on(WellRestController.class).searchWonsWells(null))
        )
        .addObject("well", regulatorReportingInformationService.getPreSelectedWell(form.getWell()))
        .addObject("hydrocarbonCase", DisplayableEnumOptionUtil.getDisplayableOptions(HydrocarbonsDiscovered.class))
        .addObject("opportunityTypes", DisplayableEnumOptionUtil.getDisplayableOptions(OpportunityType.class))
        .addObject("cancelUrl", cancelUrl);

    addBreadcrumbs(modelAndView, resourceDisplay, cancelUrl);

    return modelAndView;
  }

  private void addBreadcrumbs(ModelAndView modelAndView, String resourceDisplay, String cancelUrl) {
    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(PAGE_TITLE)
        .addBreadcrumb(
            SearchProspectAndDiscoveryRecordsController.PAGE_TITLE,
            ReverseRouter.route(
                on(SearchProspectAndDiscoveryRecordsController.class).searchResourceRecords(null, null)))
        .addBreadcrumb(resourceDisplay, cancelUrl)
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);
  }
}

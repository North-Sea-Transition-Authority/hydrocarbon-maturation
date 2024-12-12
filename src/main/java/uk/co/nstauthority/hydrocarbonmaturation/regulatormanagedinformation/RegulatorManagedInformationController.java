package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasAnyRole;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.controllerhelper.ControllerHelperService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchProspectAndDiscoveryRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@Controller
@RequestMapping("review/resource/{resourceId}/regulator-managed-information/")
@HasAnyRole(role = Role.EXPLORATION, teamType = TeamType.REGULATOR)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.NORTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.SOUTHERN_OFFSHORE_AREA)
@HasAnyRole(role = Role.MANAGE_DISCOVERY_OR_FIELD_WITH_AREA_TEAM, teamType = TeamType.ONSHORE_AREA)
public class RegulatorManagedInformationController {
  private static final String PAGE_NAME = "Regulator managed information";
  private final RegulatorManagedInformationFormService regulatorManagedInformationFormService;
  private final ControllerHelperService controllerHelperService;
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final SubmissionResourceService submissionResourceService;
  private final ResourceService resourceService;
  private final ResourceSupportingDocumentService resourceSupportingDocumentService;
  private final ResponsibleTeamService responsibleTeamService;
  private final InternalTeamService internalTeamService;

  @Autowired
  public RegulatorManagedInformationController(
      RegulatorManagedInformationFormService regulatorManagedInformationFormService,
      ControllerHelperService controllerHelperService,
      RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
      SubmissionDetailService submissionDetailService,
      SubmissionResourceService submissionResourceService,
      ResourceService resourceService,
      ResourceSupportingDocumentService resourceSupportingDocumentService,
      ResponsibleTeamService responsibleTeamService,
      InternalTeamService internalTeamService) {
    this.regulatorManagedInformationFormService = regulatorManagedInformationFormService;
    this.controllerHelperService = controllerHelperService;
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.submissionResourceService = submissionResourceService;
    this.resourceService = resourceService;
    this.resourceSupportingDocumentService = resourceSupportingDocumentService;
    this.responsibleTeamService = responsibleTeamService;
    this.internalTeamService = internalTeamService;
  }

  @GetMapping
  public ModelAndView getRegulatorManagedInformationPage(@PathVariable("resourceId") UUID resourceId, User user) {
    var resource = resourceService.getResourceById(resourceId)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("Could not find resource with id %s"
                .formatted(resourceId.toString())));

    var submissionDetail = submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource);
    if (submissionDetail.isEmpty()) {
      //if there is no approved or submitted submission for the resource regulator cannot set regulator managed information
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Did not find an accepted or submitted submission for resource %s "
          .formatted(resource.getId().toString()));
    }

    var regulatorManagedInformationForm = regulatorManagedInformationFormService
        .getRegulatorManagedInformationForm(resource);
    var isResourceBeingRelinquished = submissionDetailService
        .isLatestSubmissionForResourceRelinquishment(resource);

    return getRegulatorManagedInformationModelAndView(
        regulatorManagedInformationForm, resource, isResourceBeingRelinquished, user);
  }

  @PostMapping
  public ModelAndView saveRegulatorManagedInformation(@PathVariable("resourceId") UUID resourceId,
                                                      @ModelAttribute("form") RegulatorManagedInformationForm form,
                                                      BindingResult bindingResult,
                                                      RedirectAttributes redirectAttributes,
                                                      User user) {
    var resource = resourceService.getResourceById(resourceId)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("Could not find resource with id %s"
                .formatted(resourceId.toString()))
        );

    //if there is no approved or submitted submission for the resource regulator cannot set regulator managed information
    var submissionDetail = submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.FORBIDDEN, "Did not find an accepted or submitted submission for resource %s "
            .formatted(resource.getId().toString()))
        );
    var isResourceBeingRelinquished = submissionDetailService.isLatestSubmissionForResourceRelinquishment(resource);

    var isResponsibleTeamBeingUpdated = regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(
        resource);

    var isAreaTeamUser = internalTeamService.isAreaTeamUser(user);
    bindingResult = regulatorManagedInformationFormService
        .validate(form, bindingResult, isResourceBeingRelinquished, isResponsibleTeamBeingUpdated, isAreaTeamUser);
    return controllerHelperService.checkErrorsAndRedirect(
        bindingResult,
        getRegulatorManagedInformationModelAndView(
            form, resource, isResourceBeingRelinquished, user),
        () -> {
          regulatorManagedInformationPersistenceService.saveRegulatorManagedInformation(form, resource);

          var resourceName = submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(
              submissionDetail);

          NotificationBannerUtil.addSuccessNotification(redirectAttributes,
              "Success",
              "The regulator managed information has been updated for the resource %s".formatted(resourceName));

          var isInResponsibleTeam = responsibleTeamService.isInResponsibleTeam(user, submissionDetail);
          if (SubmissionDetailStatus.SUBMITTED.equals(submissionDetail.getStatus())) {
            // status is SUBMITTED
            if (isInResponsibleTeam) {
              return ReverseRouter.redirect(on(ReviewSubmissionController.class).showReviewPage(resource.getId(), null));
            } else {
              // if they have updated the permissions the above redirects will throw forbidden so instead go to work area
              return ReverseRouter.redirect(on(WorkAreaController.class).getWorkArea(null));
            }
          }

          // submission status was accepted so show in system of record
          return ReverseRouter.redirect(on(ViewResourceRecordController.class).showResourceRecord(resource.getId(), null));
        }
    );
  }

  private ModelAndView getRegulatorManagedInformationModelAndView(RegulatorManagedInformationForm form,
                                                                  Resource resource,
                                                                  boolean isResourceBeingRelinquished,
                                                                  User user) {
    var modelAndView = new ModelAndView("remi/review/regulatorManagedInformation")
        .addObject("form", form)
        .addObject("geographicalAreas", DisplayableEnumOptionUtil
            .getDisplayableOptions(GeographicalArea.class))
        .addObject("wellEndpoint", SearchSelectorService.route(on(WellRestController.class).searchWonsWells(null)))
        .addObject("well", regulatorManagedInformationFormService.getPreSelectedWell(form.getWell()))
        .addObject("isResourceBeingRelinquished", isResourceBeingRelinquished);
    if (isResourceBeingRelinquished) {
      modelAndView
          .addObject("relinquishmentReportTemplate",
              resourceSupportingDocumentService.fileUploadComponentAttributes(
                  form.getRelinquishmentReport(), FileDocumentType.RELINQUISHMENT_REPORT));
    }

    var canUpdateResponsibleTeam = regulatorManagedInformationPersistenceService.canUpdateResponsibleTeam(resource);
    modelAndView
        .addObject("canUpdateResponsibleTeam", canUpdateResponsibleTeam);
    if (canUpdateResponsibleTeam) {
      modelAndView
          .addObject("responsibleTeamTypes", DisplayableEnumOptionUtil
              .getDisplayableOptions(ResponsibleTeamType.class));
    }
    addBreadcrumbsAndCancelUrl(modelAndView, resource);

    if (resource.getType() == ResourceType.DISCOVERY && internalTeamService.isAreaTeamUser(user)) {
      modelAndView.addObject("shelvedCopFieldApiEndpoint",
          SearchSelectorService.route(on(ResourceRestController.class)
              .searchShelvedAndCopFields(null, null)))
          .addObject("preSelectedFieldOption", regulatorManagedInformationFormService.getPreSelectedFieldOption(resource));
    }

    return modelAndView;
  }

  private void addBreadcrumbsAndCancelUrl(ModelAndView modelAndView, Resource resource) {
    var submissionOnResource = submissionDetailService
        .getMostRecentlySubmittedSubmissionDetailForResource(resource)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("Could not find find an accepted or submitted submission for resource %s"
                .formatted(resource.getId())));

    String cancelUrl;
    Breadcrumbs breadcrumbs;
    if (submissionOnResource.getStatus().equals(SubmissionDetailStatus.ACCEPTED)) {
      // then breadcrumbs go to system of record
      cancelUrl = ReverseRouter.route(on(ViewResourceRecordController.class)
          .showResourceRecord(resource.getId(), null));
      breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(PAGE_NAME)
          .addBreadcrumb(
              SearchProspectAndDiscoveryRecordsController.PAGE_TITLE,
              ReverseRouter.route(
                  on(SearchProspectAndDiscoveryRecordsController.class).searchResourceRecords(null, null)))
          .addBreadcrumb(submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(
                  submissionOnResource),
              cancelUrl)
          .build();
    } else {
      // then breadcrumbs go to work area
      cancelUrl = ReverseRouter.route(on(ReviewSubmissionController.class)
          .showReviewPage(resource.getId(), null));
      breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(PAGE_NAME)
          .addWorkAreaBreadcrumb()
          .addBreadcrumb(submissionResourceService.getResourceNameWithCentreOfInterestAndLicenceApplicationReferenceOrDefault(
                  submissionOnResource),
              cancelUrl)
          .build();
    }

    modelAndView.addObject("cancelUrl", cancelUrl);

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);
  }
}

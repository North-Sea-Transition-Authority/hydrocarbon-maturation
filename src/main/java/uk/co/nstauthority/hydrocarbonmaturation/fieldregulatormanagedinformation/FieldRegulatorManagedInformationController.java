package uk.co.nstauthority.hydrocarbonmaturation.fieldregulatormanagedinformation;

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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.organisationunit.OrganisationUnitRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceRestController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.fds.notificationbanner.NotificationBannerUtil;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.field.DeterminationStatus;
import uk.co.nstauthority.hydrocarbonmaturation.field.DeterminedFieldRestController;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldForm;
import uk.co.nstauthority.hydrocarbonmaturation.field.NewFieldService;
import uk.co.nstauthority.hydrocarbonmaturation.field.StartFieldController;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchFieldRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@Controller
@AccessibleByServiceUsers
@RequestMapping("review/resource/{resourceId}/field-regulator-managed-information/")
public class FieldRegulatorManagedInformationController {
  public static final String PAGE_NAME = "Regulator managed information";

  private final UserDetailService userDetailService;
  private final ResponsibleTeamService responsibleTeamService;
  private final ResourceService resourceService;
  private final NewFieldService newFieldService;
  private final SubmissionResourceService submissionResourceService;
  private final SubmissionDetailService submissionDetailService;
  private final FieldRegulatorManagedInformationService fieldRegulatorManagedInformationService;

  @Autowired
  public FieldRegulatorManagedInformationController(
      UserDetailService userDetailService,
      ResponsibleTeamService responsibleTeamService, ResourceService resourceService,
      NewFieldService newFieldService,
      SubmissionResourceService submissionResourceService,
      SubmissionDetailService submissionDetailService,
      FieldRegulatorManagedInformationService fieldRegulatorManagedInformationService) {
    this.userDetailService = userDetailService;
    this.responsibleTeamService = responsibleTeamService;
    this.resourceService = resourceService;
    this.newFieldService = newFieldService;
    this.submissionResourceService = submissionResourceService;
    this.submissionDetailService = submissionDetailService;
    this.fieldRegulatorManagedInformationService = fieldRegulatorManagedInformationService;
  }

  @GetMapping
  public ModelAndView getRegulatorManagedInformationPage(@PathVariable("resourceId") UUID resourceId,
                                                         ServiceUserDetail serviceUserDetail) {
    var resource = resourceService.getResourceById(resourceId)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("Could not find resource with id %s"
                .formatted(resourceId.toString())));

    //if there is no approved or submitted submission for the resource regulator cannot set regulator managed information
    var submissionDetail = submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource)
        .orElseThrow(
            () -> new ResponseStatusException(
                HttpStatus.FORBIDDEN, "Did not find an accepted or submitted submission for resource %s "
                .formatted(resource.getId().toString()))
        );

    var isInResponsibleTeam = responsibleTeamService.isInResponsibleTeam(serviceUserDetail, submissionDetail);
    if (!isInResponsibleTeam) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          "User with id %s is not in the responsible team for resource with id %s"
              .formatted(serviceUserDetail.wuaId(), resourceId));
    }

    return getFieldRegulatorManagedInformationModelAndView(
        fieldRegulatorManagedInformationService.resourceToFieldForm(resource), resource);
  }

  @PostMapping
  public ModelAndView saveRegulatorManagedInformation(@PathVariable("resourceId") UUID resourceId,
                                                      @ModelAttribute("form") FieldForm form,
                                                      BindingResult bindingResult,
                                                      RedirectAttributes redirectAttributes) {
    var resource = resourceService.getResourceById(resourceId)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("Could not find resource with id %s"
                .formatted(resourceId.toString())));

    //if there is no approved or submitted submission for the resource regulator cannot set regulator managed information
    var latestAcceptedOrSubmittedSubmissionDetail =
        submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource)
            .orElseThrow(() ->
                new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Did not find an accepted or submitted submission for resource %s "
                    .formatted(resource.getId().toString()))
            );

    if (fieldRegulatorManagedInformationService.isValidNewFieldForm(form, bindingResult)) {
      fieldRegulatorManagedInformationService.saveField(form, resource);

      var resourceName =
          submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(latestAcceptedOrSubmittedSubmissionDetail);

      NotificationBannerUtil.addSuccessNotification(redirectAttributes,
          "Success",
          "The regulator managed information has been updated for the resource %s".formatted(resourceName));

      var userDetail = userDetailService.getUserDetail();
      var isInResponsibleTeam =
          responsibleTeamService.isInResponsibleTeam(userDetail, latestAcceptedOrSubmittedSubmissionDetail);

      if (SubmissionDetailStatus.SUBMITTED.equals(latestAcceptedOrSubmittedSubmissionDetail.getStatus())) {
        if (isInResponsibleTeam) {
          return ReverseRouter.redirect(on(ReviewSubmissionController.class).showReviewPage(resource.getId()));
        } else {
          // if they have updated the permissions the above redirects will throw forbidden so instead go to work area
          return ReverseRouter.redirect(on(WorkAreaController.class).getWorkArea(null));
        }
      }

      // submission status was accepted so show in system of record
      return ReverseRouter.redirect(on(ViewResourceRecordController.class).showResourceRecord(resource.getId()));
    }

    return getFieldRegulatorManagedInformationModelAndView(form, resource);
  }

  private ModelAndView getFieldRegulatorManagedInformationModelAndView(FieldForm form,
                                                                       Resource resource) {
    var modelAndView = new ModelAndView("remi/field/newField")
        .addObject("form", form)
        .addObject("backLinkUrl", ReverseRouter.route(on(StartFieldController.class).getStartPage()))
        .addObject("pageTitle", PAGE_NAME)
        .addObject("organisationUnitEndpoint",
            SearchSelectorService.route(on(OrganisationUnitRestController.class).searchOrganisationUnits(null)))
        .addObject("responsibleOrganisation", newFieldService
            .getPreSelectedOrganisationUnit(form.getResponsibleOrganisationId()))
        .addObject("onshoreOffshore", OnshoreOffshore.getOnshoreOffshore())
        .addObject("geographicalAreas", newFieldService.getGeographicalAreaDisplayableOptions())
        .addObject("responsibleTeamTypes", newFieldService.getResponsibleTeamTypeDisplayableOptions())
        .addObject("determinationStatus", DisplayableEnumOptionUtil.getDisplayableOptions(DeterminationStatus.class))
        .addObject("parentDeterminedFieldEndpoint",
            SearchSelectorService.route(on(DeterminedFieldRestController.class).searchDeterminedFields(null)))
        .addObject("parentDeterminedField", newFieldService
            .getPreSelectedParentDeterminedField(form.getParentDeterminedField()))
        .addObject("alreadyAddedAssociatedDiscoveries", newFieldService
            .getPreSelectedAssociatedDiscoveriesMap(form.getAssociatedDiscoveries()))
        .addObject("discoveriesApiEndpoint",
            SearchSelectorService.route(on(ResourceRestController.class)
                .searchDiscoveriesNotInGroupOrPreferredView(null)));

    addBreadcrumbsAndCancelUrl(modelAndView, resource);

    return modelAndView;
  }

  private void addBreadcrumbsAndCancelUrl(ModelAndView modelAndView, Resource resource) {

    var submissionOnResource = submissionDetailService
        .getMostRecentlySubmittedSubmissionDetailForResource(resource)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("Could not find find an accepted or submitted submission for resource %s"
                .formatted(resource.getId())));

    String cancelUrl = null;
    Breadcrumbs breadcrumbs = null;
    if (submissionOnResource.getStatus().equals(SubmissionDetailStatus.ACCEPTED)) {
      // then breadcrumbs go to system of record
      cancelUrl = ReverseRouter.route(on(ViewResourceRecordController.class)
          .showResourceRecord(resource.getId()));
      breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(PAGE_NAME)
          .addBreadcrumb(
              SearchFieldRecordsController.PAGE_TITLE,
              ReverseRouter.route(on(SearchFieldRecordsController.class).searchResourceRecords(null, null)))
          .addBreadcrumb(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionOnResource),
              cancelUrl)
          .build();
    } else {
      // then breadcrumbs go to work area
      cancelUrl = ReverseRouter.route(on(ReviewSubmissionController.class)
          .showReviewPage(resource.getId()));
      breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(PAGE_NAME)
          .addWorkAreaBreadcrumb()
          .addBreadcrumb(submissionResourceService.getResourceNameWithCentreOfInterestOrDefault(submissionOnResource),
              cancelUrl)
          .build();
    }

    modelAndView.addObject("cancelUrl", cancelUrl);

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);
  }
}

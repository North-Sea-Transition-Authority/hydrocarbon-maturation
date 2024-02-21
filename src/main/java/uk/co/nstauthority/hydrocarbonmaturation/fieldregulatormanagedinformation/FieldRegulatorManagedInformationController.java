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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.HasPermission;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.licence.application.LicenceRestController;
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
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.NewFieldService;
import uk.co.nstauthority.hydrocarbonmaturation.field.StartFieldController;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldlicences.FieldLicenceService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.OnshoreOffshore;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.ReviewSubmissionController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SearchFieldRecordsController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.RolePermission;
import uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaController;

@Controller
@HasPermission(
    permissions = {
        RolePermission.EXPLORATION,
        RolePermission.NORTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.SOUTHERN_OFFSHORE_AREA_TEAM_MEMBER,
        RolePermission.ONSHORE_AREA_TEAM_MEMBER
    }
)
@RequestMapping("review/resource/{resourceId}/field-regulator-managed-information/")
public class FieldRegulatorManagedInformationController {
  public static final String PAGE_NAME = "Regulator managed information";

  private final UserService userService;
  private final ResponsibleTeamService responsibleTeamService;
  private final ResourceService resourceService;
  private final NewFieldService newFieldService;
  private final FieldRegulatorManagedInformationService fieldRegulatorManagedInformationService;
  private final FieldPersistenceService fieldPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final FieldLicenceService fieldLicenceService;

  @Autowired
  public FieldRegulatorManagedInformationController(
      UserService userService,
      ResponsibleTeamService responsibleTeamService, ResourceService resourceService,
      NewFieldService newFieldService,
      FieldRegulatorManagedInformationService fieldRegulatorManagedInformationService,
      FieldPersistenceService fieldPersistenceService,
      SubmissionDetailService submissionDetailService,
      FieldLicenceService fieldLicenceService) {
    this.userService = userService;
    this.responsibleTeamService = responsibleTeamService;
    this.resourceService = resourceService;
    this.newFieldService = newFieldService;
    this.fieldRegulatorManagedInformationService = fieldRegulatorManagedInformationService;
    this.fieldPersistenceService = fieldPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.fieldLicenceService = fieldLicenceService;
  }

  @GetMapping
  public ModelAndView getRegulatorManagedInformationPage(@PathVariable("resourceId") UUID resourceId,
                                                         User user) {
    var resource = getResourceOrThrow(resourceId);

    var modelAndView = getCommonModelAndViewOrThrow(resource, user);
    addBreadcrumbsAndCancelUrlForSystemOfRecord(modelAndView, resource);

    return modelAndView;
  }

  @GetMapping("/{submissionDetailId}")
  public ModelAndView getRegulatorManagedInformationPageWithSubmissionDetail(
      @PathVariable("resourceId") UUID resourceId,
      @PathVariable("submissionDetailId") UUID submissionDetailId,
      User user) {

    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    if (SubmissionDetailStatus.ACCEPTED.equals(submissionDetail.getStatus())) {
      return getRegulatorManagedInformationPage(resourceId, user);
    }

    var resource = getResourceOrThrow(resourceId);

    var modelAndView = getCommonModelAndViewOrThrow(resource, user);
    addBreadcrumbsAndCancelUrlForWorkArea(modelAndView, resource);

    return modelAndView;
  }

  @PostMapping
  public ModelAndView saveRegulatorManagedInformation(@PathVariable("resourceId") UUID resourceId,
                                                      @ModelAttribute("form") FieldForm form,
                                                      BindingResult bindingResult,
                                                      RedirectAttributes redirectAttributes) {
    var resource = getResourceOrThrow(resourceId);

    if (fieldRegulatorManagedInformationService.isValidNewFieldForm(form, bindingResult)) {
      fieldRegulatorManagedInformationService.saveField(form, resource);

      var resourceName = fieldPersistenceService.getFieldOrThrow(resource).getCorrectedLegalFieldName();

      NotificationBannerUtil.addSuccessNotification(redirectAttributes,
          "Success",
          "The regulator managed information has been updated for the resource %s".formatted(resourceName));

      return ReverseRouter.redirect(on(ViewResourceRecordController.class).showResourceRecord(resource.getId()));
    }

    var modelAndView = getFieldRegulatorManagedInformationModelAndView(form);
    addBreadcrumbsAndCancelUrlForSystemOfRecord(modelAndView, resource);

    return modelAndView;
  }

  @PostMapping("/{submissionDetailId}")
  public ModelAndView saveRegulatorManagedInformationWithSubmissionDetail(
      @PathVariable("resourceId") UUID resourceId,
      @PathVariable("submissionDetailId") UUID submissionDetailId,
      @ModelAttribute("form") FieldForm form,
      BindingResult bindingResult,
      RedirectAttributes redirectAttributes) {

    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    if (SubmissionDetailStatus.ACCEPTED.equals(submissionDetail.getStatus())) {
      return saveRegulatorManagedInformation(resourceId, form, bindingResult, redirectAttributes);
    }

    var resource = getResourceOrThrow(resourceId);

    if (fieldRegulatorManagedInformationService.isValidNewFieldForm(form, bindingResult)) {
      fieldRegulatorManagedInformationService.saveField(form, resource);

      var resourceName = fieldPersistenceService.getFieldOrThrow(resource).getCorrectedLegalFieldName();

      NotificationBannerUtil.addSuccessNotification(redirectAttributes,
          "Success",
          "The regulator managed information has been updated for the resource %s".formatted(resourceName));

      var user = userService.getUser();
      var isInResponsibleTeam =
          responsibleTeamService.isInResponsibleTeam(user, form.getResponsibleTeamType());

      if (isInResponsibleTeam) {
        return ReverseRouter.redirect(on(ReviewSubmissionController.class).showReviewPage(resource.getId()));
      } else {
        // if they have updated the permissions the above redirects will throw forbidden so instead go to work area
        return ReverseRouter.redirect(on(WorkAreaController.class).getWorkArea(null));
      }
    }

    ModelAndView modelAndView = getFieldRegulatorManagedInformationModelAndView(form);
    addBreadcrumbsAndCancelUrlForWorkArea(modelAndView, resource);

    return modelAndView;
  }

  private ModelAndView getFieldRegulatorManagedInformationModelAndView(FieldForm form) {
    return new ModelAndView("remi/field/newField")
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
                .searchDiscoveriesNotInGroupOrPreferredView(null)))
        .addObject("licenceEndpoint",
            SearchSelectorService.route(on(LicenceRestController.class).searchExtantLicences(null)))
        .addObject("alreadyAddedLicences", fieldLicenceService.getFieldLicenceViews(form));
  }

  private void addBreadcrumbsAndCancelUrlForSystemOfRecord(ModelAndView modelAndView, Resource resource) {
    var field = fieldPersistenceService.getFieldOrThrow(resource);

    var cancelUrl = ReverseRouter.route(on(ViewResourceRecordController.class)
        .showResourceRecord(resource.getId()));
    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(PAGE_NAME)
        .addBreadcrumb(
            SearchFieldRecordsController.PAGE_TITLE,
            ReverseRouter.route(on(SearchFieldRecordsController.class).searchResourceRecords(null, null)))
        .addBreadcrumb(field.getCorrectedLegalFieldName(),
            cancelUrl)
        .build();

    modelAndView.addObject("cancelUrl", cancelUrl);

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);
  }

  private void addBreadcrumbsAndCancelUrlForWorkArea(ModelAndView modelAndView, Resource resource) {
    var field = fieldPersistenceService.getFieldOrThrow(resource);

    var cancelUrl = ReverseRouter.route(on(ReviewSubmissionController.class)
        .showReviewPage(resource.getId()));
    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(PAGE_NAME)
        .addWorkAreaBreadcrumb()
        .addBreadcrumb(field.getCorrectedLegalFieldName(),
            cancelUrl)
        .build();

    modelAndView.addObject("cancelUrl", cancelUrl);

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);
  }

  private Resource getResourceOrThrow(UUID resourceId) {
    return resourceService.getResourceById(resourceId)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("Could not find resource with id %s"
                .formatted(resourceId.toString())));
  }

  private ModelAndView getCommonModelAndViewOrThrow(Resource resource, User user) {
    var form = fieldRegulatorManagedInformationService.resourceToFieldForm(resource);
    var responsibleTeam = form.getResponsibleTeamType();
    var isInResponsibleTeam = responsibleTeamService.isInResponsibleTeam(user, responsibleTeam);
    if (!isInResponsibleTeam) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          "User with id %s is not in the responsible team for resource with id %s"
              .formatted(user.wuaId(), resource.getId()));
    }
    return getFieldRegulatorManagedInformationModelAndView(form);
  }
}

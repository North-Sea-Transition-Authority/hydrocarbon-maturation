package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Optional;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory.ViewFieldStatusHistoryController;
import uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.ProjectListController;
import uk.co.nstauthority.hydrocarbonmaturation.fieldregulatormanagedinformation.FieldRegulatorManagedInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.fieldregulatormanagedinformation.FieldRegulatorManagedInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailDisplayUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.FieldMapViewService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections.CorrectionSelectionController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections.CorrectionService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.Role;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.TeamType;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoverySummaryController;

@Controller
@RequestMapping("/view-resource-record/{resourceId}")
@AccessibleByServiceUsers
public class ViewResourceRecordController {
  private final ViewResourceRecordService viewResourceRecordService;
  private final SubmissionSummaryModelService submissionSummaryModelService;
  private final SubmissionDetailService submissionDetailService;
  private final SubmissionHistoryQueryService submissionHistoryQueryService;
  private final RegulatorManagedInformationSummaryService regulatorManagedInformationSummaryService;
  private final ResourceGroupSummaryService resourceGroupSummaryService;
  private final ResourceService resourceService;
  private final ResponsibleTeamService responsibleTeamService;
  private final CorrectionService correctionService;
  private final FieldRegulatorManagedInformationSummaryService fieldRegulatorManagedInformationSummaryService;
  private final FieldPersistenceService fieldPersistenceService;
  private final TeamQueryService teamQueryService;
  private final FieldMapViewService fieldMapViewService;

  @Autowired
  public ViewResourceRecordController(
      ViewResourceRecordService viewResourceRecordService,
      SubmissionSummaryModelService submissionSummaryModelService,
      SubmissionDetailService submissionDetailService,
      SubmissionHistoryQueryService submissionHistoryQueryService,
      RegulatorManagedInformationSummaryService regulatorManagedInformationSummaryService,
      ResourceGroupSummaryService resourceGroupSummaryService,
      ResourceService resourceService,
      ResponsibleTeamService responsibleTeamService,
      CorrectionService correctionService,
      FieldRegulatorManagedInformationSummaryService fieldRegulatorManagedInformationSummaryService,
      FieldPersistenceService fieldPersistenceService,
      FieldMapViewService fieldMapViewService,
      TeamQueryService teamQueryService) {
    this.viewResourceRecordService = viewResourceRecordService;
    this.submissionSummaryModelService = submissionSummaryModelService;
    this.submissionDetailService = submissionDetailService;
    this.submissionHistoryQueryService = submissionHistoryQueryService;
    this.regulatorManagedInformationSummaryService = regulatorManagedInformationSummaryService;
    this.resourceGroupSummaryService = resourceGroupSummaryService;
    this.resourceService = resourceService;
    this.responsibleTeamService = responsibleTeamService;
    this.correctionService = correctionService;
    this.fieldRegulatorManagedInformationSummaryService = fieldRegulatorManagedInformationSummaryService;
    this.fieldPersistenceService = fieldPersistenceService;
    this.teamQueryService = teamQueryService;
    this.fieldMapViewService = fieldMapViewService;
  }

  @GetMapping
  public ModelAndView showResourceRecord(@PathVariable("resourceId") UUID resourceId, User user) {
    var resourceRecord = viewResourceRecordService.getResourceRecordOrThrow(resourceId);

    if (!viewResourceRecordService.canViewResource(resourceRecord, user)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Resource cannot be viewed by user with ID %s ".formatted(user.wuaId()));
    }

    var submissionDetailOptional = submissionDetailService.getSubmissionDetail(resourceRecord.submissionDetailId());
    var form = new SubmissionSearchSelectorForm();
    submissionDetailOptional.ifPresent(submissionDetail -> form.setSelectedSubmissionId(submissionDetail.getId().toString()));
    return getModelAndView(resourceRecord, submissionDetailOptional, user, form, resourceId);
  }

  @PostMapping
  public ModelAndView viewSubmission(@PathVariable("resourceId") UUID resourceId,
                                     @ModelAttribute("form") SubmissionSearchSelectorForm form) {
    return ReverseRouter.redirect(on(ViewResourceRecordController.class)
        .showSubmissionSummary(resourceId, UUID.fromString(form.getSelectedSubmissionId()), null, null));
  }

  @GetMapping("submission-details/{submissionDetailId}")
  public ModelAndView showSubmissionSummary(@PathVariable("resourceId") UUID resourceId,
                                            @PathVariable("submissionDetailId") UUID submissionDetailId,
                                            @ModelAttribute("form") SubmissionSearchSelectorForm form,
                                            User user) {
    var selectedSubmissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var resourceRecord = viewResourceRecordService.getResourceRecordOrThrow(resourceId);

    if (!viewResourceRecordService.canViewResource(resourceRecord, user)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Resource cannot be viewed by user with ID %s ".formatted(user.wuaId()));
    }

    form.setSelectedSubmissionId(submissionDetailId.toString());
    return getModelAndView(resourceRecord, Optional.of(selectedSubmissionDetail), user, form, resourceId);
  }

  private ModelAndView getModelAndView(ResourceRecordSummaryView resourceRecord,
                                       Optional<SubmissionDetail> submissionDetailSelectedOptional,
                                       User user,
                                       SubmissionSearchSelectorForm form,
                                       UUID resourceId) {
    var resource = resourceService.getResourceById(resourceId)
        .orElseThrow(() -> new RemiEntityNotFoundException("Could not find resource for id %s"
            .formatted(resourceId)));

    var isInResponsibleTeam = responsibleTeamService
        .isInResponsibleTeam(user, resourceRecord.responsibleTeam());
    var isFieldResource = ResourceType.FIELD.equals(resource.getType());
    var isDiscovery = ResourceType.DISCOVERY.equals(resource.getType());
    var isRegulatorOrAreaUser = viewResourceRecordService.isRegulatorOrAreaTeamUser(user);

    ModelAndView modelAndView = getModelAndViewForSubmissionDetail(submissionDetailSelectedOptional, resource, user);

    if (isInResponsibleTeam && !isFieldResource) {
      modelAndView.addObject("resourceGroupUrl",
          ReverseRouter.route(on(ResourceGroupController.class)
              .getResourceGroupPage(resourceId, null)));
    }

    if (isRegulatorOrAreaUser) {
      addRegulatorOnlyProperties(resourceId, isFieldResource, modelAndView, resource);
    }

    if (!isFieldResource && correctionService.canStartCorrection(resource, user)) {
      modelAndView.addObject("correctionUrl",
          ReverseRouter.route(on(CorrectionSelectionController.class).startCorrection(resourceId, null)));
    }

    if (isDiscovery
        && teamQueryService.userHasStaticRole(user.wuaId(), TeamType.REGULATOR, Role.RESERVES_AND_RESOURCES_ADVISOR)) {
      modelAndView.addObject("undevelopedDiscoveryUrl",
          ReverseRouter.route(on(UndevelopedDiscoverySummaryController.class).getUndevelopedDiscoverySummary(resourceId, null)));
    }

    addBreadCrumbsForResourceType(resourceRecord, isFieldResource, modelAndView);

    var pageTitle = SubmissionDetailDisplayUtil.getDisplayName(
        resourceRecord.resourceName(),
        resourceRecord.centreOfInterest(),
        resourceRecord.licenceApplicationReference()
    );

    return modelAndView
        .addObject("form", form)
        .addObject("pageTitle", pageTitle)
        .addObject("resourceRecord", resourceRecord)
        .addObject("isFieldResource", isFieldResource)
        .addObject("canManageRegulatorManagementInfo", isInResponsibleTeam)
        .addObject("canViewRegulatorManagementInfo", isRegulatorOrAreaUser);
  }

  private void addRegulatorOnlyProperties(UUID resourceId,
                                          boolean isFieldResource,
                                          ModelAndView modelAndView,
                                          Resource resource) {
    if (isFieldResource) {
      var field = fieldPersistenceService.getFieldOrThrow(resource);
      fieldMapViewService.getFieldGeoJsonUrl(field)
          .ifPresent(fieldMapView -> modelAndView.addObject("fieldMapView", fieldMapView));

      modelAndView
          .addObject("regulatorManagedInformationUrl",
              ReverseRouter.route(on(FieldRegulatorManagedInformationController.class)
                  .getRegulatorManagedInformationPage(resourceId, null)))
          .addObject("fieldRegulatorManagedInformationSummaryCard",
              fieldRegulatorManagedInformationSummaryService.getFieldRegulatorManagedInformationSummaryCard(resource))
          .addObject("fieldStatusUrl",
              ReverseRouter.route(on(ViewFieldStatusHistoryController.class)
                  .getViewFieldStatusHistory(field.getFieldIdentifier(), null)))
          .addObject("manageResourceInformationText", "Manage field information")
          .addObject("projectTrackerUrl",
              ReverseRouter.route(on(ProjectListController.class)
                  .getProjectList(field.getResource().getId(), null)));
    } else {
      modelAndView
          .addObject("regulatorManagedInformationUrl",
              ReverseRouter.route(on(RegulatorManagedInformationController.class)
                  .getRegulatorManagedInformationPage(resourceId)))
          .addObject("resourceGroupsSummaryView",
              resourceGroupSummaryService.getResourceGroupLinkSummaryFromResource(resource))
          .addObject("regulatorManagedInformationSummaryView",
              regulatorManagedInformationSummaryService.getRegulatorManagedInformationSummaryFromResource(resource))
          .addObject("manageResourceInformationText", "Manage resource information");
    }
  }

  private void addBreadCrumbsForResourceType(ResourceRecordSummaryView resourceRecord,
                                                    boolean isFieldResource,
                                                    ModelAndView modelAndView) {
    var pageName = SubmissionDetailDisplayUtil.getDisplayName(
        resourceRecord.resourceName(),
        resourceRecord.centreOfInterest(),
        resourceRecord.licenceApplicationReference()
    );

    if (isFieldResource) {
      SystemOfRecordBreadcrumbUtil.addSearchFieldRecordsBreadcrumbsToModelAndView(modelAndView, pageName);
    } else {
      SystemOfRecordBreadcrumbUtil.addSearchProspectAndDiscoveryRecordsBreadcrumbsToModelAndView(modelAndView, pageName);
    }
  }

  @NotNull
  private ModelAndView getModelAndViewForSubmissionDetail(Optional<SubmissionDetail> submissionDetailSelectedOptional,
                                                          Resource resource,
                                                          User user) {
    return submissionDetailSelectedOptional.map(submissionDetailSelected -> {
          if (!viewResourceRecordService.canViewSubmissionDetail(submissionDetailSelected, resource)) {
            throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                ("You cannot view this %s submission detail due to it being a draft or deleted submission detail " +
                    "or a submission detail not on the same resource")
                    .formatted(submissionDetailSelected.getId()));
          }

          var modelAndViewForSubmissionType = submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(
              submissionDetailSelected,
              "remi/systemofrecord/viewResourceRecord",
              user
          );
          var querySearchResults = submissionHistoryQueryService.getAcceptedSubmissionsOnResourceWithLinkedResources(
              resource.getId()
          );

          modelAndViewForSubmissionType
              .addObject("submissionType", submissionDetailSelected.getSubmissionType())
              .addObject("resourceType", submissionDetailSelected
                  .getSubmission().getResource().getType().getDisplayName())
              .addObject("submissionsOnResource", SubmissionHistorySelectorUtil.getSubmissionSelectorItems(
                  querySearchResults))
              .addObject("submissionSelectedUrl",
                  ReverseRouter.route(on(ViewResourceRecordController.class).viewSubmission(resource.getId(), null))
              );

          return modelAndViewForSubmissionType;
        }
    ).orElse(new ModelAndView("remi/systemofrecord/viewResourceRecord"));
  }
}
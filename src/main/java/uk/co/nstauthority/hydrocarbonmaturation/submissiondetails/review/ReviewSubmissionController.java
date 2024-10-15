package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.UUID;
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
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.Breadcrumbs;
import uk.co.nstauthority.hydrocarbonmaturation.breadcrumb.BreadcrumbsUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory.ViewFieldStatusHistoryController;
import uk.co.nstauthority.hydrocarbonmaturation.fieldregulatormanagedinformation.FieldRegulatorManagedInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.fieldregulatormanagedinformation.FieldRegulatorManagedInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.RequestUpdateController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.review.update.StartUpdateController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.FieldMapViewService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.TaskListController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistoryQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionHistorySelectorUtil;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.SubmissionSearchSelectorForm;
import uk.co.nstauthority.hydrocarbonmaturation.teams.InternalTeamService;

@Controller
@RequestMapping("/review/resource/{resourceId}")
@AccessibleByServiceUsers
public class ReviewSubmissionController {

  private final ReviewSubmissionService reviewSubmissionService;
  private final SubmissionSummaryModelService submissionSummaryModelService;
  private final SubmissionDetailService submissionDetailService;
  private final SubmissionHistoryQueryService submissionHistoryQueryService;
  private final RegulatorManagedInformationSummaryService regulatorManagedInformationSummaryService;
  private final ResourceService resourceService;
  private final ResourceGroupSummaryService resourceGroupSummaryService;
  private final ResponsibleTeamService responsibleTeamService;
  private final FieldPersistenceService fieldPersistenceService;
  private final FieldRegulatorManagedInformationSummaryService fieldRegulatorManagedInformationSummaryService;
  private final FieldMapViewService fieldMapViewService;
  private final InternalTeamService internalTeamService;

  @Autowired
  public ReviewSubmissionController(
      ReviewSubmissionService reviewSubmissionService,
      SubmissionSummaryModelService submissionSummaryModelService,
      SubmissionDetailService submissionDetailService,
      SubmissionHistoryQueryService submissionHistoryQueryService,
      RegulatorManagedInformationSummaryService regulatorManagedInformationSummaryService,
      ResourceService resourceService,
      ResourceGroupSummaryService resourceGroupSummaryService,
      ResponsibleTeamService responsibleTeamService,
      FieldPersistenceService fieldPersistenceService,
      FieldRegulatorManagedInformationSummaryService fieldRegulatorManagedInformationSummaryService,
      FieldMapViewService fieldMapViewService,
      InternalTeamService internalTeamService) {
    this.reviewSubmissionService = reviewSubmissionService;
    this.submissionSummaryModelService = submissionSummaryModelService;
    this.submissionDetailService = submissionDetailService;
    this.submissionHistoryQueryService = submissionHistoryQueryService;
    this.regulatorManagedInformationSummaryService = regulatorManagedInformationSummaryService;
    this.resourceService = resourceService;
    this.resourceGroupSummaryService = resourceGroupSummaryService;
    this.responsibleTeamService = responsibleTeamService;
    this.fieldPersistenceService = fieldPersistenceService;
    this.fieldRegulatorManagedInformationSummaryService = fieldRegulatorManagedInformationSummaryService;
    this.fieldMapViewService = fieldMapViewService;
    this.internalTeamService = internalTeamService;
  }

  @GetMapping
  public ModelAndView showReviewPage(@PathVariable("resourceId") UUID resourceId, User user) {
    var submissionRecord = reviewSubmissionService.getSubmissionRecordOrThrow(resourceId);

    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(
        submissionRecord.submissionDetailId());

    if (!reviewSubmissionService.canViewSubmission(submissionRecord, user, submissionDetail)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Submission cannot be reviewed by user with ID %s ".formatted(user.wuaId()));
    }
    var form = new SubmissionSearchSelectorForm();
    form.setSelectedSubmissionId(submissionRecord.submissionDetailId().toString());
    return getModelAndView(submissionRecord, user, submissionDetail, form, resourceId);
  }

  @GetMapping("submission-details/{submissionDetailId}")
  public ModelAndView showSubmissionSummary(@PathVariable("resourceId") UUID resourceId,
                                            @PathVariable("submissionDetailId") UUID submissionDetailId,
                                            @ModelAttribute("form") SubmissionSearchSelectorForm form,
                                            User user) {
    var selectedSubmission = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var resource = resourceService.getResourceById(resourceId)
        .orElseThrow(() -> new RemiEntityNotFoundException("Could not find resource with id %s"
            .formatted(resourceId)));

    if (!reviewSubmissionService.canViewSelectedSubmissionDetail(selectedSubmission, resource)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          ("You cannot view this %s submission detail due to it being a draft or deleted submission detail " +
              "or a submission detail not on the same resource")
              .formatted(submissionDetailId));
    }

    var latestSubmission = submissionDetailService.getMostRecentlySubmittedSubmissionDetailForResource(resource)
        .orElseThrow(() ->
            new RemiEntityNotFoundException("Could not find latest submitted or accepted submission for resource %s"
                .formatted(resourceId)));

    var submissionRecord = reviewSubmissionService.getSubmissionRecordOrThrow(resourceId);

    if (!reviewSubmissionService.canViewSubmission(submissionRecord, user, latestSubmission)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Submission cannot be viewed by user with ID %s ".formatted(user.wuaId()));
    }

    form.setSelectedSubmissionId(submissionDetailId.toString());
    return getModelAndView(submissionRecord, user, selectedSubmission, form, resourceId);
  }

  @PostMapping
  public ModelAndView viewSubmission(@PathVariable("resourceId") UUID resourceId,
                                     @ModelAttribute("form") SubmissionSearchSelectorForm form) {
    return ReverseRouter.redirect(on(ReviewSubmissionController.class)
        .showSubmissionSummary(resourceId, UUID.fromString(form.getSelectedSubmissionId()), null, null));
  }

  private ModelAndView getModelAndView(SubmissionRecordSummaryView submissionRecord,
                                       User user,
                                       SubmissionDetail submissionDetail,
                                       SubmissionSearchSelectorForm form,
                                       UUID resourceId) {
    var modelAndView = submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(
        submissionDetail,
        "remi/review/reviewSubmission",
        user
    );

    var querySearchResults = submissionHistoryQueryService.getAcceptedAndSubmittedSubmissionsOnResourceWithLinkedResources(
        resourceId
    );
    modelAndView.addObject("submissionsOnResource", SubmissionHistorySelectorUtil.getSubmissionSelectorItems(
        querySearchResults
    ));
    modelAndView.addObject("submissionSelectedUrl",
        ReverseRouter.route(on(ReviewSubmissionController.class).viewSubmission(resourceId, null))
    );

    var breadcrumbs = new Breadcrumbs.BreadcrumbsBuilder(
        submissionRecord.resourceNameCentreOfInterestAndLicenceApplicationReference())
        .addWorkAreaBreadcrumb()
        .build();

    BreadcrumbsUtil.addBreadcrumbsToModel(modelAndView, breadcrumbs);

    var isInResponsibleTeam = responsibleTeamService
        .isInResponsibleTeam(user, submissionRecord.responsibleTeamType());
    var optionalSubmissionDetailUpdate = reviewSubmissionService.getUpdateSubmissionDetailBySubmission(
        submissionDetail.getSubmission());

    var resource = resourceService.getResourceById(resourceId)
        .orElseThrow(() -> new RemiEntityNotFoundException("Could not find resource for id %s"
            .formatted(resourceId)));

    var isFieldResource = ResourceType.FIELD.equals(resource.getType());
    if (isFieldResource) {
      addFieldSummary(modelAndView, resource);
    }
    if (isInResponsibleTeam) {
      addResponsibleTeamProperties(modelAndView, resource, submissionDetail, user);
    }

    var isIndustrySubmitter = reviewSubmissionService
        .canUpdateSubmission(submissionRecord, user, submissionDetail);
    if (isIndustrySubmitter) {
      var updateSubmissionUrl = optionalSubmissionDetailUpdate
          .map(detail -> ReverseRouter.route(on(TaskListController.class)
              .getTaskList(detail.getId(), null)))
          .orElseGet(() -> ReverseRouter.route(on(StartUpdateController.class)
              .showStartUpdatePage(submissionDetail.getId())));

      modelAndView
          .addObject("updateSubmissionUrl", updateSubmissionUrl)
          .addObject("updateStarted", optionalSubmissionDetailUpdate.isPresent());
    }

    reviewSubmissionService.getLatestUnresolvedSubmissionUpdateBySubmission(submissionDetail.getSubmission())
        .ifPresentOrElse(submissionUpdate ->
                modelAndView
                    .addObject("submissionUpdateReason", submissionUpdate.getReason())
                    .addObject("updateRequested", true),
            () -> modelAndView
                .addObject("updateRequested", false));


    return modelAndView
        .addObject("form", form)
        .addObject("submissionRecord", submissionRecord)
        .addObject("submissionType", submissionDetail.getSubmissionType())
        .addObject("canReview",
            submissionDetail.getStatus() == SubmissionDetailStatus.SUBMITTED && isInResponsibleTeam)
        .addObject("isFieldSubmission", submissionDetail.isFieldSubmission())
        .addObject("isInResponsibleTeam", isInResponsibleTeam)
        .addObject("isIndustrySubmitter", isIndustrySubmitter);
  }

  private void addFieldSummary(ModelAndView modelAndView, Resource resource) {
    modelAndView
        .addObject("fieldRegulatorManagedInformationSummaryCard",
            fieldRegulatorManagedInformationSummaryService.getFieldRegulatorManagedInformationSummaryCard(resource))
        .addObject("associatedDiscoveriesMap",
            fieldRegulatorManagedInformationSummaryService.getAssociatedDiscoveriesMap(resource));
  }

  private void addResponsibleTeamProperties(ModelAndView modelAndView, Resource resource,
                                            SubmissionDetail selectedSubmissionDetail,
                                            User user) {

    modelAndView
        .addObject("resourceGroupsSummaryView",
            resourceGroupSummaryService.getResourceGroupLinkSummaryFromResource(resource))
        .addObject("canRequestUpdate", reviewSubmissionService
            .canRequestUpdateOnSubmissionDetail(selectedSubmissionDetail, user))
        .addObject("requestUpdateUrl", ReverseRouter.route(on(RequestUpdateController.class)
            .showRequestUpdateConfirmationPage(selectedSubmissionDetail.getId(), null)))
        .addObject("approveUrl",
            ReverseRouter.route(on(ApproveSubmissionController.class)
                .showApproveConfirmationPage(selectedSubmissionDetail.getId(), null)))
        .addObject("withdrawUrl",
            ReverseRouter.route(on(WithdrawSubmissionController.class)
                .showWithdrawConfirmationPage(selectedSubmissionDetail.getId(), null)));

    if (selectedSubmissionDetail.isFieldSubmission()) {
      var field = fieldPersistenceService.getFieldOrThrow(resource);
      fieldMapViewService.getFieldGeoJsonUrl(field)
          .ifPresent(fieldMapView -> modelAndView.addObject("fieldMapView", fieldMapView));

      modelAndView
          .addObject("regulatorManagedInformationUrl",
              ReverseRouter.route(on(FieldRegulatorManagedInformationController.class)
                  .getRegulatorManagedInformationPageWithSubmissionDetail(resource.getId(), selectedSubmissionDetail.getId(),
                      null)))
          .addObject("manageResourceInformationText", "Manage field information")
          .addObject("fieldStatusUrl", ReverseRouter.route(on(ViewFieldStatusHistoryController.class)
              .getViewFieldStatusHistoryWithSubmissionDetail(
                  field.getFieldIdentifier(), selectedSubmissionDetail.getId(), null)));
    } else {
      modelAndView
          .addObject("regulatorManagedInformationSummaryView",
              regulatorManagedInformationSummaryService.getRegulatorManagedInformationSummaryFromResource(resource))
          .addObject("regulatorManagedInformationUrl",
              ReverseRouter.route(on(RegulatorManagedInformationController.class)
                  .getRegulatorManagedInformationPage(resource.getId(), null)))
          .addObject("manageResourceInformationText", "Manage resource information")
          .addObject("canSeePostFieldLink", internalTeamService.isRegulatorOrAreaTeamUser(user));
    }
  }
}

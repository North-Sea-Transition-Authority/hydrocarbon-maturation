package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupController;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections.CorrectionSelectionController;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections.CorrectionService;

@Controller
@RequestMapping("/view-resource-record/{resourceId}")
@AccessibleByServiceUsers
public class ViewResourceRecordController {
  private final ViewResourceRecordService viewResourceRecordService;
  private final SubmissionSummaryModelService submissionSummaryModelService;
  private final SubmissionDetailService submissionDetailService;
  private final UserService userService;
  private final SubmissionHistoryQueryService submissionHistoryQueryService;
  private final RegulatorManagedInformationSummaryService regulatorManagedInformationSummaryService;
  private final ResourceGroupSummaryService resourceGroupSummaryService;
  private final ResourceService resourceService;
  private final ResponsibleTeamService responsibleTeamService;
  private final CorrectionService correctionService;

  @Autowired
  public ViewResourceRecordController(ViewResourceRecordService viewResourceRecordService,
                                      SubmissionSummaryModelService submissionSummaryModelService,
                                      SubmissionDetailService submissionDetailService,
                                      UserService userService,
                                      SubmissionHistoryQueryService submissionHistoryQueryService,
                                      RegulatorManagedInformationSummaryService regulatorManagedInformationSummaryService,
                                      ResourceGroupSummaryService resourceGroupSummaryService,
                                      ResourceService resourceService,
                                      ResponsibleTeamService responsibleTeamService,
                                      CorrectionService correctionService) {
    this.viewResourceRecordService = viewResourceRecordService;
    this.submissionSummaryModelService = submissionSummaryModelService;
    this.submissionDetailService = submissionDetailService;
    this.userService = userService;
    this.submissionHistoryQueryService = submissionHistoryQueryService;
    this.regulatorManagedInformationSummaryService = regulatorManagedInformationSummaryService;
    this.resourceGroupSummaryService = resourceGroupSummaryService;
    this.resourceService = resourceService;
    this.responsibleTeamService = responsibleTeamService;
    this.correctionService = correctionService;
  }

  @GetMapping
  public ModelAndView showResourceRecord(@PathVariable("resourceId") UUID resourceId) {
    var resourceRecord = viewResourceRecordService.getResourceRecordOrThrow(resourceId);
    var user = userService.getUser();

    if (!viewResourceRecordService.canViewResource(resourceRecord, user)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Resource cannot be viewed by user with ID %s ".formatted(user.wuaId()));
    }

    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(resourceRecord.submissionDetailId());
    var form = new SubmissionSearchSelectorForm();
    form.setSelectedSubmissionId(submissionDetail.getId().toString());
    return getModelAndView(resourceRecord, submissionDetail, user, form, resourceId);
  }

  @PostMapping
  public ModelAndView viewSubmission(@PathVariable("resourceId") UUID resourceId,
                                     @ModelAttribute("form") SubmissionSearchSelectorForm form) {
    return ReverseRouter.redirect(on(ViewResourceRecordController.class)
        .showSubmissionSummary(resourceId, UUID.fromString(form.getSelectedSubmissionId()), null));
  }

  @GetMapping("submission-details/{submissionDetailId}")
  public ModelAndView showSubmissionSummary(@PathVariable("resourceId") UUID resourceId,
                                            @PathVariable("submissionDetailId") UUID submissionDetailId,
                                            @ModelAttribute("form") SubmissionSearchSelectorForm form) {
    var selectedSubmissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var resourceRecord = viewResourceRecordService.getResourceRecordOrThrow(resourceId);
    var user = userService.getUser();

    if (!viewResourceRecordService.canViewResource(resourceRecord, user)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Resource cannot be viewed by user with ID %s ".formatted(user.wuaId()));
    }

    form.setSelectedSubmissionId(submissionDetailId.toString());
    return getModelAndView(resourceRecord, selectedSubmissionDetail, user, form, resourceId);
  }

  private ModelAndView getModelAndView(ResourceRecordSummaryView resourceRecord,
                                       SubmissionDetail submissionDetailSelected,
                                       User user,
                                       SubmissionSearchSelectorForm form,
                                       UUID resourceId) {
    var resource = resourceService.getResourceById(resourceId)
        .orElseThrow(() -> new RemiEntityNotFoundException("Could not find resource for id %s"
            .formatted(resourceId)));

    if (!viewResourceRecordService.canViewSubmissionDetail(submissionDetailSelected, resource)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN,
          ("You cannot view this %s submission detail due to it being a draft or deleted submission detail " +
              "or a submission detail not on the same resource")
              .formatted(submissionDetailSelected.getId()));
    }

    var modelAndView = submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(
        submissionDetailSelected,
        "remi/systemofrecord/viewResourceRecord"
    );

    SystemOfRecordBreadcrumbUtil.addSearchSystemOfRecordToModelAndView(
        modelAndView, resourceRecord.resourceNameAndCentreOfInterest());

    var isInResponsibleTeam = responsibleTeamService
        .isInResponsibleTeam(user, resourceRecord.responsibleTeam());
    if (isInResponsibleTeam) {
      modelAndView
          .addObject("regulatorManagedInformationUrl",
              ReverseRouter.route(on(RegulatorManagedInformationController.class)
                  .getRegulatorManagedInformationPage(resourceId
                  )))
          .addObject("resourceGroupUrl",
              ReverseRouter.route(on(ResourceGroupController.class)
                  .getResourceGroupPage(resourceId, null)));
    }

    var querySearchResults = submissionHistoryQueryService.getApprovedSubmissionsOnResource(
        resourceId
    );
    modelAndView.addObject("submissionsOnResource", SubmissionHistorySelectorUtil.getSubmissionSelectorItems(
        querySearchResults
    ));
    modelAndView.addObject("submissionSelectedUrl",
        ReverseRouter.route(on(ViewResourceRecordController.class).viewSubmission(resourceId, null))
    );

    var canViewRegulatorManagementInfo = viewResourceRecordService.isRegulatorOrAreaTeamUser(user);
    if (canViewRegulatorManagementInfo) {
      modelAndView
          .addObject("resourceGroupsSummaryView",
              resourceGroupSummaryService.getResourceGroupLinkSummaryFromResource(resource))
          .addObject("regulatorManagedInformationSummaryView",
              regulatorManagedInformationSummaryService.getRegulatorManagedInformationSummaryFromResource(resource));
    }

    if (correctionService.canStartCorrection(resource, user)) {
      modelAndView.addObject("correctionUrl",
          ReverseRouter.route(on(CorrectionSelectionController.class).startCorrection(resourceId, null)));
    }

    return modelAndView
        .addObject("form", form)
        .addObject("resourceRecord", resourceRecord)
        .addObject("submissionType", submissionDetailSelected.getSubmissionType())
        .addObject("resourceType", submissionDetailSelected
            .getSubmission().getResource().getType().getDisplayName())
        .addObject("canManageRegulatorManagementInfo", isInResponsibleTeam)
        .addObject("canViewRegulatorManagementInfo", canViewRegulatorManagementInfo);
  }
}
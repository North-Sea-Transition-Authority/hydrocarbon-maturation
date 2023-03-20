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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@Controller
@RequestMapping("/view-resource-record/{resourceId}")
@AccessibleByServiceUsers
public class ViewResourceRecordController {
  private final ViewResourceRecordService viewResourceRecordService;
  private final SubmissionSummaryModelService submissionSummaryModelService;
  private final SubmissionDetailService submissionDetailService;
  private final UserDetailService userDetailService;
  private final SubmissionHistorySearchSelectorModelService submissionHistorySearchSelectorModelService;
  private final RegulatorTeamService regulatorTeamService;
  private final RegulatorManagedInformationSummaryService regulatorManagedInformationSummaryService;
  private final ResourceService resourceService;

  @Autowired
  public ViewResourceRecordController(ViewResourceRecordService viewResourceRecordService,
                                      SubmissionSummaryModelService submissionSummaryModelService,
                                      SubmissionDetailService submissionDetailService,
                                      UserDetailService userDetailService,
                                      SubmissionHistorySearchSelectorModelService submissionHistorySearchSelectorModelService,
                                      RegulatorTeamService regulatorTeamService,
                                      RegulatorManagedInformationSummaryService regulatorManagedInformationSummaryService,
                                      ResourceService resourceService) {
    this.viewResourceRecordService = viewResourceRecordService;
    this.submissionSummaryModelService = submissionSummaryModelService;
    this.submissionDetailService = submissionDetailService;
    this.userDetailService = userDetailService;
    this.submissionHistorySearchSelectorModelService = submissionHistorySearchSelectorModelService;
    this.regulatorTeamService = regulatorTeamService;
    this.regulatorManagedInformationSummaryService = regulatorManagedInformationSummaryService;
    this.resourceService = resourceService;
  }

  @GetMapping
  public ModelAndView showResourceRecord(@PathVariable("resourceId") UUID resourceId) {
    var resourceRecord = viewResourceRecordService.getResourceRecordOrThrow(resourceId);
    var userDetail = userDetailService.getUserDetail();

    if (!viewResourceRecordService.canViewResource(resourceRecord, userDetail)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Resource cannot be viewed by user with ID %s ".formatted(userDetail.wuaId()));
    }

    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(resourceRecord.submissionDetailId());
    var form = new SubmissionSearchSelectorForm();
    form.setSelectedSubmissionId(submissionDetail.getId().toString());
    return getModelAndView(resourceRecord, submissionDetail, userDetail, form, resourceId);
  }

  @PostMapping
  public ModelAndView viewSubmission(@PathVariable("resourceId") UUID resourceId,
                                     @ModelAttribute("form") SubmissionSearchSelectorForm form) {
    return ReverseRouter.redirect(on(ViewResourceRecordController.class)
        .showSubmissionSummary(resourceId, UUID.fromString(form.getSelectedSubmissionId()), null));
  }

  @GetMapping("{submissionDetailId}")
  public ModelAndView showSubmissionSummary(@PathVariable("resourceId") UUID resourceId,
                                            @PathVariable("submissionDetailId") UUID submissionDetailId,
                                            @ModelAttribute("form") SubmissionSearchSelectorForm form) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var resourceRecord = viewResourceRecordService.getResourceRecordOrThrow(resourceId);
    var userDetail = userDetailService.getUserDetail();

    if (!viewResourceRecordService.canViewResource(resourceRecord, userDetail)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Resource cannot be viewed by user with ID %s ".formatted(userDetail.wuaId()));
    }
    form.setSelectedSubmissionId(submissionDetailId.toString());
    return getModelAndView(resourceRecord, submissionDetail, userDetail, form, resourceId);
  }

  private ModelAndView getModelAndView(ResourceRecordSummaryView resourceRecord,
                                       SubmissionDetail submissionDetailSelected,
                                       ServiceUserDetail userDetail,
                                       SubmissionSearchSelectorForm form,
                                       UUID resourceId) {
    var modelAndView = submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(
        submissionDetailSelected,
        "remi/systemofrecord/viewResourceRecord"
    );

    SystemOfRecordBreadcrumbUtil.addSearchSystemOfRecordToModelAndView(
        modelAndView, resourceRecord.resourceNameAndCentreOfInterest());

    var isRegulator = regulatorTeamService.isRegulatorUser(userDetail);

    if (isRegulator) {
      modelAndView
          .addObject("regulatorManagedInformationSummaryView",
              regulatorManagedInformationSummaryService.getRegulatorManagedInformationSummaryFromResource(
                 resourceService.getResourceById(resourceId)
                     .orElseThrow(() -> new RemiEntityNotFoundException("Could not find resource for id %s"
                         .formatted(resourceId)))));
    }

    modelAndView = submissionHistorySearchSelectorModelService
        .addViewSorSubmissionSearchSelectorItemsToModelAndView(
            modelAndView, submissionDetailSelected, resourceId);
    return modelAndView
        .addObject("form", form)
        .addObject("resourceRecord", resourceRecord)
        .addObject("submissionType", submissionDetailSelected.getSubmissionType())
        .addObject("resourceType", submissionDetailSelected.getResourceId().getType().getDisplayName())
        .addObject("isRegulatorInResponsibleTeam",
            isRegulator && viewResourceRecordService.isInResponsibleRegulatorTeam(userDetail))
        .addObject("regulatorManagedInformationUrl",
            ReverseRouter.route(on(RegulatorManagedInformationController.class)
                .getRegulatorManagedInformationPage(resourceId,
                    null)))
        .addObject("isRegulator", isRegulator
        );
  }
}
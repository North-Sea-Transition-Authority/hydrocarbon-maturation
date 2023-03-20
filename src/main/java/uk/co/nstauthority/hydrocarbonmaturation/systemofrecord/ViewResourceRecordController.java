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
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubmissionSummaryModelService;

@Controller
@RequestMapping("/view-resource-record/{resourceId}")
@AccessibleByServiceUsers
public class ViewResourceRecordController {
  private final ViewResourceRecordService viewResourceRecordService;
  private final SubmissionSummaryModelService submissionSummaryModelService;
  private final SubmissionDetailService submissionDetailService;
  private final UserDetailService userDetailService;
  private final SubmissionHistorySearchSelectorModelService submissionHistorySearchSelectorModelService;

  @Autowired
  public ViewResourceRecordController(ViewResourceRecordService viewResourceRecordService,
                                      SubmissionSummaryModelService submissionSummaryModelService,
                                      SubmissionDetailService submissionDetailService,
                                      UserDetailService userDetailService,
                                      SubmissionHistorySearchSelectorModelService submissionHistorySearchSelectorModelService) {
    this.viewResourceRecordService = viewResourceRecordService;
    this.submissionSummaryModelService = submissionSummaryModelService;
    this.submissionDetailService = submissionDetailService;
    this.userDetailService = userDetailService;
    this.submissionHistorySearchSelectorModelService = submissionHistorySearchSelectorModelService;
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
    return getModelAndView(resourceRecord, submissionDetail, userDetail, form);
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
    return getModelAndView(resourceRecord, submissionDetail, userDetail, form);
  }

  private ModelAndView getModelAndView(ResourceRecordSummaryView resourceRecord,
                                       SubmissionDetail submissionDetail,
                                       ServiceUserDetail userDetail,
                                       SubmissionSearchSelectorForm form) {
    var modelAndView = submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(
        submissionDetail,
        "remi/systemofrecord/viewResourceRecord"
    );

    SystemOfRecordBreadcrumbUtil.addSearchSystemOfRecordToModelAndView(
        modelAndView, resourceRecord.resourceNameAndCentreOfInterest());

    modelAndView = submissionHistorySearchSelectorModelService
        .addViewSorSubmissionSearchSelectorItemsToModelAndView(modelAndView, submissionDetail);
    return modelAndView
        .addObject("form", form)
        .addObject("resourceRecord", resourceRecord)
        .addObject("submissionType", submissionDetail.getSubmissionType())
        .addObject("resourceType", submissionDetail.getResourceId().getType().getDisplayName())
        .addObject("isRegulatorInResponsibleTeam",
            viewResourceRecordService.isInResponsibleRegulatorTeam(userDetail))
        .addObject("regulatorManagedInformationUrl",
            ReverseRouter.route(on(RegulatorManagedInformationController.class)
                .getRegulatorManagedInformationPage(submissionDetail.getResourceId().getId(),
                    null)));
  }
}
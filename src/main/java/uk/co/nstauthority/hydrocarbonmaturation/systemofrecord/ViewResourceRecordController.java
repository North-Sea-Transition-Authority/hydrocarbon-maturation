package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
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

  @Autowired
  public ViewResourceRecordController(ViewResourceRecordService viewResourceRecordService,
                                      SubmissionSummaryModelService submissionSummaryModelService,
                                      SubmissionDetailService submissionDetailService,
                                      UserDetailService userDetailService) {
    this.viewResourceRecordService = viewResourceRecordService;
    this.submissionSummaryModelService = submissionSummaryModelService;
    this.submissionDetailService = submissionDetailService;
    this.userDetailService = userDetailService;
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

    return getModelAndView(resourceRecord, submissionDetail);
  }

  private ModelAndView getModelAndView(ResourceRecordSummaryView resourceRecord, SubmissionDetail submissionDetail) {
    var modelAndView = submissionSummaryModelService.createSummaryForSubmissionTypeToModelAndView(
        submissionDetail,
        "remi/systemofrecord/viewResourceRecord"
    );

    SystemOfRecordBreadcrumbUtil.addSearchSystemOfRecordToModelAndView(
        modelAndView, resourceRecord.resourceNameAndCentreOfInterest());

    return modelAndView
        .addObject("resourceRecord", resourceRecord)
        .addObject("submissionType", submissionDetail.getSubmissionType());
  }
}
package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordService;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/outline-for-maximum-case/rest")
@AccessibleByServiceUsers
public class OutlineForMaximumCaseRestController {

  private final OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final ViewResourceRecordService viewResourceRecordService;
  private final UserDetailService userDetailService;

  @Autowired
  public OutlineForMaximumCaseRestController(OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService,
                                             SubmissionDetailService submissionDetailService,
                                             ViewResourceRecordService viewResourceRecordService,
                                             UserDetailService userDetailService) {
    this.outlineForMaximumCasePersistenceService = outlineForMaximumCasePersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.viewResourceRecordService = viewResourceRecordService;
    this.userDetailService = userDetailService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<String> getGeoJsonForOutlineForMaximumCase(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);
    var resourceRecord = viewResourceRecordService
        .getResourceRecordOrThrow(submissionDetail.getSubmission().getResource().getId());
    var userDetail = userDetailService.getUserDetail();

    if (!viewResourceRecordService.canViewResource(resourceRecord, userDetail)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Resource cannot be viewed by user with ID %s ".formatted(userDetail.wuaId()));
    }

    var geoJson = outlineForMaximumCasePersistenceService.getGeoJsonForOutlineForMaximumCase(submissionDetail);
    return ResponseEntity.ok(geoJson);
  }
}

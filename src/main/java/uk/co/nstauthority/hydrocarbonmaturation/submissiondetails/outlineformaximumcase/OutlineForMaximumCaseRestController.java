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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailAccessService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/outline-for-maximum-case/rest")
@AccessibleByServiceUsers
public class OutlineForMaximumCaseRestController {

  private final OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final SubmissionDetailAccessService submissionDetailAccessService;

  @Autowired
  public OutlineForMaximumCaseRestController(OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService,
                                             SubmissionDetailService submissionDetailService,
                                             SubmissionDetailAccessService submissionDetailAccessService) {
    this.outlineForMaximumCasePersistenceService = outlineForMaximumCasePersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.submissionDetailAccessService = submissionDetailAccessService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<String> getGeoJsonForOutlineForMaximumCase(@PathVariable("submissionDetailId") UUID submissionDetailId,
                                                                   User user) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    if (!submissionDetailAccessService.canViewSubmissionDetail(submissionDetail, user)) {
      throw new ResponseStatusException(
          HttpStatus.FORBIDDEN, "Submission detail cannot be viewed by user with ID %s ".formatted(user.wuaId()));
    }

    var geoJson = outlineForMaximumCasePersistenceService.getGeoJsonForOutlineForMaximumCase(submissionDetail);
    return ResponseEntity.ok(geoJson);
  }
}

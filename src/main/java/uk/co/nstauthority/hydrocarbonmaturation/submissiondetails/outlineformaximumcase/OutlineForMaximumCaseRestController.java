package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.co.nstauthority.hydrocarbonmaturation.authorisation.AccessibleByServiceUsers;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;

@Controller
@RequestMapping("/submission-details/{submissionDetailId}/outline-for-maximum-case/rest")
@AccessibleByServiceUsers
public class OutlineForMaximumCaseRestController {

  private final OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;
  private final SubmissionDetailService submissionDetailService;

  @Autowired
  public OutlineForMaximumCaseRestController(OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService,
                                             SubmissionDetailService submissionDetailService) {
    this.outlineForMaximumCasePersistenceService = outlineForMaximumCasePersistenceService;
    this.submissionDetailService = submissionDetailService;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ResponseEntity<String> getGeoJsonForOutlineForMaximumCase(@PathVariable("submissionDetailId") UUID submissionDetailId) {
    var submissionDetail = submissionDetailService.getSubmissionDetailForIdOrError(submissionDetailId);

    var geoJson = outlineForMaximumCasePersistenceService.getGeoJsonForOutlineForMaximumCase(submissionDetail);
    return ResponseEntity.ok(geoJson);
  }
}

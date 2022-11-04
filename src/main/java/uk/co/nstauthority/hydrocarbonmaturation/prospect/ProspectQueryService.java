package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;

@Service
class ProspectQueryService {

  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final SubmissionDetailService submissionDetailService;

  @Autowired
  ProspectQueryService(GeneralDetailsPersistenceService generalDetailsPersistenceService,
                       SubmissionDetailService submissionDetailService) {
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.submissionDetailService = submissionDetailService;
  }

  public List<ProspectView> getLatestProspectSubmissionByName(String name) {
    return generalDetailsPersistenceService.getGeneralDetailsByName(name).stream()
        .map(detail -> detail.getSubmissionDetail().getResourceId())
        .distinct()
        .map(submissionDetailService::getLatestSubmissionDetailForResourceOrThrow)
        .map(generalDetailsPersistenceService::getGeneralDetailOrThrow)
        .map(generalDetail -> new ProspectView(generalDetail.getId(), generalDetail.getProspectName()))
        .toList();
  }
}

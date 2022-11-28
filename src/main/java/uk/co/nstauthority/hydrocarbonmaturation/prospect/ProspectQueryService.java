package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesPersistenceService;

@Service
class ProspectQueryService {

  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final TrapPropertiesPersistenceService trapPropertiesPersistenceService;
  private final SubmissionDetailService submissionDetailService;

  @Autowired
  ProspectQueryService(GeneralDetailsPersistenceService generalDetailsPersistenceService,
                       TrapPropertiesPersistenceService trapPropertiesPersistenceService,
                       SubmissionDetailService submissionDetailService) {
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.trapPropertiesPersistenceService = trapPropertiesPersistenceService;
    this.submissionDetailService = submissionDetailService;
  }

  public List<ProspectView> getLatestProspectSubmissionByName(String name) {
    return generalDetailsPersistenceService.getGeneralDetailsByName(name).stream()
        .map(detail -> detail.getSubmissionDetail().getResourceId())
        .distinct()
        .map(submissionDetailService::getLatestSubmissionDetailForResourceOrThrow)
        .map(generalDetailsPersistenceService::getGeneralDetailOrThrow)
        .map(generalDetail -> {
          var centreOfInterestBlock = trapPropertiesPersistenceService
              .getCentreOfInterestBlockFromSubmissionDetail(generalDetail.getSubmissionDetail());
          var prospectDisplayName = generalDetail.getProspectName() + centreOfInterestBlock
              .map(item -> " (" + item + ")")
              .orElse("");
          return new ProspectView(generalDetail.getId(), prospectDisplayName);
        })
        .toList();
  }
}

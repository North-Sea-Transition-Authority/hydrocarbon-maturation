package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesPersistenceService;

@Service
public class SubmissionQueryService {

  private final GeneralDetailsPersistenceService generalDetailsPersistenceService;
  private final TrapPropertiesPersistenceService trapPropertiesPersistenceService;
  private final SubmissionDetailService submissionDetailService;

  @Autowired
  SubmissionQueryService(GeneralDetailsPersistenceService generalDetailsPersistenceService,
                         TrapPropertiesPersistenceService trapPropertiesPersistenceService,
                         SubmissionDetailService submissionDetailService) {
    this.generalDetailsPersistenceService = generalDetailsPersistenceService;
    this.trapPropertiesPersistenceService = trapPropertiesPersistenceService;
    this.submissionDetailService = submissionDetailService;
  }

  public List<ResourceView> getLatestSubmissionByNameAndResourceType(String name, ResourceType resourceType) {
    return generalDetailsPersistenceService.getGeneralDetailsByName(name).stream()
        .map(detail -> detail.getSubmissionDetail().getResourceId())
        .distinct()
        .filter(resource -> resource.getType() == resourceType)
        .map(submissionDetailService::getLatestSubmissionDetailForResourceOrThrow)
        .map(generalDetailsPersistenceService::getGeneralDetailOrThrow)
        .map(generalDetail -> {
          var centreOfInterestBlock = trapPropertiesPersistenceService
              .getCentreOfInterestBlockFromSubmissionDetail(generalDetail.getSubmissionDetail());
          var prospectDisplayName = generalDetail.getProspectName() + centreOfInterestBlock
              .map(item -> " (" + item + ")")
              .orElse("");
          return new ResourceView(generalDetail.getId(), prospectDisplayName);
        })
        .sorted(Comparator.comparing(ResourceView::getSelectionText))
        .toList();
  }
}

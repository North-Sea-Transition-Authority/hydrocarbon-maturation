package uk.co.nstauthority.hydrocarbonmaturation.reservoirs;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLink;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;

@Service
public class ReservoirQueryService {

  private final FieldPersistenceService fieldPersistenceService;
  private final ResourceLinkService resourceLinkService;
  private final SubmissionDetailService submissionDetailService;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final ReservoirLinkService reservoirLinkService;

  @Autowired
  public ReservoirQueryService(FieldPersistenceService fieldPersistenceService, ResourceLinkService resourceLinkService,
                               SubmissionDetailService submissionDetailService,
                               ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
                               ReservoirLinkService reservoirLinkService) {
    this.fieldPersistenceService = fieldPersistenceService;
    this.resourceLinkService = resourceLinkService;
    this.submissionDetailService = submissionDetailService;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.reservoirLinkService = reservoirLinkService;
  }

  public List<ReservoirView> getReservoirByNameForSubmissionDetail(String reservoirName, String submissionDetailId) {
    var submissionDetail = submissionDetailService
        .getSubmissionDetailForIdOrError(UUID.fromString(submissionDetailId));

    var field = fieldPersistenceService.getFieldByResourceOrThrow(submissionDetail.getSubmission().getResource());

    var fieldDiscoveries = resourceLinkService.getResourceLinksByField(field.getResource())
        .stream()
        .map(ResourceLink::getDiscoveryResource);

    // gets the latest submission for each resource and flattens out the Stream of Streams into a singular concatenated Stream
    var latestDiscoverySubmissionDetails =
        fieldDiscoveries
            .map(submissionDetailService::getLatestApprovedDiscoverySubmissionDetailForResource)
            .flatMap(Optional::stream);

    // gets all the reservoir submission details for each of the above submission details and flattens the Streams into a singular
    var discoveryReservoirSubmissionDetails =
        latestDiscoverySubmissionDetails
            .map(reservoirSubmissionDetailPersistenceService::getAllReservoirSubmissionDetailsForSubmissionDetail)
            .flatMap(Collection::stream);

    // filters the reservoirs containing the searched for name and create the reservoir views
    return discoveryReservoirSubmissionDetails
        .filter(reservoirSubmissionDetail ->
            reservoirSubmissionDetail.getReservoir().getName().toLowerCase().contains(reservoirName.toLowerCase()))
        .map(reservoirLinkService::getReservoirViewFromReservoirSubmissionDetail)
        .toList();
  }
}

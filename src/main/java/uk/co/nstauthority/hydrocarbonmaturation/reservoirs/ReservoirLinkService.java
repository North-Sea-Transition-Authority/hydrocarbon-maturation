package uk.co.nstauthority.hydrocarbonmaturation.reservoirs;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionResourceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@Service
public class ReservoirLinkService {

  private final ReservoirLinkRepository reservoirLinkRepository;
  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;
  private final EntityManager entityManager;
  private final SubmissionResourceService submissionResourceService;

  @Autowired
  public ReservoirLinkService(ReservoirLinkRepository reservoirLinkRepository,
                              ResourceDetailsPersistenceService resourceDetailsPersistenceService,
                              EntityManager entityManager,
                              SubmissionResourceService submissionResourceService) {
    this.reservoirLinkRepository = reservoirLinkRepository;
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
    this.entityManager = entityManager;
    this.submissionResourceService = submissionResourceService;
  }

  public List<ReservoirLink> constructReservoirLinks(ReservoirSubmissionDetail reservoirSubmissionDetail,
                                                     List<Reservoir> discoveryReservoirs) {
    var reservoirLinks = new ArrayList<ReservoirLink>();
    discoveryReservoirs
        .forEach(
            discoveryReservoir -> {
              var reservoirLink = new ReservoirLink();
              reservoirLink.setReservoirSubmissionDetail(reservoirSubmissionDetail);
              reservoirLink.setLinkedReservoir(discoveryReservoir);
              reservoirLinks.add(reservoirLink);
            }
    );
    return reservoirLinks;
  }

  @Transactional
  public void saveReservoirLinks(List<ReservoirLink> reservoirLinks) {
    reservoirLinkRepository.saveAll(reservoirLinks);
  }

  @Transactional
  public void deleteReservoirLinks(List<ReservoirLink> reservoirLinks) {
    reservoirLinkRepository.deleteAll(reservoirLinks);
    entityManager.flush();
  }

  public List<ReservoirLink> getReservoirLinksByReservoirSubmissionDetail(
      ReservoirSubmissionDetail reservoirSubmissionDetail) {
    return reservoirLinkRepository.findReservoirLinksByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }

  public ReservoirView getReservoirViewFromReservoirSubmissionDetail(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var submissionDetail = reservoirSubmissionDetail.getSubmissionDetail();
    var reservoir = reservoirSubmissionDetail.getReservoir();
    var resourceDetail = resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail);
    var reservoirViewName = "%s (%s)".formatted(reservoir.getName(), resourceDetail.getResourceName());

    return new ReservoirView(reservoir.getId(), reservoirViewName);
  }

  public boolean reservoirLinksExistForReservoir(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var extantReservoirLinks = getExtantReservoirLinks(reservoirSubmissionDetail);
    return !extantReservoirLinks.isEmpty();
  }

  public List<ReservoirLink> getExtantReservoirLinks(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var reservoir = reservoirSubmissionDetail.getReservoir();
    var reservoirLinks = reservoirLinkRepository.findReservoirLinksByLinkedReservoir(reservoir);
    return reservoirLinks.stream().filter(reservoirLink ->
            !reservoirLink.getReservoirSubmissionDetail().getSubmissionDetail().isDeletedOrWithdrawnSubmission()
        ).toList();
  }

  public List<String> getAssociatedFieldNamesFromLinkedDiscovery(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var extantReservoirLinks = getExtantReservoirLinks(reservoirSubmissionDetail);
    return extantReservoirLinks.stream()
        .map(reservoirLink -> submissionResourceService.getResourceNameOrDefault(
            reservoirLink.getReservoirSubmissionDetail().getSubmissionDetail()
        ))
        .distinct()
        .toList();
  }
}

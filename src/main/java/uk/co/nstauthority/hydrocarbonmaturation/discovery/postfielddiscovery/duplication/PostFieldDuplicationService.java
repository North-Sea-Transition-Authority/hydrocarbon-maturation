package uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.duplication;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.ReservoirHydrocarbonsDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;

@Service
public class PostFieldDuplicationService {

  private final List<PostFieldSectionDuplicationService> postFieldSectionDuplicationServiceList;
  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  private final ReservoirHydrocarbonsDuplicationService reservoirHydrocarbonsDuplicationService;

  @Autowired
  PostFieldDuplicationService(List<PostFieldSectionDuplicationService> postFieldSectionDuplicationServiceList,
                              ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService,
                              ReservoirHydrocarbonsDuplicationService reservoirHydrocarbonsDuplicationService) {
    this.postFieldSectionDuplicationServiceList = postFieldSectionDuplicationServiceList;
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
    this.reservoirHydrocarbonsDuplicationService = reservoirHydrocarbonsDuplicationService;
  }

  public void duplicateSubmissionDetails(SubmissionDetail fieldDetails, SubmissionDetail discoveryDetails) {
    postFieldSectionDuplicationServiceList.forEach(postFieldSectionDuplicationService ->
        postFieldSectionDuplicationService.duplicateSubmissionSection(
            fieldDetails,
            discoveryDetails
        )
    );

    var oldReservoirSubmissionDetails = reservoirSubmissionDetailPersistenceService
            .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(fieldDetails);

    oldReservoirSubmissionDetails.forEach(reservoirSubmissionDetail ->
        reservoirSubmissionDetailPersistenceService.saveReservoirSubmissionDetail(
                reservoirSubmissionDetail.getReservoir(),
                discoveryDetails
      )
    );

    var newReservoirSubmissionDetailsMap = reservoirSubmissionDetailPersistenceService
            .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(discoveryDetails)
            .stream()
            .collect(Collectors.toMap(reservoirSubmissionDetail ->
                    reservoirSubmissionDetail.getReservoir().getId(), Function.identity()));

    oldReservoirSubmissionDetails.forEach(oldReservoirSubmissionDetail -> {
      var newReservoirSubmissionDetail = newReservoirSubmissionDetailsMap
            .get(oldReservoirSubmissionDetail.getReservoir().getId());
      if (newReservoirSubmissionDetail != null) {
        reservoirHydrocarbonsDuplicationService.duplicateReservoirSection(
              oldReservoirSubmissionDetail,
              newReservoirSubmissionDetail
        );
      }
    }
    );
  }
}

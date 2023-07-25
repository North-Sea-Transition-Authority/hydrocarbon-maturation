package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class ReservoirListFormService {

  private final ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Autowired
  public ReservoirListFormService(
      ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService) {
    this.reservoirSubmissionDetailPersistenceService = reservoirSubmissionDetailPersistenceService;
  }


  ReservoirListForm getReservoirForm(SubmissionDetail submissionDetail) {
    return reservoirEntityToForm(
        reservoirSubmissionDetailPersistenceService.getAllReservoirsForSubmissionDetail(submissionDetail)
    );
  }

  private ReservoirListForm reservoirEntityToForm(List<ReservoirSubmissionDetail> reservoirs) {
    var form = new ReservoirListForm();
    form.setReservoirs(reservoirs.stream()
        .map(reservoirSubmissionDetail -> reservoirSubmissionDetail.getReservoir().getName())
        .collect(Collectors.toList())
    );
    return form;
  }
}

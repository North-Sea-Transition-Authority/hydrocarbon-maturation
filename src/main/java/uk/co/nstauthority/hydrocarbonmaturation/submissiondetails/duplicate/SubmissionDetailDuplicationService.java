package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
public class SubmissionDetailDuplicationService {
  private final List<SubmissionSectionDuplicationService> submissionTaskDuplicationServiceList;

  @Autowired
  SubmissionDetailDuplicationService(List<SubmissionSectionDuplicationService> submissionTaskDuplicationServiceList) {
    this.submissionTaskDuplicationServiceList = submissionTaskDuplicationServiceList;
  }

  @Transactional
  public void duplicateSubmissionDetailSections(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    submissionTaskDuplicationServiceList.forEach(submissionTaskDuplicationService ->
        submissionTaskDuplicationService.duplicateSubmissionSection(
            oldSubmissionDetail,
            newSubmissionDetail
        )
    );
  }
}

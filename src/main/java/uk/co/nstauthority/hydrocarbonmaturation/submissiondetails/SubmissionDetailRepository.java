package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

@Repository
interface SubmissionDetailRepository extends CrudRepository<SubmissionDetail, UUID> {

  List<SubmissionDetail> findSubmissionDetailsBySubmissionInOrderByVersionNumberDesc(List<Submission> submissions);

  Optional<SubmissionDetail> findFirstBySubmissionInAndSubmissionTypeInAndStatusInOrderBySubmittedTimestampDesc(
      List<Submission> submissions, List<SubmissionType> submissionTypes, List<SubmissionDetailStatus> statuses);

  Optional<SubmissionDetail> findFirstBySubmissionInAndSubmissionTypeInAndStatusOrderByApprovedTimestampDesc(
      List<Submission> submissions, List<SubmissionType> submissionTypes, SubmissionDetailStatus status);

  Optional<SubmissionDetail> findFirstBySubmissionInAndStatusOrderByApprovedTimestampDesc(
      List<Submission> submissions, SubmissionDetailStatus status);

  Optional<SubmissionDetail> findFirstBySubmissionInAndStatusInOrderByVersionNumberDesc(
      List<Submission> submissions, List<SubmissionDetailStatus> statuses);

  List<SubmissionDetail> findAllBySubmission(Submission submission);

  Optional<SubmissionDetail> findFirstBySubmissionAndStatusInOrderByVersionNumberDesc(
      Submission submission, Set<SubmissionDetailStatus> statuses);

  Optional<SubmissionDetail> findByLegacyResDataId(Integer legacyResDataId);
}

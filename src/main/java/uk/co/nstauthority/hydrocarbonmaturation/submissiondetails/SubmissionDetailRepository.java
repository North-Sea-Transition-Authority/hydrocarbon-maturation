package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

@Repository
public interface SubmissionDetailRepository extends CrudRepository<SubmissionDetail, UUID> {

  @Query("""
      SELECT s FROM SubmissionDetail s
      WHERE s.submission IN (:submissions)
      AND s.submissionType IN (:submissionTypes)
      AND s.status IN (:statuses)
      ORDER BY s.submittedTimestamp DESC, s.correctionTimestamp DESC NULLS LAST, s.legacyResDataId DESC
      FETCH FIRST 1 ROWS ONLY
      """)
  Optional<SubmissionDetail> findLatestForSubmissionsBySubmissionTypesAndStatusesOrderedBySubmittedTimestamp(
      List<Submission> submissions, List<SubmissionType> submissionTypes, List<SubmissionDetailStatus> statuses);

  @Query("""
      SELECT s FROM SubmissionDetail s
      WHERE s.submission IN (:submissions)
      AND s.submissionType IN (:submissionTypes)
      AND s.status = :status
      ORDER BY s.acceptedTimestamp DESC, s.correctionTimestamp DESC NULLS LAST, s.legacyResDataId DESC
      FETCH FIRST 1 ROWS ONLY
      """)
  Optional<SubmissionDetail> findLatestForSubmissionsBySubmissionTypesAndStatusOrderedByAcceptedTimestamp(
      List<Submission> submissions, List<SubmissionType> submissionTypes, SubmissionDetailStatus status);

  @Query("""
      SELECT s FROM SubmissionDetail s
      WHERE s.submission IN (:submissions)
      AND s.status = :status
      ORDER BY s.acceptedTimestamp DESC, s.correctionTimestamp DESC NULLS LAST, s.legacyResDataId DESC
      FETCH FIRST 1 ROWS ONLY
      """)
  Optional<SubmissionDetail> findLatestForSubmissionsByStatusOrderedByAcceptedTimestamp(
      List<Submission> submissions, SubmissionDetailStatus status);

  List<SubmissionDetail> findAllBySubmission(Submission submission);

  Optional<SubmissionDetail> findFirstBySubmissionAndStatusInOrderByVersionNumberDesc(
      Submission submission, Set<SubmissionDetailStatus> statuses);

  boolean existsSubmissionDetailByCorrectionTrueAndStatusInAndSubmission_Resource(
      Set<SubmissionDetailStatus> statuses, Resource resource
  );

  List<SubmissionDetail> findByLegacyResDataId(Integer legacyResDataId);
}

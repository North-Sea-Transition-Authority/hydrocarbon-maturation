package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Repository
interface QuadrantAndBlockRepository extends CrudRepository<QuadrantAndBlock, UUID> {
  List<QuadrantAndBlock> findAllByQuadrantBlockReferenceIn(List<String> quadrantBlockList);

  List<QuadrantAndBlock> findAllByGeneralDetail(GeneralDetail generalDetail);

  List<QuadrantAndBlock> findAllByGeneralDetail_SubmissionDetail(SubmissionDetail submissionDetail);

  Optional<QuadrantAndBlock> findById(UUID quadrantAndBlock);

  void deleteAllByGeneralDetail(GeneralDetail generalDetail);
}

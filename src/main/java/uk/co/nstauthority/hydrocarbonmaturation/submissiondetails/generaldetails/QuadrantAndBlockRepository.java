package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface QuadrantAndBlockRepository extends CrudRepository<QuadrantAndBlock, UUID> {
  List<QuadrantAndBlock> findAllByQuadrantBlockReferenceIn(List<String> quadrantBlockList);

  List<QuadrantAndBlock> findAllByGeneralDetail(GeneralDetail generalDetail);

  Optional<QuadrantAndBlock> findById(UUID quadrantAndBlock);

  void deleteAllByGeneralDetail(GeneralDetail generalDetail);
}

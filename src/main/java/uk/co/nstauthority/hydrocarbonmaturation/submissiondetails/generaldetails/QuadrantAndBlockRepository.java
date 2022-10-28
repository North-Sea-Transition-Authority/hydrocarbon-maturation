package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.List;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
interface QuadrantAndBlockRepository extends CrudRepository<QuadrantAndBlock, UUID> {
  List<QuadrantAndBlock> findAllByQuadrantBlockIn(List<String> quadrantBlockList);

  List<QuadrantAndBlock> findAllByGeneralDetail(GeneralDetail generalDetail);

  void deleteAllByGeneralDetail(GeneralDetail generalDetail);
}

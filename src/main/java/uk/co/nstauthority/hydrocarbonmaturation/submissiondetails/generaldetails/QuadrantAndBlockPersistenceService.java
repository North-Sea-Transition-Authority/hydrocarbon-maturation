package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.enerrgyportal.referenceblock.ReferenceBlockSearchService;

@Service
public class QuadrantAndBlockPersistenceService {

  private final QuadrantAndBlockRepository quadrantAndBlockRepository;
  private final ReferenceBlockSearchService referenceBlockSearchService;

  @Autowired
  public QuadrantAndBlockPersistenceService(QuadrantAndBlockRepository quadrantAndBlockRepository,
                                            ReferenceBlockSearchService referenceBlockSearchService) {
    this.quadrantAndBlockRepository = quadrantAndBlockRepository;
    this.referenceBlockSearchService = referenceBlockSearchService;
  }

  @Transactional
  public void saveQuadrantAndBlocks(GeneralDetailsForm form, GeneralDetail generalDetail) {
    var quadBlocksRefs = form.getQuadrantBlocks().stream()
        .distinct()
        .toList();

    var quadrantAndBlocks = referenceBlockSearchService.getReferenceBlocksByRefsIn(quadBlocksRefs)
        .stream()
        .map(view -> createQuadrantAndBlock(generalDetail, view.id()))
        .toList();

    quadrantAndBlockRepository.deleteAllByGeneralDetail(generalDetail);
    quadrantAndBlockRepository.saveAll(quadrantAndBlocks);
  }

  List<QuadrantAndBlock> getQuadrantBlocksByRefs(List<String> quadrantBlockRefList) {
    return quadrantAndBlockRepository.findAllByQuadrantBlockIn(quadrantBlockRefList);
  }

  List<QuadrantAndBlock> getQuadrantBlocksByGeneralDetail(GeneralDetail generalDetail) {
    return quadrantAndBlockRepository.findAllByGeneralDetail(generalDetail);
  }

  private QuadrantAndBlock createQuadrantAndBlock(GeneralDetail generalDetail, String quadBlockRef) {
    var quadrantAndBlock = new QuadrantAndBlock();
    quadrantAndBlock.setGeneralDetail(generalDetail);
    quadrantAndBlock.setQuadrantBlock(quadBlockRef);

    return quadrantAndBlock;
  }
}

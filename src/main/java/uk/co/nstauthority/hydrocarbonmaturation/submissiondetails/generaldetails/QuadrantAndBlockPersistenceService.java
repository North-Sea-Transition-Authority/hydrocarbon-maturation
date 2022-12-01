package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesRepository;

@Service
public class QuadrantAndBlockPersistenceService {

  private final QuadrantAndBlockRepository quadrantAndBlockRepository;
  private final ReferenceBlockQueryService referenceBlockQueryService;
  private final TrapPropertiesRepository trapPropertyRepository;

  @Autowired
  public QuadrantAndBlockPersistenceService(QuadrantAndBlockRepository quadrantAndBlockRepository,
                                            ReferenceBlockQueryService referenceBlockQueryService,
                                            TrapPropertiesRepository trapPropertyRepository) {
    this.quadrantAndBlockRepository = quadrantAndBlockRepository;
    this.referenceBlockQueryService = referenceBlockQueryService;
    this.trapPropertyRepository = trapPropertyRepository;
  }

  @Transactional
  public void saveQuadrantAndBlocks(GeneralDetailsForm form, GeneralDetail generalDetail) {
    var quadBlocksRefs = form.getQuadrantBlocks().stream()
        .distinct()
        .toList();

    var quadrantAndBlocks = referenceBlockQueryService.getReferenceBlocksByRefsIn(quadBlocksRefs)
        .stream()
        .map(view -> createQuadrantAndBlock(generalDetail, view.blockRef(), view.sortKey()))
        .toList();

    quadrantAndBlockRepository.deleteAllByGeneralDetail(generalDetail);
    updateTrapProperty(generalDetail, quadrantAndBlocks);
    quadrantAndBlockRepository.saveAll(quadrantAndBlocks);
  }

  List<QuadrantAndBlock> getQuadrantBlocksByRefs(List<String> quadrantBlockRefList) {
    return quadrantAndBlockRepository.findAllByQuadrantBlockIn(quadrantBlockRefList);
  }

  public List<QuadrantAndBlock> getQuadrantBlocksByGeneralDetail(GeneralDetail generalDetail) {
    return quadrantAndBlockRepository.findAllByGeneralDetail(generalDetail);
  }

  public Optional<QuadrantAndBlock> getQuadrantBlockById(UUID quadrantAndBlockId) {
    return quadrantAndBlockRepository.findById(quadrantAndBlockId);
  }

  /**
   * This method ensures the quad block is removed from trap property, when its been removed in general details.
   * @param generalDetail general detail that was updated
   * @param quadrantAndBlocks newly saved quadrant and blocks
   */
  @Transactional
  public void updateTrapProperty(GeneralDetail generalDetail, List<QuadrantAndBlock> quadrantAndBlocks) {
    trapPropertyRepository.findBySubmissionDetail(generalDetail.getSubmissionDetail())
        .ifPresent(trapProperty -> {
          var quadBlock = trapProperty.getCentreOfInterestQuadrantBlock();
          trapProperty.setCentreOfInterestQuadrantBlock(null);
          if (quadBlock != null) {
            quadrantAndBlocks
                .stream()
                .filter(item -> quadBlock.equals(item.getQuadrantBlock()))
                .findAny()
                .ifPresent(block -> trapProperty.setCentreOfInterestQuadrantBlock(block.getQuadrantBlock()));
            trapPropertyRepository.save(trapProperty);
          }
        });
  }

  private QuadrantAndBlock createQuadrantAndBlock(GeneralDetail generalDetail, String quadBlockRef, String sortKey) {
    var quadrantAndBlock = new QuadrantAndBlock();
    quadrantAndBlock.setGeneralDetail(generalDetail);
    quadrantAndBlock.setQuadrantBlock(quadBlockRef);
    quadrantAndBlock.setSortKey(sortKey);

    return quadrantAndBlock;
  }
}

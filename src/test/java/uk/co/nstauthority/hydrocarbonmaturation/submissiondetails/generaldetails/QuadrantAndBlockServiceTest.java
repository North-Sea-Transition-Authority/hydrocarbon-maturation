package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static java.util.Map.entry;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class QuadrantAndBlockServiceTest {
  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;
  @Mock
  private QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;
  @InjectMocks
  private QuadrantAndBlockService quadrantAndBlockService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
  }

  @Test
  void getQuadrantAndBlocks_noResults_assertEmptyMap() {
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksBySubmissionDetail(submissionDetail))
        .thenReturn(List.of());

    var quadrantBlocks = quadrantAndBlockService.getQuadrantAndBlocks(submissionDetail);

    assertThat(quadrantBlocks)
        .isEmpty();
  }

  @Test
  void getQuadrantAndBlocks_singleResult_assertPresent() {
    var quadrantBlock = new QuadrantAndBlock();
    quadrantBlock.setQuadrantBlockReference("quad1");
    var blockId = UUID.randomUUID();
    quadrantBlock.setId(blockId);

    when(quadrantAndBlockPersistenceService.getQuadrantBlocksBySubmissionDetail(submissionDetail))
        .thenReturn(List.of(quadrantBlock));

    var quadrantBlocks = quadrantAndBlockService.getQuadrantAndBlocks(submissionDetail);

    assertThat(quadrantBlocks)
        .containsExactly(
            entry("quad1", "quad1")
        );
  }

  @Test
  void getQuadrantAndBlocks_multipleResults_assertSortedByQuadrant() {
    var quadrantBlock1 = new QuadrantAndBlock();
    var quadrantBlock2 = new QuadrantAndBlock();
    var quadrantBlock3 = new QuadrantAndBlock();
    quadrantBlock1.setQuadrantBlockReference("110/2");
    quadrantBlock1.setQuadrant("110");
    quadrantBlock1.setBlock(2);
    quadrantBlock1.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    quadrantBlock2.setQuadrantBlockReference("NA70");
    quadrantBlock2.setQuadrant("NA");
    quadrantBlock2.setBlock(70);
    quadrantBlock2.setOnshoreOffshore(OnshoreOffshore.ONSHORE);
    quadrantBlock3.setQuadrantBlockReference("100/3");
    quadrantBlock3.setQuadrant("100");
    quadrantBlock3.setBlock(3);
    quadrantBlock3.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);
    var quadrantAndBlockId = UUID.randomUUID();
    quadrantBlock1.setId(quadrantAndBlockId);
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksBySubmissionDetail(submissionDetail))
        .thenReturn(List.of(quadrantBlock1, quadrantBlock2, quadrantBlock3));

    var quadrantBlocks = quadrantAndBlockService.getQuadrantAndBlocks(submissionDetail);

    assertThat(quadrantBlocks)
        .containsExactly(
            entry("100/3", "100/3"),
            entry("110/2", "110/2"),
            entry("NA70", "NA70")
        );
  }

  @Test
  void getQuadrantAndBlocks_multipleResults_assertSortedByBlock() {
    var quadrantBlock1 = new QuadrantAndBlock();
    var quadrantBlock2 = new QuadrantAndBlock();
    var quadrantBlock3 = new QuadrantAndBlock();
    quadrantBlock1.setQuadrantBlockReference("110/1");
    quadrantBlock1.setQuadrant("110");
    quadrantBlock1.setBlock(1);
    quadrantBlock1.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);

    quadrantBlock2.setQuadrantBlockReference("110/2");
    quadrantBlock2.setQuadrant("110");
    quadrantBlock2.setBlock(2);
    quadrantBlock2.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);

    quadrantBlock3.setQuadrantBlockReference("110/10");
    quadrantBlock3.setQuadrant("110");
    quadrantBlock3.setBlock(10);
    quadrantBlock3.setOnshoreOffshore(OnshoreOffshore.OFFSHORE);

    var quadrantAndBlockId = UUID.randomUUID();
    quadrantBlock1.setId(quadrantAndBlockId);

    when(quadrantAndBlockPersistenceService.getQuadrantBlocksBySubmissionDetail(submissionDetail))
        .thenReturn(List.of(quadrantBlock1, quadrantBlock2, quadrantBlock3));

    var quadrantBlocks = quadrantAndBlockService.getQuadrantAndBlocks(submissionDetail);

    assertThat(quadrantBlocks)
        .containsExactly(
            entry("110/1", "110/1"),
            entry("110/2", "110/2"),
            entry("110/10", "110/10")
        );
  }
}
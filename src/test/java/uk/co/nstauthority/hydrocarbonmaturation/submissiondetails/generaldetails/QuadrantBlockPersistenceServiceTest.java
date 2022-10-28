package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.enerrgyportal.referenceblock.ReferenceBlockSearchService;
import uk.co.nstauthority.hydrocarbonmaturation.enerrgyportal.referenceblock.ReferenceBlockView;

@ExtendWith(MockitoExtension.class)
class QuadrantBlockPersistenceServiceTest {
  @Mock
  private QuadrantAndBlockRepository quadrantAndBlockRepository;
  @Mock
  private ReferenceBlockSearchService referenceBlockSearchService;
  @InjectMocks
  private QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;

  @Test
  void saveQuadrantAndBlocks_VerifyDeletesExistingBeforeSavingNew() {
    var generalDetail = mock(GeneralDetail.class);

    var qbRef1 = "27/40";
    var quadBlockRefs = List.of(qbRef1);
    var generalDetailsForm = new GeneralDetailsForm();
    generalDetailsForm.setQuadrantBlocks(quadBlockRefs);

    when(referenceBlockSearchService.getReferenceBlocksByRefsIn(quadBlockRefs))
        .thenReturn(List.of(new ReferenceBlockView(qbRef1, qbRef1)));

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<QuadrantAndBlock>> argumentCaptor = ArgumentCaptor.forClass(List.class);
    quadrantAndBlockPersistenceService.saveQuadrantAndBlocks(generalDetailsForm, generalDetail);

    var orderVerifier  = inOrder(quadrantAndBlockRepository);
    orderVerifier.verify(quadrantAndBlockRepository).deleteAllByGeneralDetail(generalDetail);
    orderVerifier.verify(quadrantAndBlockRepository).saveAll(argumentCaptor.capture());

    assertThat(argumentCaptor.getValue())
        .extracting(
            QuadrantAndBlock::getGeneralDetail,
            QuadrantAndBlock::getQuadrantBlock
        )
        .containsExactly(
            tuple(
                generalDetail,
                qbRef1
            )
        );
  }

  @Test
  void getQuadrantBlocksByRefs_VerifyCalls() {
    var quadBlockRefs = List.of("27/40");

    quadrantAndBlockPersistenceService.getQuadrantBlocksByRefs(quadBlockRefs);

    verify(quadrantAndBlockRepository).findAllByQuadrantBlockIn(quadBlockRefs);
  }

  @Test
  void getQuadrantBlocksByGeneralDetail_VerifyCalls() {
    var generalDetail = mock(GeneralDetail.class);

    quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail);

    verify(quadrantAndBlockRepository).findAllByGeneralDetail(generalDetail);
  }
}
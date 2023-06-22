package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.referenceblock.ReferenceBlockView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapProperty;

@ExtendWith(MockitoExtension.class)
class QuadrantAndBlockPersistenceServiceTest {
  @Mock
  private QuadrantAndBlockRepository quadrantAndBlockRepository;
  @Mock
  private ReferenceBlockQueryService referenceBlockQueryService;
  @Mock
  private TrapPropertiesRepository trapPropertiesRepository;
  @InjectMocks
  private QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @Test
  void saveQuadrantAndBlocks_VerifyDeletesExistingBeforeSavingNew() {
    var generalDetail = mock(GeneralDetail.class);

    var qbRef1 = "27/40";
    var referenceBlockView = new ReferenceBlockView(
        qbRef1,
        "27",
        40,
        OnshoreOffshore.OFFSHORE
    );
    var quadBlockRefs = List.of(qbRef1);
    var generalDetailsForm = new GeneralDetailsForm();
    generalDetailsForm.setQuadrantBlocks(quadBlockRefs);

    when(referenceBlockQueryService.getReferenceBlocksByRefsIn(quadBlockRefs))
        .thenReturn(List.of(referenceBlockView));

    @SuppressWarnings("unchecked")
    ArgumentCaptor<List<QuadrantAndBlock>> argumentCaptor = ArgumentCaptor.forClass(List.class);
    quadrantAndBlockPersistenceService.saveQuadrantAndBlocks(generalDetailsForm, generalDetail);

    var orderVerifier  = inOrder(quadrantAndBlockRepository);
    orderVerifier.verify(quadrantAndBlockRepository).deleteAllByGeneralDetail(generalDetail);
    orderVerifier.verify(quadrantAndBlockRepository).saveAll(argumentCaptor.capture());

    assertThat(argumentCaptor.getValue())
        .extracting(
            QuadrantAndBlock::getGeneralDetail,
            QuadrantAndBlock::getQuadrantBlockReference,
            QuadrantAndBlock::getQuadrant,
            QuadrantAndBlock::getBlock,
            QuadrantAndBlock::getOnshoreOffshore
        )
        .containsExactly(
            tuple(
                generalDetail,
                referenceBlockView.quadAndBlockRef(),
                referenceBlockView.quadrant(),
                referenceBlockView.blockNo(),
                referenceBlockView.onshoreOffshore()
            )
        );
  }

  @Test
  void removeCurrentQuadrantAndBlocks_VerifyCalls() {
    var generalDetail = mock(GeneralDetail.class);

    quadrantAndBlockPersistenceService.removeCurrentQuadsAndBlocks(generalDetail);

    var orderVerifier  = inOrder(quadrantAndBlockRepository);
    orderVerifier.verify(quadrantAndBlockRepository).deleteAllByGeneralDetail(generalDetail);
  }

  @Test
  void getQuadrantBlocksByRefs_VerifyCalls() {
    var quadBlockRefs = List.of("27/40");

    quadrantAndBlockPersistenceService.getQuadrantBlocksByRefs(quadBlockRefs);

    verify(quadrantAndBlockRepository).findAllByQuadrantBlockReferenceIn(quadBlockRefs);
  }

  @Test
  void getQuadrantBlocksByGeneralDetail_VerifyCalls() {
    var generalDetail = mock(GeneralDetail.class);

    quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail);

    verify(quadrantAndBlockRepository).findAllByGeneralDetail(generalDetail);
  }

  @Test
  void getQuadrantBlockById_VerifyCall() {
    var id = UUID.randomUUID();
    quadrantAndBlockPersistenceService.getQuadrantBlockById(id);

    verify(quadrantAndBlockRepository).findById(id);
  }

  @Test
  void updateTrapProperty_whenEmptyTrapProperty() {
    var trapProperty = new TrapProperty();
    var generalDetail = new GeneralDetail();
    generalDetail.setSubmissionDetail(submissionDetail);

    when(trapPropertiesRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(trapProperty));

    quadrantAndBlockPersistenceService.updateTrapProperty(generalDetail, List.of(new QuadrantAndBlock()));

    verify(trapPropertiesRepository, never()).save(any());
  }

  @Test
  void updateTrapProperty_whenFilledCentreOfInterestAndQuadBlockDeleted_assertCentreOfInterestNull() {
    var trapProperty = new TrapProperty();
    trapProperty.setCentreOfInterestQuadrantBlock("10/15");
    var genDetail = new GeneralDetail();
    genDetail.setSubmissionDetail(submissionDetail);
    when(trapPropertiesRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(trapProperty));

    quadrantAndBlockPersistenceService.updateTrapProperty(genDetail, List.of(new QuadrantAndBlock()));
    ArgumentCaptor<TrapProperty> argumentCaptor = ArgumentCaptor.forClass(TrapProperty.class);

    verify(trapPropertiesRepository, times(1)).save(argumentCaptor.capture());

    assertThat(argumentCaptor.getValue())
        .extracting(
            TrapProperty::getCentreOfInterestQuadrantBlock)
        .isNull();
  }

  @Test
  void updateTrapProperty_whenFilledCentreOfInterestAndQuadBlockNotDeleted_assertCentreOfInterestSaved() {
    var trapProperty = new TrapProperty();
    trapProperty.setCentreOfInterestQuadrantBlock("10/15");
    var genDetail = new GeneralDetail();
    genDetail.setSubmissionDetail(submissionDetail);
    when(trapPropertiesRepository.findBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(trapProperty));

    var centreOfInterestQuadBlock = new QuadrantAndBlock();
    centreOfInterestQuadBlock.setQuadrantBlockReference("10/15");

    quadrantAndBlockPersistenceService.updateTrapProperty(genDetail, List.of(centreOfInterestQuadBlock, new QuadrantAndBlock(), new QuadrantAndBlock()));
    ArgumentCaptor<TrapProperty> argumentCaptor = ArgumentCaptor.forClass(TrapProperty.class);

    verify(trapPropertiesRepository, times(1)).save(argumentCaptor.capture());

    assertThat(argumentCaptor.getValue())
        .extracting(
            TrapProperty::getCentreOfInterestQuadrantBlock)
        .isEqualTo(centreOfInterestQuadBlock.getQuadrantBlockReference());
  }
}
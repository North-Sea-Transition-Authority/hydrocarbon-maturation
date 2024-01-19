package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.StandardLithology;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeries;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.Formation;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.GroupName;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;
import uk.co.nstauthority.hydrocarbonmaturation.util.NumberUtil;

@ExtendWith(MockitoExtension.class)
class SourceAndSealSummaryServiceTest {

  @Mock
  private SourceAndSealPersistenceService sourceAndSealPersistenceService;

  @InjectMocks
  private SourceAndSealSummaryService sourceAndSealSummaryService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

  private final SourceAndSeal sourceAndSeal = new SourceAndSeal();

  @Test
  void getSourceAndSealSummaryFromSubmissionDetail_emptyView() {
    when(sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail))
        .thenReturn(Optional.of(sourceAndSeal));

    var summaryView = sourceAndSealSummaryService
        .getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);

    assertThat(summaryView)
        .extracting(
            SourceAndSealSummaryView::sourceLithology,
            SourceAndSealSummaryView::sealLithology)
        .containsExactly(
            "",
            ""
        );
  }

  @Test
  void getSourceAndSealSummaryFromSubmissionDetail_view() {
    sourceAndSeal.setSourceAgeStage(AgeSeries.LOWER_CARBONIFEROUS);
    sourceAndSeal.setSourceGroupName(GroupName.BRENT);
    sourceAndSeal.setSourceLithology(StandardLithology.COAL);
    sourceAndSeal.setTopSealAgeStage(AgeSeries.LOWER_DEVONIAN);
    sourceAndSeal.setTopSealGroupName(GroupName.BRUCE);
    sourceAndSeal.setTopSealLithology(StandardLithology.COAL);

    when(sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail))
        .thenReturn(Optional.of(sourceAndSeal));

    var summaryView = sourceAndSealSummaryService
        .getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);

    assertThat(summaryView)
        .extracting(
            SourceAndSealSummaryView::sourceAgeStage,
            SourceAndSealSummaryView::sourceGroupName,
            SourceAndSealSummaryView::sourceLithology,
            SourceAndSealSummaryView::sealAgeStage,
            SourceAndSealSummaryView::sealGroupName,
            SourceAndSealSummaryView::sealLithology)
        .containsExactly(
            AgeSeries.LOWER_CARBONIFEROUS.getDisplayName(),
            GroupName.BRENT.getDisplayName(),
            StandardLithology.COAL.getDisplayName(),
            AgeSeries.LOWER_DEVONIAN.getDisplayName(),
            GroupName.BRUCE.getDisplayName(),
            StandardLithology.COAL.getDisplayName()
        );
  }

  @Test
  void getSourceAndSealSummaryFromSubmissionDetail_formationAndAgeStageEnumView() {
    sourceAndSeal.setSourceFormationName(Formation.ASGARD);
    sourceAndSeal.setTopSealFormationName(Formation.AUK);
    sourceAndSeal.setSourceAgeStage(AgeSeries.LOWER_CARBONIFEROUS);
    sourceAndSeal.setTopSealAgeStage(AgeSeries.PLIOCENE);

    when(sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail))
        .thenReturn(Optional.of(sourceAndSeal));

    var summaryView = sourceAndSealSummaryService
        .getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);

    assertThat(summaryView)
        .extracting(
            SourceAndSealSummaryView::sourceFormationName,
            SourceAndSealSummaryView::sealFormationName,
            SourceAndSealSummaryView::sourceAgeStage,
            SourceAndSealSummaryView::sealAgeStage
        )
        .containsExactly(
            Formation.ASGARD.getDisplayName(),
            Formation.AUK.getDisplayName(),
            AgeSeries.LOWER_CARBONIFEROUS.getDisplayName(),
            AgeSeries.PLIOCENE.getDisplayName()
        );
  }

  @Test
  void getSourceAndSealSummaryFromSubmissionDetail_manualFormationView() {
    var sourceFormation = "manual source formation";
    var sealFormation = "manual seal formation";
    var sourceAgeStage = "manual source age stage";
    var sealAgeStage = "manual seal age stage";
    sourceAndSeal.setSourceFormationNameManual(sourceFormation);
    sourceAndSeal.setTopSealFormationNameManual(sealFormation);
    sourceAndSeal.setSourceAgeStageManual(sourceAgeStage);
    sourceAndSeal.setTopSealAgeStageManual(sealAgeStage);

    when(sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail))
        .thenReturn(Optional.of(sourceAndSeal));

    var summaryView = sourceAndSealSummaryService
        .getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);

    assertThat(summaryView)
        .extracting(
            SourceAndSealSummaryView::sourceFormationName,
            SourceAndSealSummaryView::sealFormationName,
            SourceAndSealSummaryView::sourceAgeStage,
            SourceAndSealSummaryView::sealAgeStage
        )
        .containsExactly(
            sourceFormation,
            sealFormation,
            sourceAgeStage,
            sealAgeStage
        );
  }

  @Test
  void getSourceAndSealSummaryFromSubmissionDetail_otherSealLithologyView() {
    var otherSealLithology = "other";
    sourceAndSeal.setTopSealLithology(StandardLithology.OTHER);
    sourceAndSeal.setTopSealLithologyOther(otherSealLithology);

    when(sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail))
        .thenReturn(Optional.of(sourceAndSeal));

    var summaryView = sourceAndSealSummaryService
        .getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);

    assertThat(summaryView.sealLithology()).isEqualTo(otherSealLithology);
  }

  @Test
  void getSourceAndSealSummaryFromSubmissionDetail_otherSourceLithologyView() {
    var otherSourceLithology = "other";
    sourceAndSeal.setSourceLithology(StandardLithology.OTHER);
    sourceAndSeal.setSourceLithologyOther(otherSourceLithology);

    when(sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail))
        .thenReturn(Optional.of(sourceAndSeal));

    var summaryView = sourceAndSealSummaryService
        .getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);

    assertThat(summaryView.sourceLithology()).isEqualTo(otherSourceLithology);
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void getFieldSourceAndSealSummaryCard_nonManualEntrys_assertProperties(SubmissionType submissionType) {
    SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var sourceAndSeal = sourceAndSealWithNonManualEntrys();
    when(sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail))
        .thenReturn(Optional.of(sourceAndSeal));

    SummaryDataView summaryCard = (SummaryDataView) sourceAndSealSummaryService
        .getFieldSourceAndSealSummaryCard(submissionDetail)
        .summaryData();

    assertThat(summaryCard.keyValues())
        .extracting(
            SummaryKeyValue::key,
            SummaryKeyValue::value
        )
        .containsExactly(
            Tuple.tuple(SourceAndSealSummaryService.SOURCE_AGE_STAGE, sourceAndSeal.getSourceAgeStage().getDisplayName()),
            Tuple.tuple(SourceAndSealSummaryService.SOURCE_GROUP_NAME, sourceAndSeal.getSourceGroupName().getDisplayName()),
            Tuple.tuple(SourceAndSealSummaryService.SOURCE_FORMATION_NAME, sourceAndSeal.getSourceFormationName().getDisplayName()),
            Tuple.tuple(SourceAndSealSummaryService.SOURCE_LITHOLOGY, sourceAndSeal.getSourceLithology().getDisplayName()),
            Tuple.tuple(SourceAndSealSummaryService.TOP_SEAL_AGE_STAGE, sourceAndSeal.getTopSealAgeStage().getDisplayName()),
            Tuple.tuple(SourceAndSealSummaryService.TOP_SEAL_GROUP_NAME, sourceAndSeal.getTopSealGroupName().getDisplayName()),
            Tuple.tuple(SourceAndSealSummaryService.TOP_SEAL_FORMATION_NAME, sourceAndSeal.getTopSealFormationName().getDisplayName()),
            Tuple.tuple(SourceAndSealSummaryService.TOP_SEAL_LITHOLOGY, sourceAndSeal.getTopSealLithology().getDisplayName()),

            Tuple.tuple(SourceAndSealSummaryService.SEAL_THICKNESS,
                NumberUtil.getNumberStringWithUnitsOrEmpty(sourceAndSeal.getSealThickness(), "m")),
            Tuple.tuple(SourceAndSealSummaryService.TOP_SEAL_FRACTURE_PRESSURE,
                NumberUtil.getNumberStringWithUnitsOrEmpty(sourceAndSeal.getTopSealFracturePressure(), "psi"))
        );
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void getFieldSourceAndSealSummaryCard_manualEntrys_assertProperties(SubmissionType submissionType) {
    SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var sourceAndSeal = sourceAndSealWithManualEntrys();
    when(sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail))
        .thenReturn(Optional.of(sourceAndSeal));

    SummaryDataView summaryCard = (SummaryDataView) sourceAndSealSummaryService
        .getFieldSourceAndSealSummaryCard(submissionDetail)
        .summaryData();

    assertThat(summaryCard.keyValues())
        .extracting(
            SummaryKeyValue::key,
            SummaryKeyValue::value
        )
        .containsExactly(
            Tuple.tuple(SourceAndSealSummaryService.SOURCE_AGE_STAGE, sourceAndSeal.getSourceAgeStageManual()),
            Tuple.tuple(SourceAndSealSummaryService.SOURCE_GROUP_NAME, sourceAndSeal.getSourceGroupName().getDisplayName()),
            Tuple.tuple(SourceAndSealSummaryService.SOURCE_FORMATION_NAME, sourceAndSeal.getSourceFormationNameManual()),
            Tuple.tuple(SourceAndSealSummaryService.SOURCE_LITHOLOGY, sourceAndSeal.getSourceLithologyOther()),
            Tuple.tuple(SourceAndSealSummaryService.TOP_SEAL_AGE_STAGE, sourceAndSeal.getTopSealAgeStageManual()),
            Tuple.tuple(SourceAndSealSummaryService.TOP_SEAL_GROUP_NAME, sourceAndSeal.getTopSealGroupName().getDisplayName()),
            Tuple.tuple(SourceAndSealSummaryService.TOP_SEAL_FORMATION_NAME, sourceAndSeal.getTopSealFormationNameManual()),
            Tuple.tuple(SourceAndSealSummaryService.TOP_SEAL_LITHOLOGY, sourceAndSeal.getTopSealLithologyOther()),

            Tuple.tuple(SourceAndSealSummaryService.SEAL_THICKNESS,
                NumberUtil.getNumberStringWithUnitsOrEmpty(sourceAndSeal.getSealThickness(), "m")),
            Tuple.tuple(SourceAndSealSummaryService.TOP_SEAL_FRACTURE_PRESSURE,
                NumberUtil.getNumberStringWithUnitsOrEmpty(sourceAndSeal.getTopSealFracturePressure(), "psi"))
        );
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void getFieldSourceAndSealSummaryCard_noSourceAndSealExists_assertProperties(SubmissionType submissionType) {
    SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    when(sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail))
        .thenReturn(Optional.empty());

    var summaryCard = sourceAndSealSummaryService
        .getFieldSourceAndSealSummaryCard(submissionDetail);

    assertThat(summaryCard).usingRecursiveComparison()
        .isEqualTo(SummaryCard.emptySummaryCardWithHeading(SourceAndSealSummaryService.SOURCE_AND_SEAL));
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void getFieldSourceAndSealSummaryCard_sourceAndSealExistsWithAllEmptyValues_assertProperties(SubmissionType submissionType) {
    SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var emptySourceAndSeal = new SourceAndSeal();
    emptySourceAndSeal.setSubmissionDetail(submissionDetail);

    when(sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail))
        .thenReturn(Optional.of(emptySourceAndSeal));

    SummaryDataView summaryCard = (SummaryDataView) sourceAndSealSummaryService
        .getFieldSourceAndSealSummaryCard(submissionDetail)
        .summaryData();

    assertThat(summaryCard.keyValues())
        .extracting(
            SummaryKeyValue::key,
            SummaryKeyValue::value
        )
        .containsExactly(
            Tuple.tuple(SourceAndSealSummaryService.SOURCE_AGE_STAGE, ""),
            Tuple.tuple(SourceAndSealSummaryService.SOURCE_GROUP_NAME, ""),
            Tuple.tuple(SourceAndSealSummaryService.SOURCE_FORMATION_NAME, ""),
            Tuple.tuple(SourceAndSealSummaryService.SOURCE_LITHOLOGY, ""),
            Tuple.tuple(SourceAndSealSummaryService.TOP_SEAL_AGE_STAGE, ""),
            Tuple.tuple(SourceAndSealSummaryService.TOP_SEAL_GROUP_NAME, ""),
            Tuple.tuple(SourceAndSealSummaryService.TOP_SEAL_FORMATION_NAME, ""),
            Tuple.tuple(SourceAndSealSummaryService.TOP_SEAL_LITHOLOGY, ""),
            Tuple.tuple(SourceAndSealSummaryService.SEAL_THICKNESS, ""),
            Tuple.tuple(SourceAndSealSummaryService.TOP_SEAL_FRACTURE_PRESSURE, "")
        );
  }

  private SourceAndSeal sourceAndSealWithNonManualEntrys() {
    var sourceAndSeal = new SourceAndSeal();
    sourceAndSeal.setSourceFormationName(Formation.NAUST);
    sourceAndSeal.setSourceAgeStage(AgeSeries.LOWER_CARBONIFEROUS);
    sourceAndSeal.setSourceGroupName(GroupName.BRUCE);
    sourceAndSeal.setSourceLithology(StandardLithology.LIMESTONE);
    sourceAndSeal.setTopSealFormationName(Formation.NESS);
    sourceAndSeal.setTopSealAgeStage(AgeSeries.PLEISTOCENE);
    sourceAndSeal.setTopSealGroupName(GroupName.CHALK);
    sourceAndSeal.setTopSealLithology(StandardLithology.COAL);
    sourceAndSeal.setSealThickness(BigDecimal.ONE);
    sourceAndSeal.setTopSealFracturePressure(BigDecimal.TEN);

    return sourceAndSeal;
  }

  private SourceAndSeal sourceAndSealWithManualEntrys() {
    var sourceAndSeal = new SourceAndSeal();
    sourceAndSeal.setSourceFormationNameManual("source formation");
    sourceAndSeal.setSourceGroupName(GroupName.BRUCE);
    sourceAndSeal.setSourceAgeStageManual("source age");
    sourceAndSeal.setSourceLithology(StandardLithology.OTHER);
    sourceAndSeal.setSourceLithologyOther("source lithology");
    sourceAndSeal.setTopSealFormationNameManual("top seal formation");
    sourceAndSeal.setTopSealGroupName(GroupName.CHALK);
    sourceAndSeal.setTopSealAgeStageManual("top seal age");
    sourceAndSeal.setTopSealLithology(StandardLithology.OTHER);
    sourceAndSeal.setTopSealLithologyOther("top seal lithology");
    sourceAndSeal.setSealThickness(BigDecimal.ONE);
    sourceAndSeal.setTopSealFracturePressure(BigDecimal.TEN);

    return sourceAndSeal;
  }

  private static Stream<Arguments> allFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(SubmissionDetailUtil::isFieldSubmission)
        .map(Arguments::of);
  }

  private static Stream<Arguments> allNonFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> !SubmissionDetailUtil.isFieldSubmission(submissionType))
        .map(Arguments::of);
  }
}
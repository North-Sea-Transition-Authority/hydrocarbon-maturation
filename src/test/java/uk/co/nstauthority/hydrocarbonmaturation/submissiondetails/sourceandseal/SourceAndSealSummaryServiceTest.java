package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.AgeSeries;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.Formation;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.formation.GroupName;

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
            SourceAndSealSummaryView::sourceAgeStage,
            SourceAndSealSummaryView::sourceLithology,
            SourceAndSealSummaryView::sealAgeStage,
            SourceAndSealSummaryView::sealLithology)
        .containsExactly(
            "",
            "",
            "",
            ""
        );
  }

  @Test
  void getSourceAndSealSummaryFromSubmissionDetail_view() {
    sourceAndSeal.setSourceAgeStage(AgeSeries.LOWER_CARBONIFEROUS);
    sourceAndSeal.setSourceGroupName(GroupName.BRENT);
    sourceAndSeal.setSourceLithology("source lithology");
    sourceAndSeal.setTopSealAgeStage(AgeSeries.LOWER_DEVONIAN);
    sourceAndSeal.setTopSealGroupName(GroupName.BRUCE);
    sourceAndSeal.setTopSealLithology(TopSealLithology.CEMENTED_SANDSTONE);

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
            "source lithology",
            AgeSeries.LOWER_DEVONIAN.getDisplayName(),
            GroupName.BRUCE.getDisplayName(),
            TopSealLithology.CEMENTED_SANDSTONE.getDisplayName()
        );
  }

  @Test
  void getSourceAndSealSummaryFromSubmissionDetail_formationEnumView() {
    sourceAndSeal.setSourceFormationName(Formation.ASGARD);
    sourceAndSeal.setTopSealFormationName(Formation.AUK);

    when(sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail))
        .thenReturn(Optional.of(sourceAndSeal));

    var summaryView = sourceAndSealSummaryService
        .getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);

    assertThat(summaryView)
        .extracting(
            SourceAndSealSummaryView::sourceFormationName,
            SourceAndSealSummaryView::sealFormationName
        )
        .containsExactly(
            Formation.ASGARD.getDisplayName(),
            Formation.AUK.getDisplayName()
        );
  }

  @Test
  void getSourceAndSealSummaryFromSubmissionDetail_manualFormationView() {
    var sourceFormation = "manual source formation";
    var sealFormation = "manual seal formation";
    sourceAndSeal.setSourceFormationNameManual(sourceFormation);
    sourceAndSeal.setTopSealFormationNameManual(sealFormation);

    when(sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail))
        .thenReturn(Optional.of(sourceAndSeal));

    var summaryView = sourceAndSealSummaryService
        .getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);

    assertThat(summaryView)
        .extracting(
            SourceAndSealSummaryView::sourceFormationName,
            SourceAndSealSummaryView::sealFormationName
        )
        .containsExactly(
            sourceFormation,
            sealFormation
        );
  }

  @Test
  void getSourceAndSealSummaryFromSubmissionDetail_otherSeaLithologyView() {
    var otherSealLithology = "other";
    sourceAndSeal.setTopSealLithology(TopSealLithology.OTHER);
    sourceAndSeal.setTopSealLithologyOther(otherSealLithology);

    when(sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail))
        .thenReturn(Optional.of(sourceAndSeal));

    var summaryView = sourceAndSealSummaryService
        .getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);

    assertThat(summaryView.sealLithology()).isEqualTo(otherSealLithology);
  }
}
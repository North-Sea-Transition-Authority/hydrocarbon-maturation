package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

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
            SourceAndSealSummaryView::sourceFormationName,
            SourceAndSealSummaryView::sourceAgeStage,
            SourceAndSealSummaryView::sourceLithology,
            SourceAndSealSummaryView::sealAgeStage,
            SourceAndSealSummaryView::sealLithology,
            SourceAndSealSummaryView::sealFormationName)
        .containsExactly(
            "",
            "",
            "",
            "",
            "",
            ""
        );
  }

  @Test
  void getSourceAndSealSummaryFromSubmissionDetail_fullView() {
    sourceAndSeal.setSourceLithology("source lithology");
    sourceAndSeal.setSourceFormationName("source formation name");
    sourceAndSeal.setSourceAgeStage("source age");
    sourceAndSeal.setTopSealFormationName("seal name");
    sourceAndSeal.setTopSealLithology("seal lithology");
    sourceAndSeal.setTopSealAgeStage("seal age");

    when(sourceAndSealPersistenceService.getSourceAndSeal(submissionDetail))
        .thenReturn(Optional.of(sourceAndSeal));

    var summaryView = sourceAndSealSummaryService
        .getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);

    assertThat(summaryView)
        .extracting(
            SourceAndSealSummaryView::sourceFormationName,
            SourceAndSealSummaryView::sourceAgeStage,
            SourceAndSealSummaryView::sourceLithology,
            SourceAndSealSummaryView::sealAgeStage,
            SourceAndSealSummaryView::sealLithology,
            SourceAndSealSummaryView::sealFormationName)
        .containsExactly(
            "source formation name",
            "source age",
            "source lithology",
            "seal age",
            "seal lithology",
            "seal name"
        );
  }
}
package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class PermeabilitySummaryServiceTest {

  @Mock
  private PermeabilityPersistenceService permeabilityPersistenceService;

  @InjectMocks
  private PermeabilitySummaryService permeabilitySummaryService;

  private SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();

  @Test
  void getPermeabilitySummaryFromSubmissionDetail_whenNoPermeability() {
    when(permeabilityPersistenceService.getPermeabilityDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var view = permeabilitySummaryService.getPermeabilitySummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            PermeabilityDetailSummaryView::hasPermeability,
            PermeabilityDetailSummaryView::hasPermeabilityBol,
            PermeabilityDetailSummaryView::permeabilityMin,
            PermeabilityDetailSummaryView::permeabilityMax,
            PermeabilityDetailSummaryView::permeabilityAverage,
            PermeabilityDetailSummaryView::averageMethodUsed,
            PermeabilityDetailSummaryView::permeabilitySource,
            PermeabilityDetailSummaryView::otherPermeabilitySource,
            PermeabilityDetailSummaryView::permeabilityComments,
            PermeabilityDetailSummaryView::kvKhAverage)
        .containsOnly("", true);
  }

  @Test
  void getPermeabilitySummaryFromSubmissionDetail_whenEmptyPermeability() {
    var permeabilityDetail = new PermeabilityDetail();

    when(permeabilityPersistenceService.getPermeabilityDetail(submissionDetail))
        .thenReturn(Optional.of(permeabilityDetail));

    var view = permeabilitySummaryService.getPermeabilitySummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            PermeabilityDetailSummaryView::hasPermeability,
            PermeabilityDetailSummaryView::hasPermeabilityBol,
            PermeabilityDetailSummaryView::permeabilityMin,
            PermeabilityDetailSummaryView::permeabilityMax,
            PermeabilityDetailSummaryView::permeabilityAverage,
            PermeabilityDetailSummaryView::averageMethodUsed,
            PermeabilityDetailSummaryView::permeabilitySource,
            PermeabilityDetailSummaryView::otherPermeabilitySource,
            PermeabilityDetailSummaryView::permeabilityComments,
            PermeabilityDetailSummaryView::kvKhAverage)
        .containsOnly("", true);
  }

  @Test
  void getPermeabilitySummaryFromSubmissionDetail_whenGivenPermeabilityOtherSources() {
    var permeabilityDetail = getPermeabilityDetail();

    when(permeabilityPersistenceService.getPermeabilityDetail(submissionDetail))
        .thenReturn(Optional.of(permeabilityDetail));

    var view = permeabilitySummaryService.getPermeabilitySummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            PermeabilityDetailSummaryView::hasPermeability,
            PermeabilityDetailSummaryView::hasPermeabilityBol,
            PermeabilityDetailSummaryView::permeabilityMin,
            PermeabilityDetailSummaryView::permeabilityMax,
            PermeabilityDetailSummaryView::permeabilityAverage,
            PermeabilityDetailSummaryView::averageMethodUsed,
            PermeabilityDetailSummaryView::permeabilitySource,
            PermeabilityDetailSummaryView::otherPermeabilitySource,
            PermeabilityDetailSummaryView::permeabilityComments,
            PermeabilityDetailSummaryView::kvKhAverage)
        .containsExactly(
            "Yes",
            permeabilityDetail.getHasPermeability(),
            permeabilityDetail.getPermeabilityMin().toString() + " md",
            permeabilityDetail.getPermeabilityMax().toString() + " md",
            permeabilityDetail.getPermeabilityAverage().toString() + " md",
            permeabilityDetail.getAverageMethodUsed().getDisplayName(),
            permeabilityDetail.getPermeabilitySource().getDisplayName(),
            permeabilityDetail.getSourceOther(),
            permeabilityDetail.getPermeabilityComments(),
            permeabilityDetail.getKvKhAverage().toString() + " kv/kh"
        );
  }

  @Test
  void getPermeabilitySummaryFromSubmissionDetail_whenPermeability() {
    var permeabilityDetail = getPermeabilityDetail();

    permeabilityDetail.setAverageMethodUsed(AverageMethod.GEOMETRIC);
    permeabilityDetail.setPermeabilitySource(PermeabilitySource.OTHER);
    when(permeabilityPersistenceService.getPermeabilityDetail(submissionDetail))
        .thenReturn(Optional.of(permeabilityDetail));

    var view = permeabilitySummaryService.getPermeabilitySummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            PermeabilityDetailSummaryView::hasPermeability,
            PermeabilityDetailSummaryView::hasPermeabilityBol,
            PermeabilityDetailSummaryView::permeabilityMin,
            PermeabilityDetailSummaryView::permeabilityMax,
            PermeabilityDetailSummaryView::permeabilityAverage,
            PermeabilityDetailSummaryView::averageMethodUsed,
            PermeabilityDetailSummaryView::permeabilitySource,
            PermeabilityDetailSummaryView::otherPermeabilitySource,
            PermeabilityDetailSummaryView::permeabilityComments,
            PermeabilityDetailSummaryView::kvKhAverage)
        .containsExactly(
            "Yes",
            permeabilityDetail.getHasPermeability(),
            permeabilityDetail.getPermeabilityMin().toString() + " md",
            permeabilityDetail.getPermeabilityMax().toString() + " md",
            permeabilityDetail.getPermeabilityAverage().toString() + " md",
            permeabilityDetail.getAverageMethodUsed().getDisplayName(),
            permeabilityDetail.getPermeabilitySource().getDisplayName(),
            "comments",
            permeabilityDetail.getPermeabilityComments(),
            permeabilityDetail.getKvKhAverage().toString() + " kv/kh"
        );
  }

  public PermeabilityDetail getPermeabilityDetail() {
    var permeabilityDetail = new PermeabilityDetail();

    permeabilityDetail.setPermeabilitySource(PermeabilitySource.OTHER);
    permeabilityDetail.setPermeabilityAverage(BigDecimal.ONE);
    permeabilityDetail.setPermeabilityMin(BigDecimal.ONE);
    permeabilityDetail.setPermeabilityMax(BigDecimal.ONE);
    permeabilityDetail.setPermeabilityAverage(BigDecimal.ONE);
    permeabilityDetail.setPermeabilityComments("comments");
    permeabilityDetail.setKvKhAverage(BigDecimal.ONE);
    permeabilityDetail.setKvKhAverage(BigDecimal.ONE);
    permeabilityDetail.setAverageMethodUsed(AverageMethod.ARITHMETIC);
    permeabilityDetail.setHasPermeability(true);
    permeabilityDetail.setSourceOther("comments");

    return permeabilityDetail;
  }
}
package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ReservoirSummaryTestUtil.assertSummarySection;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;

@ExtendWith(MockitoExtension.class)
class PermeabilitySummaryServiceTest {

  @Mock
  private PermeabilityPersistenceService permeabilityPersistenceService;

  @InjectMocks
  private PermeabilitySummaryService permeabilitySummaryService;

  private static final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();

  @Test
  void getPermeabilitySummaryFromReservoirSubmissionDetail_whenNoPermeability() {
    when(permeabilityPersistenceService.getPermeabilityDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    var view = permeabilitySummaryService.getPermeabilitySummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

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
  void getPermeabilitySummaryFromReservoirSubmissionDetail_whenEmptyPermeability() {
    var permeabilityDetail = new PermeabilityDetail();

    when(permeabilityPersistenceService.getPermeabilityDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(permeabilityDetail));

    var view = permeabilitySummaryService.getPermeabilitySummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

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
  void getPermeabilitySummaryFromReservoirSubmissionDetail_whenGivenPermeabilityOtherSources() {
    var permeabilityDetail = getPermeabilityDetail();

    when(permeabilityPersistenceService.getPermeabilityDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(permeabilityDetail));

    var view = permeabilitySummaryService.getPermeabilitySummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

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
            permeabilityDetail.getPermeabilityMin().toString() + " mD",
            permeabilityDetail.getPermeabilityMax().toString() + " mD",
            permeabilityDetail.getPermeabilityAverage().toString() + " mD",
            permeabilityDetail.getAverageMethodUsed().getDisplayName(),
            permeabilityDetail.getPermeabilitySource().getDisplayName(),
            permeabilityDetail.getSourceOther(),
            permeabilityDetail.getPermeabilityComments(),
            permeabilityDetail.getKvKhAverage().toString() + " kv/kh"
        );
  }

  @Test
  void getPermeabilitySummaryFromReservoirSubmissionDetail_whenPermeability() {
    var permeabilityDetail = getPermeabilityDetail();

    permeabilityDetail.setAverageMethodUsed(AverageMethod.GEOMETRIC);
    permeabilityDetail.setPermeabilitySource(PermeabilitySource.OTHER);
    when(permeabilityPersistenceService.getPermeabilityDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(permeabilityDetail));

    var view = permeabilitySummaryService.getPermeabilitySummaryFromReservoirSubmissionDetail(reservoirSubmissionDetail);

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
            permeabilityDetail.getPermeabilityMin().toString() + " mD",
            permeabilityDetail.getPermeabilityMax().toString() + " mD",
            permeabilityDetail.getPermeabilityAverage().toString() + " mD",
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

  @Test
  void getSummarySection_emptyPermeabilityDetail() {
    when(permeabilityPersistenceService.getPermeabilityDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    var summarySection = permeabilitySummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(), PermeabilitySummaryService.PERMEABILITY_DISPLAY_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        PermeabilitySummaryService.PERMEABILITY_MIN, ""
    );
    summaryDataView.addKeyValue(PermeabilitySummaryService.PERMEABILITY_MAX, "");
    summaryDataView.addKeyValue(PermeabilitySummaryService.PERMEABILITY_AVERAGE, "");
    summaryDataView.addKeyValue(PermeabilitySummaryService.AVERAGE_METHOD_USED, "");
    summaryDataView.addKeyValue(PermeabilitySummaryService.PERMEABILITY_SOURCE, "");
    summaryDataView.addKeyValue(PermeabilitySummaryService.PERMEABILITY_COMMENTS, "");
    summaryDataView.addKeyValue(PermeabilitySummaryService.KV_KH_AVERAGE, "");

    var summaryCard = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem = SummaryItem.withCard(PermeabilitySummaryService.PERMEABILITY, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }

  @ParameterizedTest
  @EnumSource(
      value = PermeabilitySource.class,
      names = {"OTHER"},
      mode = EnumSource.Mode.EXCLUDE
  )
  void getSummarySection_nonEmptyPermeabilityDetail_withNonOtherSource(PermeabilitySource permeabilitySource) {
    var permeabilityDetail = new PermeabilityDetail();
    permeabilityDetail.setPermeabilityMin(BigDecimal.valueOf(10.0));
    permeabilityDetail.setPermeabilityMax(BigDecimal.valueOf(20.0));
    permeabilityDetail.setPermeabilityAverage(BigDecimal.valueOf(15.0));
    permeabilityDetail.setAverageMethodUsed(AverageMethod.ARITHMETIC);
    permeabilityDetail.setPermeabilitySource(permeabilitySource);
    permeabilityDetail.setPermeabilityComments("Test comments");
    permeabilityDetail.setKvKhAverage(BigDecimal.valueOf(5.0));

    when(permeabilityPersistenceService.getPermeabilityDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(permeabilityDetail));

    var summarySection = permeabilitySummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(), PermeabilitySummaryService.PERMEABILITY_DISPLAY_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        PermeabilitySummaryService.PERMEABILITY_MIN, "10.0 mD"
    );
    summaryDataView.addKeyValue(PermeabilitySummaryService.PERMEABILITY_MAX, "20.0 mD");
    summaryDataView.addKeyValue(PermeabilitySummaryService.PERMEABILITY_AVERAGE, "15.0 mD");
    summaryDataView.addKeyValue(PermeabilitySummaryService.AVERAGE_METHOD_USED, "Arithmetic");
    summaryDataView.addKeyValue(PermeabilitySummaryService.PERMEABILITY_SOURCE, permeabilitySource.getDisplayName());
    summaryDataView.addKeyValue(PermeabilitySummaryService.PERMEABILITY_COMMENTS, "Test comments");
    summaryDataView.addKeyValue(PermeabilitySummaryService.KV_KH_AVERAGE, "5.0 kv/kh");

    var summaryCard = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem = SummaryItem.withCard(PermeabilitySummaryService.PERMEABILITY, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }

  @Test
  void getSummarySection_nonEmptyPermeabilityDetail_witOtherSource() {
    var testSource = "testSource";

    var permeabilityDetail = new PermeabilityDetail();
    permeabilityDetail.setPermeabilityMin(BigDecimal.valueOf(10.0));
    permeabilityDetail.setPermeabilityMax(BigDecimal.valueOf(20.0));
    permeabilityDetail.setPermeabilityAverage(BigDecimal.valueOf(15.0));
    permeabilityDetail.setAverageMethodUsed(AverageMethod.ARITHMETIC);
    permeabilityDetail.setPermeabilitySource(PermeabilitySource.OTHER);
    permeabilityDetail.setSourceOther(testSource);
    permeabilityDetail.setPermeabilityComments("Test comments");
    permeabilityDetail.setKvKhAverage(BigDecimal.valueOf(5.0));

    when(permeabilityPersistenceService.getPermeabilityDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(permeabilityDetail));

    var summarySection = permeabilitySummaryService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(), PermeabilitySummaryService.PERMEABILITY_DISPLAY_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        PermeabilitySummaryService.PERMEABILITY_MIN, "10.0 mD"
    );
    summaryDataView.addKeyValue(PermeabilitySummaryService.PERMEABILITY_MAX, "20.0 mD");
    summaryDataView.addKeyValue(PermeabilitySummaryService.PERMEABILITY_AVERAGE, "15.0 mD");
    summaryDataView.addKeyValue(PermeabilitySummaryService.AVERAGE_METHOD_USED, "Arithmetic");
    summaryDataView.addKeyValue(PermeabilitySummaryService.PERMEABILITY_SOURCE, testSource);
    summaryDataView.addKeyValue(PermeabilitySummaryService.PERMEABILITY_COMMENTS, "Test comments");
    summaryDataView.addKeyValue(PermeabilitySummaryService.KV_KH_AVERAGE, "5.0 kv/kh");

    var summaryCard = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem = SummaryItem.withCard(PermeabilitySummaryService.PERMEABILITY, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }
}
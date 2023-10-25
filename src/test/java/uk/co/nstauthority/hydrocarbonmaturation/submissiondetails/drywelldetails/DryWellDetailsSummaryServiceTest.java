package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;

@ExtendWith(MockitoExtension.class)
class DryWellDetailsSummaryServiceTest {

  @Mock
  private DryWellDetailsPersistenceService dryWellDetailsPersistenceService;

  @InjectMocks
  private DryWellDetailsSummaryService dryWellDetailsSummaryService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
  }

  @Test
  void getDryWellDetailSummaryFromSubmissionDetail_whenNotPresent_emptySummary() {
    when(dryWellDetailsPersistenceService.getDryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var view = dryWellDetailsSummaryService.getDryWellDetailSummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            DryWellDetailsSummaryView::principalWell,
            DryWellDetailsSummaryView::latitudeDegrees,
            DryWellDetailsSummaryView::latitudeMinutes,
            DryWellDetailsSummaryView::latitudeSeconds,
            DryWellDetailsSummaryView::longitudeDegrees,
            DryWellDetailsSummaryView::longitudeMinutes,
            DryWellDetailsSummaryView::longitudeSeconds,
            DryWellDetailsSummaryView::longitudeHemisphere,
            DryWellDetailsSummaryView::datum,
            DryWellDetailsSummaryView::totalDepthFormation,
            DryWellDetailsSummaryView::totalDepthFormationComments,
            DryWellDetailsSummaryView::dataAcquisitionProgramme,
            DryWellDetailsSummaryView::geophysicalUpdate,
            DryWellDetailsSummaryView::dryHoleType,
            DryWellDetailsSummaryView::dryHoleDescription,
            DryWellDetailsSummaryView::dryHoleScope,
            DryWellDetailsSummaryView::dryHoleComments,
            DryWellDetailsSummaryView::dryHoleCost,
            DryWellDetailsSummaryView::migrationFailureCategory,
            DryWellDetailsSummaryView::reservoirFailureCategory,
            DryWellDetailsSummaryView::sourceFailureCategory,
            DryWellDetailsSummaryView::sealFailureCategory,
            DryWellDetailsSummaryView::trapFailureCategory)
        .containsOnly("");
  }

  @Test
  void getDryWellDetailSummaryFromSubmissionDetail_whenPresentButEmpty_emptySummary() {
    when(dryWellDetailsPersistenceService.getDryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(new DryWellDetail()));

    var view = dryWellDetailsSummaryService.getDryWellDetailSummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            DryWellDetailsSummaryView::principalWell,
            DryWellDetailsSummaryView::latitudeDegrees,
            DryWellDetailsSummaryView::latitudeMinutes,
            DryWellDetailsSummaryView::latitudeSeconds,
            DryWellDetailsSummaryView::longitudeDegrees,
            DryWellDetailsSummaryView::longitudeMinutes,
            DryWellDetailsSummaryView::longitudeSeconds,
            DryWellDetailsSummaryView::longitudeHemisphere,
            DryWellDetailsSummaryView::datum,
            DryWellDetailsSummaryView::totalDepthFormation,
            DryWellDetailsSummaryView::totalDepthFormationComments,
            DryWellDetailsSummaryView::dataAcquisitionProgramme,
            DryWellDetailsSummaryView::geophysicalUpdate,
            DryWellDetailsSummaryView::dryHoleType,
            DryWellDetailsSummaryView::dryHoleDescription,
            DryWellDetailsSummaryView::dryHoleScope,
            DryWellDetailsSummaryView::dryHoleComments,
            DryWellDetailsSummaryView::dryHoleCost,
            DryWellDetailsSummaryView::migrationFailureCategory,
            DryWellDetailsSummaryView::reservoirFailureCategory,
            DryWellDetailsSummaryView::sourceFailureCategory,
            DryWellDetailsSummaryView::sealFailureCategory,
            DryWellDetailsSummaryView::trapFailureCategory)
        .containsOnly("");
  }

  @Test
  void getDryWellDetailSummaryFromSubmissionDetail_whenPresent_fullSummary() {
    var dryWellDetail = new DryWellDetail();
    dryWellDetail.setWellReference("wellname");
    dryWellDetail.setWellId(1);
    dryWellDetail.setTopReservoirLatitudeDegrees(BigDecimal.ONE);
    dryWellDetail.setTopReservoirLatitudeMinutes(BigDecimal.ONE);
    dryWellDetail.setTopReservoirLatitudeSeconds(BigDecimal.ONE);
    dryWellDetail.setTopReservoirLongitudeDegrees(BigDecimal.ONE);
    dryWellDetail.setTopReservoirLongitudeMinutes(BigDecimal.ONE);
    dryWellDetail.setTopReservoirLongitudeSeconds(BigDecimal.ONE);
    dryWellDetail.setTopReservoirLongitudeHemisphere(Hemisphere.EAST);
    dryWellDetail.setTopReservoirDatum(ReservoirDatum.ED50);
    dryWellDetail.setTotalDepthFormation("depth");
    dryWellDetail.setTotalDepthFormationComments("depthC");
    dryWellDetail.setDataAcquisitionProgramme("data");
    dryWellDetail.setGeophysicalUpdateCommentary("geophysical");
    dryWellDetail.setDryHoleType(DryHoleType.GAS);
    dryWellDetail.setDryHoleDescription("description");
    dryWellDetail.setDryHoleScope("scope");
    dryWellDetail.setDryHoleComments("comments");
    dryWellDetail.setDryHoleCost(BigDecimal.ONE);
    dryWellDetail.setTrapFailureCategory(TrapFailureCategory.STRATIGRAPHIC_TRAP_COMPONENTS_NOT_PRESENT);
    dryWellDetail.setMigrationFailureCategory(MigrationFailureCategory.PROSPECT_NOT_ON_MIGRATION);
    dryWellDetail.setSourceFailureCategory(SourceFailureCategory.SOURCE_NOT_MATURE);
    dryWellDetail.setSealFailureCategory(SealFailureCategory.SEAL_LITHOLOGY_PRESENT_POOR_QUALITY);
    dryWellDetail.setReservoirFailureCategory(ReservoirFailureCategory.RESERVOIR_INTERVAL_NOT_PRESENT);

    when(dryWellDetailsPersistenceService.getDryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(dryWellDetail));

    var view = dryWellDetailsSummaryService.getDryWellDetailSummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            DryWellDetailsSummaryView::principalWell,
            DryWellDetailsSummaryView::latitudeDegrees,
            DryWellDetailsSummaryView::latitudeMinutes,
            DryWellDetailsSummaryView::latitudeSeconds,
            DryWellDetailsSummaryView::longitudeDegrees,
            DryWellDetailsSummaryView::longitudeMinutes,
            DryWellDetailsSummaryView::longitudeSeconds,
            DryWellDetailsSummaryView::longitudeHemisphere,
            DryWellDetailsSummaryView::datum,
            DryWellDetailsSummaryView::totalDepthFormation,
            DryWellDetailsSummaryView::totalDepthFormationComments,
            DryWellDetailsSummaryView::dataAcquisitionProgramme,
            DryWellDetailsSummaryView::geophysicalUpdate,
            DryWellDetailsSummaryView::dryHoleType,
            DryWellDetailsSummaryView::dryHoleDescription,
            DryWellDetailsSummaryView::dryHoleScope,
            DryWellDetailsSummaryView::dryHoleComments,
            DryWellDetailsSummaryView::dryHoleCost,
            DryWellDetailsSummaryView::migrationFailureCategory,
            DryWellDetailsSummaryView::reservoirFailureCategory,
            DryWellDetailsSummaryView::sourceFailureCategory,
            DryWellDetailsSummaryView::sealFailureCategory,
            DryWellDetailsSummaryView::trapFailureCategory)
        .containsExactly(
            "wellname",
            BigDecimal.ONE + "°",
            BigDecimal.ONE + "'",
            BigDecimal.ONE + "\"",
            BigDecimal.ONE + "°",
            BigDecimal.ONE + "'",
            BigDecimal.ONE + "\"",
            Hemisphere.EAST.getDisplayName(),
            ReservoirDatum.ED50.getDisplayName(),
            "depth",
            "depthC",
            "data",
            "geophysical",
            DryHoleType.GAS.getDisplayName(),
            "description",
            "scope",
            "comments",
            "£" + BigDecimal.ONE + " million",
            MigrationFailureCategory.PROSPECT_NOT_ON_MIGRATION.getDisplayName(),
            ReservoirFailureCategory.RESERVOIR_INTERVAL_NOT_PRESENT.getDisplayName(),
            SourceFailureCategory.SOURCE_NOT_MATURE.getDisplayName(),
            SealFailureCategory.SEAL_LITHOLOGY_PRESENT_POOR_QUALITY.getDisplayName(),
            TrapFailureCategory.STRATIGRAPHIC_TRAP_COMPONENTS_NOT_PRESENT.getDisplayName()
        );
  }
}
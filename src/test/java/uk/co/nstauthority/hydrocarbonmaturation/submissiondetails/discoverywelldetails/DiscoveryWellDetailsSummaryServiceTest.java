package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
class DiscoveryWellDetailsSummaryServiceTest {

  @Mock
  private DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  @InjectMocks
  private DiscoveryWellDetailsSummaryService discoveryWellDetailsSummaryService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  }

  @Test
  void getDiscoveryWellDetailSummaryFromSubmissionDetail_whenNotPresent_emptySummary() {
    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var view = discoveryWellDetailsSummaryService.getDiscoveryWellDetailSummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            DiscoveryWellDetailsSummaryView::principalWell,
            DiscoveryWellDetailsSummaryView::latitudeDegrees,
            DiscoveryWellDetailsSummaryView::latitudeMinutes,
            DiscoveryWellDetailsSummaryView::latitudeSeconds,
            DiscoveryWellDetailsSummaryView::longitudeDegrees,
            DiscoveryWellDetailsSummaryView::longitudeMinutes,
            DiscoveryWellDetailsSummaryView::longitudeSeconds,
            DiscoveryWellDetailsSummaryView::longitudeHemisphere,
            DiscoveryWellDetailsSummaryView::datum,
            DiscoveryWellDetailsSummaryView::totalDepthFormation,
            DiscoveryWellDetailsSummaryView::totalDepthFormationComments,
            DiscoveryWellDetailsSummaryView::dataAcquisitionProgramme,
            DiscoveryWellDetailsSummaryView::geophysicalUpdate,
            DiscoveryWellDetailsSummaryView::hydrocarbonsDiscovered,
            DiscoveryWellDetailsSummaryView::discoveryStatus,
            DiscoveryWellDetailsSummaryView::commentsRegardingCommerciality,
            DiscoveryWellDetailsSummaryView::totalWellCost,
            DiscoveryWellDetailsSummaryView::successRelatedCosts)
        .containsOnly("");
  }

  @Test
  void getDiscoveryWellDetailSummaryFromSubmissionDetail_whenPresent_fullSummary() {
    var discoveryWellDetail = new DiscoveryWellDetail();
    discoveryWellDetail.setWellReference("wellname");
    discoveryWellDetail.setWellId(1);
    discoveryWellDetail.setLatitudeDegrees(BigDecimal.ONE);
    discoveryWellDetail.setLatitudeMinutes(BigDecimal.ONE);
    discoveryWellDetail.setLatitudeSeconds(BigDecimal.ONE);
    discoveryWellDetail.setLongitudeDegrees(BigDecimal.ONE);
    discoveryWellDetail.setLongitudeMinutes(BigDecimal.ONE);
    discoveryWellDetail.setLongitudeSeconds(BigDecimal.ONE);
    discoveryWellDetail.setLongitudeHemisphere(Hemisphere.EAST);
    discoveryWellDetail.setDatum(ReservoirDatum.ED50);
    discoveryWellDetail.setTotalDepthFormation("depth");
    discoveryWellDetail.setTotalDepthFormationComments("depthC");
    discoveryWellDetail.setDataAcquisitionProgramme("data");
    discoveryWellDetail.setGeophysicalUpdateCommentary("geophysical");
    discoveryWellDetail.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
    discoveryWellDetail.setDiscoveryStatus(DiscoveryStatus.TECHNICAL_DISCOVERY);
    discoveryWellDetail.setChanceOfCommercialityComments("comments commerciality");
    discoveryWellDetail.setTotalWellCost(BigDecimal.ZERO);
    discoveryWellDetail.setSuccessRelatedCosts(BigDecimal.ONE);

    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(discoveryWellDetail));

    var view = discoveryWellDetailsSummaryService.getDiscoveryWellDetailSummaryFromSubmissionDetail(submissionDetail);

    assertThat(view)
        .extracting(
            DiscoveryWellDetailsSummaryView::principalWell,
            DiscoveryWellDetailsSummaryView::latitudeDegrees,
            DiscoveryWellDetailsSummaryView::latitudeMinutes,
            DiscoveryWellDetailsSummaryView::latitudeSeconds,
            DiscoveryWellDetailsSummaryView::longitudeDegrees,
            DiscoveryWellDetailsSummaryView::longitudeMinutes,
            DiscoveryWellDetailsSummaryView::longitudeSeconds,
            DiscoveryWellDetailsSummaryView::longitudeHemisphere,
            DiscoveryWellDetailsSummaryView::datum,
            DiscoveryWellDetailsSummaryView::totalDepthFormation,
            DiscoveryWellDetailsSummaryView::totalDepthFormationComments,
            DiscoveryWellDetailsSummaryView::dataAcquisitionProgramme,
            DiscoveryWellDetailsSummaryView::geophysicalUpdate,
            DiscoveryWellDetailsSummaryView::hydrocarbonsDiscovered,
            DiscoveryWellDetailsSummaryView::discoveryStatus,
            DiscoveryWellDetailsSummaryView::commentsRegardingCommerciality,
            DiscoveryWellDetailsSummaryView::totalWellCost,
            DiscoveryWellDetailsSummaryView::successRelatedCosts)
        .containsExactly(
            "wellname",
            BigDecimal.ONE + "°",
            BigDecimal.ONE + "''",
            BigDecimal.ONE + "'",
            BigDecimal.ONE + "°",
            BigDecimal.ONE + "''",
            BigDecimal.ONE + "'",
            Hemisphere.EAST.getDisplayName(),
            ReservoirDatum.ED50.getDisplayName(),
            "depth",
            "depthC",
            "data",
            "geophysical",
            HydrocarbonsDiscovered.GAS_ONLY.getDisplayName(),
            DiscoveryStatus.TECHNICAL_DISCOVERY.getDisplayName(),
            "comments commerciality",
            "£" + BigDecimal.ZERO + " million",
            "£" + BigDecimal.ONE + " million"
        );
  }
}
package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.Reservoir;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationSummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;

@ExtendWith(MockitoExtension.class)
class ReservoirSummaryServiceTest {

  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;

  @Mock
  private GeneralReservoirInformationSummarySectionService generalReservoirInformationSummarySectionService;

  private ReservoirSummaryService reservoirSummaryService;

  @BeforeEach
  void setUp() {
    reservoirSummaryService = new ReservoirSummaryService(
        List.of(generalReservoirInformationSummarySectionService),
        reservoirSubmissionDetailPersistenceService
    );
  }

  @Test
  void getReservoirSummaries() {
    var submissionDetail = SubmissionDetailTestUtils.createDiscoverySubmissionDetail(SubmissionType.CREATE_DISCOVERY);
    var reservoir1 = new Reservoir();
    var reservoirName1 = "Reservoir A";
    reservoir1.setName(reservoirName1);
    var reservoir2 = new Reservoir();
    var reservoirName2 = "Reservoir C";
    reservoir2.setName(reservoirName2);
    var reservoir3 = new Reservoir();
    var reservoirName3 = "Reservoir B";
    reservoir3.setName(reservoirName3);
    var reservoirSubmissionDetail1 = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        reservoir1,
        submissionDetail
    );
    var reservoirSubmissionDetail2 = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        reservoir2,
        submissionDetail
    );
    var reservoirSubmissionDetail3 = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(
        reservoir3,
        submissionDetail
    );
    when(reservoirSubmissionDetailPersistenceService.getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(List.of(reservoirSubmissionDetail1, reservoirSubmissionDetail2, reservoirSubmissionDetail3));
    var reservoirSubmissionDetailList = List.of(reservoirSubmissionDetail1, reservoirSubmissionDetail2, reservoirSubmissionDetail3);
    reservoirSubmissionDetailList.forEach(reservoirSubmissionDetail ->
        when(generalReservoirInformationSummarySectionService.getSummarySection(reservoirSubmissionDetail))
            .thenReturn(
                Optional.of(
                    new SummarySection(
                        GeneralReservoirInformationSummarySectionService.GENERAL_RESERVOIR_INFORMATION_DISPLAY_ORDER,
                    null,
                    ""
                    )
                )
            )
    );

    var reservoirSummaries = reservoirSummaryService.getReservoirSummaries(submissionDetail);
    var generalReservoirInfoSummarySection = new SummarySection(GeneralReservoirInformationSummarySectionService.GENERAL_RESERVOIR_INFORMATION_DISPLAY_ORDER, null, "");
    var reservoirSummary1 = new ReservoirSummary(reservoirName1, List.of(generalReservoirInfoSummarySection));
    var reservoirSummary2 = new ReservoirSummary(reservoirName3, List.of(generalReservoirInfoSummarySection));
    var reservoirSummary3 = new ReservoirSummary(reservoirName2, List.of(generalReservoirInfoSummarySection));
    assertThat(reservoirSummaries).containsExactly(reservoirSummary1, reservoirSummary2, reservoirSummary3);

  }
}

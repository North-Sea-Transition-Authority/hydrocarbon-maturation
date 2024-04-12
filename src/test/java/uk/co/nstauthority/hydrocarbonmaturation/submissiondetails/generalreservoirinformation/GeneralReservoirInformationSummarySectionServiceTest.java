package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ReservoirSummaryTestUtil.assertSummarySection;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;

@ExtendWith(MockitoExtension.class)
class GeneralReservoirInformationSummarySectionServiceTest {

  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;

  @InjectMocks
  private GeneralReservoirInformationSummarySectionService generalReservoirInformationSummarySectionService;

  @Test
  void getSummarySection_discovery() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    var reservoirHydrocarbon = new ReservoirHydrocarbons();
    reservoirHydrocarbon.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    reservoirHydrocarbon.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)).thenReturn(Optional.of(reservoirHydrocarbon));

    var summarySection = generalReservoirInformationSummarySectionService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(), GeneralReservoirInformationSummarySectionService.GENERAL_RESERVOIR_INFORMATION_DISPLAY_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        GeneralReservoirInformationSummarySectionService.HYDROCARBONS_DISCOVERED,
        HydrocarbonsDiscovered.GAS_ONLY.getDisplayName()
    );
    var summaryCard = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem = SummaryItem.withCard(GeneralReservoirInformationSummarySectionService.RESERVOIR_INFORMATION, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }

  @Test
  void getSummarySection_noHydrocarbons() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)).thenReturn(Optional.empty());

    var summarySection = generalReservoirInformationSummarySectionService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(), GeneralReservoirInformationSummarySectionService.GENERAL_RESERVOIR_INFORMATION_DISPLAY_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(GeneralReservoirInformationSummarySectionService.HYDROCARBONS_DISCOVERED, null);
    var summaryCard = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem = SummaryItem.withCard(GeneralReservoirInformationSummarySectionService.RESERVOIR_INFORMATION, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }

  @Test
  void getSummarySection_noHydrocarbonsDiscoveredType() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(null);

    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)).thenReturn(Optional.of(reservoirHydrocarbons));

    var summarySection = generalReservoirInformationSummarySectionService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(), GeneralReservoirInformationSummarySectionService.GENERAL_RESERVOIR_INFORMATION_DISPLAY_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(GeneralReservoirInformationSummarySectionService.HYDROCARBONS_DISCOVERED, null);
    var summaryCard = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem = SummaryItem.withCard(GeneralReservoirInformationSummarySectionService.RESERVOIR_INFORMATION, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }

  @Test
  void getSummarySection_field() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(SubmissionType.CREATE_CSR);
    var reservoirHydrocarbon = new ReservoirHydrocarbons();
    reservoirHydrocarbon.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    reservoirHydrocarbon.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
    var reservoirName1 = "Res 1";
    var reservoirView1 = new ReservoirView(UUID.randomUUID(), reservoirName1);
    var reservoirName2 = "Res 2";
    var reservoirView2 = new ReservoirView(UUID.randomUUID(), reservoirName2);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)).thenReturn(Optional.of(reservoirHydrocarbon));
    when(generalReservoirInformationService.getLinkedDiscoveryReservoirViewsForField(reservoirSubmissionDetail))
        .thenReturn(List.of(reservoirView1, reservoirView2));

    var summarySection = generalReservoirInformationSummarySectionService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(), GeneralReservoirInformationSummarySectionService.GENERAL_RESERVOIR_INFORMATION_DISPLAY_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        GeneralReservoirInformationSummarySectionService.HYDROCARBONS_DISCOVERED,
        HydrocarbonsDiscovered.GAS_ONLY.getDisplayName()
    );
    summaryDataView.addKeyValue(GeneralReservoirInformationSummarySectionService.DISCOVERY_RESERVOIRS, String.join(", ", List.of(reservoirName1, reservoirName2)));
    var summaryCard = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem = SummaryItem.withCard(GeneralReservoirInformationSummarySectionService.RESERVOIR_INFORMATION, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }

  @Test
  void getSummarySection_field_noLinkedDiscoveries() {
    var reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(SubmissionType.CREATE_CSR);
    var reservoirHydrocarbon = new ReservoirHydrocarbons();
    reservoirHydrocarbon.setReservoirSubmissionDetail(reservoirSubmissionDetail);
    reservoirHydrocarbon.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
    when(generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail)).thenReturn(Optional.of(reservoirHydrocarbon));
    when(generalReservoirInformationService.getLinkedDiscoveryReservoirViewsForField(reservoirSubmissionDetail))
        .thenReturn(Collections.emptyList());

    var summarySection = generalReservoirInformationSummarySectionService.getSummarySection(reservoirSubmissionDetail);

    assertThat(summarySection).isPresent();
    assertSummarySection(summarySection.get(), GeneralReservoirInformationSummarySectionService.GENERAL_RESERVOIR_INFORMATION_DISPLAY_ORDER);
    var summaryDataView = SummaryDataView.newWithKeyValue(
        GeneralReservoirInformationSummarySectionService.HYDROCARBONS_DISCOVERED,
        HydrocarbonsDiscovered.GAS_ONLY.getDisplayName()
    );
    summaryDataView.addKeyValue(GeneralReservoirInformationSummarySectionService.DISCOVERY_RESERVOIRS, "");
    var summaryCard = SummaryCard.simpleSummaryCard(summaryDataView);
    var summaryItem = SummaryItem.withCard(GeneralReservoirInformationSummarySectionService.RESERVOIR_INFORMATION, summaryCard);
    assertThat(summarySection.get().summaryItems()).containsExactly(summaryItem);
  }
}

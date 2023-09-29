package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;

@Service
public class GeneralReservoirInformationSummarySectionService implements SummarySectionService<ReservoirSubmissionDetail> {

  public static final String RESERVOIR_INFORMATION = "Reservoir information";
  public static final String HYDROCARBONS_DISCOVERED = "Hydrocarbons discovered";
  public static final String DISCOVERY_RESERVOIRS = "Discovery reservoirs";

  private final GeneralReservoirInformationService generalReservoirInformationService;

  @Autowired
  public GeneralReservoirInformationSummarySectionService(
      GeneralReservoirInformationService generalReservoirInformationService) {
    this.generalReservoirInformationService = generalReservoirInformationService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var summaryItems = List.of(getGeneralInformationSummary(reservoirSubmissionDetail));
    return Optional.of(new SummarySection(10, summaryItems, ""));
  }

  private SummaryItem getGeneralInformationSummary(ReservoirSubmissionDetail reservoirSubmissionDetail) {
    var hydrocarbonsDiscovered = generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail);
    String hydrocarbonDisplayName;
    if (hydrocarbonsDiscovered.isPresent()) {
      hydrocarbonDisplayName = hydrocarbonsDiscovered.get().getHydrocarbonsDiscovered().getDisplayName();
    } else {
      hydrocarbonDisplayName = "";
    }

    var summaryDataView = SummaryDataView.newWithKeyValue(HYDROCARBONS_DISCOVERED, hydrocarbonDisplayName);

    if (reservoirSubmissionDetail.getSubmissionDetail().isFieldSubmission()) {
      var linkedDiscoveryReservoirs = generalReservoirInformationService.getLinkedDiscoveryReservoirViewsForField(
          reservoirSubmissionDetail
      );

      if (linkedDiscoveryReservoirs.isEmpty()) {
        summaryDataView.addKeyValue(DISCOVERY_RESERVOIRS, "");
      } else {
        var linkedDiscoveryReservoirsDisplay = linkedDiscoveryReservoirs.stream()
            .map(ReservoirView::getName)
            .toList();
        var linkedDiscoveryReservoirsSummary = String.join(", ", linkedDiscoveryReservoirsDisplay);
        summaryDataView.addKeyValue(DISCOVERY_RESERVOIRS, linkedDiscoveryReservoirsSummary);
      }
    }

    var summaryCard = SummaryCard.simpleSummaryCard(
        summaryDataView
    );
    return SummaryItem.withCard(RESERVOIR_INFORMATION, summaryCard);
  }
}

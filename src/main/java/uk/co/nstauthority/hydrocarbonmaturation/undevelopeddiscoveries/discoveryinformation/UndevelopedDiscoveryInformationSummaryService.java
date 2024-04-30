package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCrossTabView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscovery;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.DecimalFormatUtil;

@Service
public class UndevelopedDiscoveryInformationSummaryService implements SummarySectionService<UndevelopedDiscovery> {

  private static final int DISPLAY_ORDER = 20;
  static final String DISCOVERY_VOLUMETRICS = "Discovery volumetrics";
  static final String JUSTIFICATION_KEY = "Reasons why volumetrics are being provided where they differ from " +
      "the current operator provided figures";
  static final String JUSTIFICATION_HEADING = "Volumetrics difference reason";
  static final List<String> CROSS_TAB_HEADINGS = List.of("P50", "1C", "2C", "3C", "Units");
  private final UndevelopedDiscoveryInformationService informationService;

  @Autowired
  UndevelopedDiscoveryInformationSummaryService(UndevelopedDiscoveryInformationService informationService) {
    this.informationService = informationService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(UndevelopedDiscovery undevelopedDiscovery) {
    var undevelopedDiscoveryInformationList = informationService.getOrderedDiscoveryInformation(undevelopedDiscovery);

    var summaryItems = undevelopedDiscoveryInformationList.stream()
        .map(this::getSummaryItem)
        .toList();

    var summarySection = new SummarySection(DISPLAY_ORDER, summaryItems, "");
    return Optional.of(summarySection);
  }

  private SummaryItem getSummaryItem(UndevelopedDiscoveryInformation undevelopedDiscoveryInformation) {
    var volumetricCard = getVolumetricsCard(undevelopedDiscoveryInformation);
    var justificationCard = getJustificationCard(undevelopedDiscoveryInformation);
    var cards = List.of(volumetricCard, justificationCard);

    var prettyDate = DateUtil.formatLongDate(undevelopedDiscoveryInformation.getInformationDate());
    return SummaryItem.withCards("Discovery information for %s".formatted(prettyDate), cards);
  }

  private SummaryCard getVolumetricsCard(UndevelopedDiscoveryInformation undevelopedDiscoveryInformation) {
    var rows = entityToRows(undevelopedDiscoveryInformation);
    var crossTabView = new SummaryCrossTabView(CROSS_TAB_HEADINGS, rows);
    return SummaryCard.crossTabSummaryCard(DISCOVERY_VOLUMETRICS, crossTabView);
  }

  private SummaryCard getJustificationCard(UndevelopedDiscoveryInformation undevelopedDiscoveryInformation) {
    var summaryData = SummaryDataView.newWithKeyValue(
        JUSTIFICATION_KEY,
        undevelopedDiscoveryInformation.getDifferenceReason()
    );
    return SummaryCard.simpleSummaryCardWithHeading(JUSTIFICATION_HEADING, summaryData);
  }

  private List<UndevelopedDiscoveryInformationVolumetricsRow> entityToRows(
      UndevelopedDiscoveryInformation undevelopedDiscoveryInformation) {
    return List.of(
        new UndevelopedDiscoveryInformationVolumetricsRow(
            "Oil in place",
            DecimalFormatUtil.bigDecimalToFormattedString(undevelopedDiscoveryInformation.getOilInPlaceP50()),
            "",
            "",
            "",
            "mmbbl"
        ),
        new UndevelopedDiscoveryInformationVolumetricsRow(
            "Oil recoverable resources",
            "",
            DecimalFormatUtil.bigDecimalToFormattedString(undevelopedDiscoveryInformation.getOilRecoverableOneC()),
            DecimalFormatUtil.bigDecimalToFormattedString(undevelopedDiscoveryInformation.getOilRecoverableTwoC()),
            DecimalFormatUtil.bigDecimalToFormattedString(undevelopedDiscoveryInformation.getOilRecoverableThreeC()),
            "mmbbl"
        ),
        new UndevelopedDiscoveryInformationVolumetricsRow(
            "Gas in place",
            DecimalFormatUtil.bigDecimalToFormattedString(undevelopedDiscoveryInformation.getGasInPlaceP50()),
            "",
            "",
            "",
            "bcf"
        ),
        new UndevelopedDiscoveryInformationVolumetricsRow(
            "Gas recoverable resources",
            "",
            DecimalFormatUtil.bigDecimalToFormattedString(undevelopedDiscoveryInformation.getGasRecoverableOneC()),
            DecimalFormatUtil.bigDecimalToFormattedString(undevelopedDiscoveryInformation.getGasRecoverableTwoC()),
            DecimalFormatUtil.bigDecimalToFormattedString(undevelopedDiscoveryInformation.getGasRecoverableThreeC()),
            "bcf"
        )
    );
  }
}

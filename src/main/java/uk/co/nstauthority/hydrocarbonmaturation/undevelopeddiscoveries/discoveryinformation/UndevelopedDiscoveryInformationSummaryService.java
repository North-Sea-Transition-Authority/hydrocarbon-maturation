package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.complexity.ComplexityUtil;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryAction;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCrossTabView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscovery;
import uk.co.nstauthority.hydrocarbonmaturation.util.DateUtil;

@Service
public class UndevelopedDiscoveryInformationSummaryService implements SummarySectionService<UndevelopedDiscovery> {

  private static final int DISPLAY_ORDER = 20;
  static final String DISCOVERY_VOLUMETRICS = "Undeveloped discovery volumetrics";
  static final String VOLUMETRIC_DIFFERENCE_KEY = "Reasons why volumetrics are being provided where they differ from " +
      "the current operator provided figures";
  static final String VOLUMETRICS_DIFFERENCE_HEADING = "Volumetrics difference reason";
  static final String DISCOVERY_COMPLEXITY = "Undeveloped discovery complexity";
  static final List<String> CROSS_TAB_HEADINGS = List.of("P50", "1C", "2C", "3C", "Units");
  private final UndevelopedDiscoveryInformationService informationService;

  @Autowired
  UndevelopedDiscoveryInformationSummaryService(UndevelopedDiscoveryInformationService informationService) {
    this.informationService = informationService;
  }

  @Override
  public Optional<SummarySection> getSummarySection(UndevelopedDiscovery undevelopedDiscovery, User user) {
    var summaryItems = informationService.getOrderedDiscoveryInformation(undevelopedDiscovery)
        .stream()
        .map(this::getSummaryItem)
        .toList();

    var summarySection = new SummarySection(DISPLAY_ORDER, summaryItems, "");
    return Optional.of(summarySection);
  }

  public SummaryItem getSummaryItem(UndevelopedDiscoveryInformation undevelopedDiscoveryInformation) {
    var actions = getActionsForCard(undevelopedDiscoveryInformation);
    return getSummaryItem(undevelopedDiscoveryInformation, actions);
  }

  public SummaryItem getSummaryItem(UndevelopedDiscoveryInformation undevelopedDiscoveryInformation,
                                    List<SummaryAction> actions) {
    var volumetricCard = getVolumetricsCard(undevelopedDiscoveryInformation);
    var justificationCard = getJustificationCard(undevelopedDiscoveryInformation);
    var complexityCard = getComplexityCard(undevelopedDiscoveryInformation);

    var cards = List.of(volumetricCard, justificationCard, complexityCard);

    var prettyDate = DateUtil.formatLongDate(undevelopedDiscoveryInformation.getInformationDate());
    return SummaryItem.withCardsAndActions("Discovery information for %s".formatted(prettyDate), cards, actions);
  }

  private SummaryCard getVolumetricsCard(UndevelopedDiscoveryInformation undevelopedDiscoveryInformation) {
    var crossTabView = new SummaryCrossTabView(CROSS_TAB_HEADINGS);
    crossTabView = addRows(crossTabView, undevelopedDiscoveryInformation);
    return SummaryCard.crossTabSummaryCard(DISCOVERY_VOLUMETRICS, crossTabView);
  }

  private SummaryCard getJustificationCard(UndevelopedDiscoveryInformation undevelopedDiscoveryInformation) {
    var summaryData = SummaryDataView.newWithKeyValue(
        VOLUMETRIC_DIFFERENCE_KEY,
        undevelopedDiscoveryInformation.getDifferenceReason()
    );

    return new SummaryCard(
        VOLUMETRICS_DIFFERENCE_HEADING,
        SummaryCardType.SIMPLE_SUMMARY,
        summaryData
    );
  }

  private SummaryCard getComplexityCard(UndevelopedDiscoveryInformation information) {
    var summaryData = ComplexityUtil.getComplexitySummaryDataView(
        information.getViscosityRange(),
        information.getVerticalHeterogeneityRange(),
        information.getArealDensityRange(),
        information.getStructuralComplexityRange()
    );

    return new SummaryCard(
        DISCOVERY_COMPLEXITY,
        SummaryCardType.SIMPLE_SUMMARY,
        summaryData
    );
  }

  private List<SummaryAction> getActionsForCard(UndevelopedDiscoveryInformation undevelopedDiscoveryInformation) {
    var resourceId = undevelopedDiscoveryInformation.getUndevelopedDiscovery().getResource().getId();
    var informationId = undevelopedDiscoveryInformation.getId();

    return List.of(
        new SummaryAction(
            "Update undeveloped discovery information",
            ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                .getExistingUndevelopedDiscoveryInformation(resourceId, informationId)),
            "Update undeveloped discovery information"
        ),
        new SummaryAction(
            "Remove undeveloped discovery information",
            ReverseRouter.route(on(UndevelopedDiscoveryRemoveInformationController.class)
                .getRemoveInformationConfirmation(resourceId, informationId)),
            "Remove undeveloped discovery information"
        )
    );
  }

  private SummaryCrossTabView addRows(SummaryCrossTabView crossTabView,
                                      UndevelopedDiscoveryInformation undevelopedDiscoveryInformation) {
    return crossTabView
        .addRow(
            "Oil initially in place",
            undevelopedDiscoveryInformation.getOilInPlaceP50(), null, null, null, "mmbbl"
        )
        .addRow(
            "Oil recoverable resources",
            null, undevelopedDiscoveryInformation.getOilRecoverableOneC(),
            undevelopedDiscoveryInformation.getOilRecoverableTwoC(),
            undevelopedDiscoveryInformation.getOilRecoverableThreeC(), "mmbbl"
        )
        .addRow(
            "Gas initially in place",
            undevelopedDiscoveryInformation.getGasInPlaceP50(), null, null, null, "bcf"
        )
        .addRow(
            "Gas recoverable resources",
            null, undevelopedDiscoveryInformation.getGasRecoverableOneC(),
            undevelopedDiscoveryInformation.getGasRecoverableTwoC(),
            undevelopedDiscoveryInformation.getGasRecoverableThreeC(), "bcf"
        );
  }
}

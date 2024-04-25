package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryAction;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;

@Service
public class UndevelopedDiscoveryTrackingDetailsSummaryService implements SummarySectionService<UndevelopedDiscovery> {

  private static final int DISPLAY_ORDER = 10;
  private static final String TRACKING_DETAILS = "Tracking details";
  private static final String INCLUDED_IN_DISCOVERY = "Discovery included in the undeveloped discoveries tracker?";

  @Override
  public Optional<SummarySection> getSummarySection(UndevelopedDiscovery undevelopedDiscovery) {
    var trackingDetailsSummaryCard = getTrackingDetailsSummaryCard(undevelopedDiscovery);
    var summaryItems = List.of(SummaryItem.withCard(TRACKING_DETAILS, trackingDetailsSummaryCard));
    return Optional.of(new SummarySection(DISPLAY_ORDER, summaryItems, ""));
  }

  private SummaryCard getTrackingDetailsSummaryCard(UndevelopedDiscovery undevelopedDiscovery) {
    var includedInDiscoveryValue = BooleanUtil.yesNoFromBoolean(undevelopedDiscovery.getIsUndevelopedDiscovery(), "");

    var summaryData = new SummaryDataView(new ArrayList<>());
    summaryData.addKeyValue(INCLUDED_IN_DISCOVERY, includedInDiscoveryValue);

    var actions = new ArrayList<SummaryAction>();
    actions.add(
        new SummaryAction(
            "Change",
            ReverseRouter.route(on(UndevelopedDiscoveryTrackingDetailsController.class)
                .getUndevelopedDiscoveryTrackingDetails(undevelopedDiscovery.getResource().getId())),
            "Change tracking details"
        )
    );

    return new SummaryCard(
        TRACKING_DETAILS,
        SummaryCardType.SIMPLE_SUMMARY,
        summaryData,
        actions
    );
  }
}

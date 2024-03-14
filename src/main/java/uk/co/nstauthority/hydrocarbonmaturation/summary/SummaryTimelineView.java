package uk.co.nstauthority.hydrocarbonmaturation.summary;

import java.util.List;
import java.util.Map;

public record SummaryTimelineView(Map<String, String> cardActionsMap, List<TimelineEvent> timelineEvents) {
}

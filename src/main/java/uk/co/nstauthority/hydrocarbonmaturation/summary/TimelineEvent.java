package uk.co.nstauthority.hydrocarbonmaturation.summary;

import java.util.Map;

public record TimelineEvent(String heading, String hint, Map<String, String> eventActionsMap) {
}

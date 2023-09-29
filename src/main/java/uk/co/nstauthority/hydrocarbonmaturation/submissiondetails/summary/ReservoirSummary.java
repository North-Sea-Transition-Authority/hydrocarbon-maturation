package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;

public record ReservoirSummary(String reservoirName, List<SummarySection> summarySections) {
}

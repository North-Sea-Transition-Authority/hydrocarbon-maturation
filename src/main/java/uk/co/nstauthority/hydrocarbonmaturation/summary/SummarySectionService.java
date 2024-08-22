package uk.co.nstauthority.hydrocarbonmaturation.summary;

import java.util.Optional;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;

public interface SummarySectionService<T> {
  Optional<SummarySection> getSummarySection(T source, User user);
}

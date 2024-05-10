package uk.co.nstauthority.hydrocarbonmaturation.summary;

import java.util.List;

public record SummaryTableValue(String value, String link, List<Tag> tags) {
  public SummaryTableValue(Object value, String link, List<Tag> tags) {
    this(SummaryUtil.format(value), link, tags);
  }

  public SummaryTableValue(Object value) {
    this(SummaryUtil.format(value), null, List.of());
  }
}

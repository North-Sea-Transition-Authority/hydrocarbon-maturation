package uk.co.nstauthority.hydrocarbonmaturation.summary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record SummaryTableRow(
    List<String> rowValues,
    List<SummaryAction> rowActions
) {
  public SummaryTableRow(List<String> rowValues) {
    this(rowValues, List.of());
  }

  public int valueCount() {
    return this.rowValues.size();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    List<String> rowValues = new ArrayList<>();
    List<SummaryAction> rowActions = new ArrayList<>();

    private Builder() {
    }

    public Builder withValue(Object value) {
      return withValues(value);
    }

    public Builder withValues(Object... values) {
      this.rowValues.addAll(Arrays.stream(values).map(SummaryUtil::format).toList());
      return this;
    }

    public Builder withAction(String label, String url) {
      return withActions(Collections.singletonList(new SummaryAction(label, url, "")));
    }

    public Builder withAction(String label, String url, String screenReaderText) {
      return withActions(Collections.singletonList(new SummaryAction(label, url, screenReaderText)));
    }

    public Builder withActions(List<SummaryAction> actions) {
      this.rowActions.addAll(actions);
      return this;
    }

    public SummaryTableRow build() {
      return new SummaryTableRow(rowValues, rowActions);
    }
  }
}
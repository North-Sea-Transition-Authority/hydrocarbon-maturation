package uk.co.nstauthority.hydrocarbonmaturation.summary;

import java.util.List;

public record SummaryTableRow(
    List<String> rowValues
) {
  public int valueCount() {
    return this.rowValues.size();
  }
}
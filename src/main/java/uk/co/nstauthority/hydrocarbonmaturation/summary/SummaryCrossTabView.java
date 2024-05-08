package uk.co.nstauthority.hydrocarbonmaturation.summary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record SummaryCrossTabView(
    List<String> headings,
    List<CrossTabRow> rows
) {
  public SummaryCrossTabView {
    rows.forEach(row -> valueCountCheck(row.valueCount(), headings.size()));
  }

  public SummaryCrossTabView(List<String> headings) {
    this(headings, new ArrayList<>());
  }

  public SummaryCrossTabView addRow(Object key, Object... rowValues) {
    valueCountCheck(rowValues.length);

    var formattedKey = SummaryUtil.format(key);
    var formattedRowValueStrings = Arrays.stream(rowValues).map(SummaryUtil::format).toList();
    rows.add(new CrossTabRow(formattedKey, formattedRowValueStrings));

    return this;
  }

  private void valueCountCheck(int numberOfValuesToAdd, int numHeadings) {
    if (numHeadings != numberOfValuesToAdd) {
      throw new SummaryException("Error adding to cross tab summary, table has %s headings but row found with %s values"
          .formatted(numHeadings, numberOfValuesToAdd));
    }
  }

  private void valueCountCheck(int numberOfValuesToAdd) {
    valueCountCheck(numberOfValuesToAdd, headings.size());
  }
}
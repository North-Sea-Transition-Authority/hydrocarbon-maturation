package uk.co.nstauthority.hydrocarbonmaturation.summary;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record SummaryTableView(
    List<SummaryTableRow> tableRows,
    TableHeadingStyle tableHeadingStyle,
    String actionHeading,
    Integer defaultSortIndex,
    Integer defaultSortDirection
) {
  private static final Logger LOGGER = LoggerFactory.getLogger(SummaryTableView.class);

  public SummaryTableView(List<SummaryTableRow> tableRows) {
    this(tableRows, TableHeadingStyle.COLUMN, "Actions", null, 0);
  }

  public static SummaryTableView newWithHeading(Object... rowValues) {
    var summaryTableView = new SummaryTableView(new ArrayList<>());
    return summaryTableView.addRow(rowValues);
  }

  public SummaryTableView addRow(Object... rowValues) {
    addRow(this.tableRows, Arrays.asList(rowValues));
    return this;
  }

  public void addRow(SummaryTableRow summaryTableRow) {
    tableRows.add(summaryTableRow);
  }

  public String toString() {
    try {
      return new ObjectMapper().writeValueAsString(this);
    } catch (JsonProcessingException e) {
      LOGGER.error("""
              Failed to serialize SummaryTableView.
              Table rows {}.
              Action heading {}.
              Default sort direction {}.
              Default sort index {}.
              Table heading style {}.""",
          this.tableRows, this.actionHeading, this.defaultSortDirection, this.defaultSortIndex, this.tableHeadingStyle
      );
      return "";
    }
  }

  public static SummaryTableViewBuilder sortableTableBuilder() {
    return new SummaryTableViewBuilder();
  }

  public static class SummaryTableViewBuilder {
    private final List<SummaryTableRow> tableRows = new ArrayList<>();
    private TableHeadingStyle tableHeadingStyle = TableHeadingStyle.COLUMN;
    private String actionHeading = "";
    private Integer defaultSortIndex = null;
    private Integer defaultSortDirection = 0;

    public SummaryTableViewBuilder newWithHeadings(Object... headingValues) {
      addRow(headingValues);
      return this;
    }

    public SummaryTableViewBuilder newWithHeadings(List<?> headingValues) {
      addRow(headingValues);
      return this;
    }

    public SummaryTableViewBuilder addRow(Object... rowValues) {
      SummaryTableView.addRow(this.tableRows, Arrays.asList(rowValues));
      return this;
    }

    public SummaryTableViewBuilder addRow(List<?> rowValues) {
      SummaryTableView.addRow(this.tableRows, rowValues);
      return this;
    }

    public SummaryTableViewBuilder withHeadingStyle(TableHeadingStyle tableHeadingStyle) {
      this.tableHeadingStyle = tableHeadingStyle;
      return this;
    }

    public SummaryTableViewBuilder withActionHeading(String actionHeading) {
      this.actionHeading = actionHeading;
      return this;
    }

    public SummaryTableViewBuilder withDefaultSortIndex(Integer defaultSortIndex) {
      this.defaultSortIndex = defaultSortIndex;
      return this;
    }

    public SummaryTableViewBuilder withDefaultSortDirection(SummaryTableSortDirection defaultSortDirection) {
      this.defaultSortDirection = defaultSortDirection.getFrontendSortValue();
      return this;
    }

    public SummaryTableView build() {
      return new SummaryTableView(tableRows, tableHeadingStyle, actionHeading, defaultSortIndex, defaultSortDirection);
    }
  }

  private static void valueCountCheck(Integer expectedValueCount, List<SummaryTableRow> tableRows) {
    var allRowsHaveEqualValueCount = tableRows.stream()
        .allMatch(summaryTableRow -> summaryTableRow.valueCount() == expectedValueCount);

    if (!allRowsHaveEqualValueCount) {
      throw new SummaryException("Error adding to summary table, " +
          "all table rows must have the same number of values, expected value count %s"
              .formatted(expectedValueCount));
    }
  }

  static void addRow(List<SummaryTableRow> rows, List<?> rowValues) {
    valueCountCheck(rowValues.size(), rows);
    var formattedRowValueStrings = rowValues
        .stream()
        .map(SummaryUtil::formatForSummaryTableValue)
        .toList();
    rows.add(new SummaryTableRow(formattedRowValueStrings));
  }
}

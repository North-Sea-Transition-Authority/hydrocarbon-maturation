<#include '../layout/layout.ftl'>
<#macro tableSummary summaryTableView>
    <#assign hasRowHeadings = summaryTableView.tableHeadingStyle() == "ROW"
    || summaryTableView.tableHeadingStyle() == "ROW_AND_COLUMN" />
    <#assign hasColumnHeadings = summaryTableView.tableHeadingStyle() == "COLUMN"
    || summaryTableView.tableHeadingStyle() == "ROW_AND_COLUMN" />

    <table class="govuk-table">
        <thead class="govuk-table__head">
        <tr class="govuk-table__row">
            <!-- the first row contains the headers -->
            <#list summaryTableView.tableRows()[0].rowValues() as rowValue>
                <#if hasRowHeadings>
                    <th class="govuk-table__header">${rowValue!""}</th>
                <#else>
                    <td class="govuk-table__cell">${rowValue!""}</td>
                </#if>
            </#list>
        </tr>
        </thead>
        <tbody class="govuk-table__body">
        <#list summaryTableView.tableRows() as tableRow>
            <!-- ignore the first row as this contains the headers -->
            <#if tableRow?index != 0>
                <tr class="govuk-table__row">
                    <#list tableRow.rowValues() as rowValue>
                        <#if rowValue?index == 0 && hasColumnHeadings>
                            <th class="govuk-table__header">${rowValue!""}</th>
                        <#else>
                            <td class="govuk-table__cell">${rowValue!""}</td>
                        </#if>
                    </#list>
                    <#if tableRow.rowActions()?has_content>
                        <td class="govuk-table__cell">
                            <ol class="govuk-list govuk-!-margin-bottom-0">
                                <#list tableRow.rowActions() as rowAction>
                                    <li class="govuk-list__item">
                                        <@fdsAction.link linkText=rowAction.label() linkUrl=springUrl(rowAction.url()) linkScreenReaderText=rowAction.screenReaderText()/>
                                    </li>
                                </#list>
                            </ol>
                        </td>
                    </#if>
                </tr>
            </#if>
        </#list>
        </tbody>
    </table>
</#macro>

<#macro tableFixedWidthSummary summaryTableView>
  <table class="govuk-table">
    <thead class="govuk-table__head">
    <tr class="govuk-table__row">
      <!-- the first row contains the headers -->
      <#list summaryTableView.tableRows()[0].rowValues() as rowValue>
        <th class="govuk-table__header govuk-!-width-one-half">${rowValue!""}</th>
      </#list>
    </tr>
    </thead>
    <tbody class="govuk-table__body">
    <#list summaryTableView.tableRows() as tableRow>
      <!-- ignore the first row as this contains the headers -->
      <#if tableRow?index != 0>
        <tr class="govuk-table__row">
          <#list tableRow.rowValues() as rowValue>
            <#if rowValue?index == 0>
              <th scope="row" class="govuk-table__header govuk-!-width-one-half">${rowValue!""}</th>
            <#else>
              <td class="govuk-table__cell">${rowValue!""}</td>
            </#if>
          </#list>
        </tr>
      </#if>
    </#list>
    </tbody>
  </table>
</#macro>
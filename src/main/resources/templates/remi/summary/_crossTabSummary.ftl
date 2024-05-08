<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="crossTabView" type="uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCrossTabView" -->

<#macro crossTabSummary crossTabView>
  <table class="govuk-table remi-table">
    <thead class="govuk-table__head">
    <tr class="govuk-table__row">
      <th scope="col" class="govuk-table__header"></th>
      <#list crossTabView.headings() as heading>
        <th scope="col" class="govuk-table__header">${heading}</th>
      </#list>
    </tr>
    </thead>
    <tbody class="govuk-table__body">
      <#list crossTabView.rows() as row>
        <tr class="govuk-table__row">
          <th scope="row" class="govuk-table__header">
            ${row.key()}
          </th>
          <#list row.values() as value>
            <td class="govuk-table__cell">${value!""}</td>
          </#list>
        </tr>
      </#list>
    </tbody>
  </table>
</#macro>
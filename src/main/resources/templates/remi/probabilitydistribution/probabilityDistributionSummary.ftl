<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="probabilityDistributions" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView>" -->
<#-- @ftlvariable name="probabilityDistribution" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView" -->

<#macro probabilityDistributionTable
  probabilityDistributions = []
  withDistributionType=true>
  <@probabilityDistributionTableHeader withDistributionType=withDistributionType>
    <#list probabilityDistributions as probabilityDistribution>
      <tr class="govuk-table__row">
        <th scope="row" class="govuk-table__header">${probabilityDistribution.probabilityDistributionName()}</th>
        <#if withDistributionType>
          <td class="govuk-table__cell">${probabilityDistribution.probabilityDistributionType()}</td>
          <td class="govuk-table__cell govuk-table__cell--numeric">${probabilityDistribution.min()}</td>
        </#if>
        <td class="govuk-table__cell govuk-table__cell--numeric">${probabilityDistribution.p99()}</td>
        <td class="govuk-table__cell govuk-table__cell--numeric">${probabilityDistribution.p90()}</td>
        <td class="govuk-table__cell govuk-table__cell--numeric">${probabilityDistribution.mode()}</td>
        <td class="govuk-table__cell govuk-table__cell--numeric">${probabilityDistribution.p50()}</td>
        <td class="govuk-table__cell govuk-table__cell--numeric">${probabilityDistribution.meanOrConstant()}</td>
        <td class="govuk-table__cell govuk-table__cell--numeric">${probabilityDistribution.p10()}</td>
        <td class="govuk-table__cell govuk-table__cell--numeric">${probabilityDistribution.p1()}</td>
        <#if withDistributionType>
          <td class="govuk-table__cell govuk-table__cell--numeric">${probabilityDistribution.max()}</td>
        </#if>
        <td class="govuk-table__cell govuk-table__cell--numeric">${probabilityDistribution.units()}</td>
      </tr>
    </#list>
  </@probabilityDistributionTableHeader>

</#macro>

<#macro probabilityDistributionTableHeader
  withDistributionType=true>
  <table class="govuk-table">
    <thead class="govuk-table__head">
    <tr class="govuk-table__row">
      <th scope="col" class="govuk-table__header"/>
      <#if withDistributionType>
        <th scope="col" class="govuk-table__header">Distribution type</th>
        <th scope="col" class="govuk-table__header govuk-table__header--numeric">Min</th>
      </#if>
      <th scope="col" class="govuk-table__header govuk-table__header--numeric">P99</th>
      <th scope="col" class="govuk-table__header govuk-table__header--numeric">P90</th>
      <th scope="col" class="govuk-table__header govuk-table__header--numeric">Mode</th>
      <th scope="col" class="govuk-table__header govuk-table__header--numeric">P50</th>
      <th scope="col" class="govuk-table__header govuk-table__header--numeric">Mean/constant</th>
      <th scope="col" class="govuk-table__header govuk-table__header--numeric">P10</th>
      <th scope="col" class="govuk-table__header govuk-table__header--numeric">P1</th>
      <#if withDistributionType>
        <th scope="col" class="govuk-table__header govuk-table__header--numeric">Max</th>
      </#if>
      <th scope="col" class="govuk-table__header govuk-table__header--numeric">Units</th>
    </tr>
    </thead>
    <tbody class="govuk-table__body">
      <#nested>
    </tbody>
  </table>
</#macro>
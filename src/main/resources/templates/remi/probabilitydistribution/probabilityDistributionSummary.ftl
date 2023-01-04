<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="probabilityDistributions" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView>" -->
<#-- @ftlvariable name="probabilityDistribution" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView" -->

<#macro probabilityDistributionTable
  probabilityDistributions = []
  withDistributionType=true>
  <@probabilityDistributionTableHeader withDistributionType=withDistributionType>
    <#list probabilityDistributions as probabilityDistribution>
      <tr class="govuk-table__row">
        <th scope="row" class="govuk-table__header">
          <#if withDistributionType>
              ${probabilityDistribution.probabilityDistributionName()}
            <#if probabilityDistribution.probabilityDistributionType()?has_content>
              <span class="govuk-caption-m">
                ${probabilityDistribution.probabilityDistributionType()}
              </span>
            </#if>
          <#else>
            ${probabilityDistribution.probabilityDistributionName()}
          </#if>
        </th>
        <#if withDistributionType>
          <td class="govuk-table__cell">${probabilityDistribution.min()}</td>
        </#if>
        <td class="govuk-table__cell">${probabilityDistribution.p99()}</td>
        <td class="govuk-table__cell">${probabilityDistribution.p90()}</td>
        <td class="govuk-table__cell">${probabilityDistribution.mode()}</td>
        <td class="govuk-table__cell">${probabilityDistribution.p50()}</td>
        <td class="govuk-table__cell">${probabilityDistribution.meanOrConstant()}</td>
        <td class="govuk-table__cell">${probabilityDistribution.p10()}</td>
        <td class="govuk-table__cell">${probabilityDistribution.p1()}</td>
        <#if withDistributionType>
          <td class="govuk-table__cell">${probabilityDistribution.max()}</td>
        </#if>
        <td class="govuk-table__cell govuk-table__cell">${probabilityDistribution.units()}</td>
      </tr>
    </#list>
  </@probabilityDistributionTableHeader>

</#macro>

<#macro minMaxMostLikelyTable
  probabilityDistributions = []>
  <@minMaxMostLikelyHeader>
    <#list probabilityDistributions as probabilityDistribution>
      <tr class="govuk-table__row">
        <th scope="row" class="govuk-table__header">${probabilityDistribution.probabilityDistributionName()}</th>
        <td class="govuk-table__cell">${probabilityDistribution.min()}</td>
        <td class="govuk-table__cell">${probabilityDistribution.mostLikely()}</td>
        <td class="govuk-table__cell">${probabilityDistribution.max()}</td>
        <td class="govuk-table__cell">${probabilityDistribution.units()}</td>
      </tr>
    </#list>
  </@minMaxMostLikelyHeader>
</#macro>

<#macro minMaxMostLikelyHeader>
  <table class="govuk-table remi-table">
    <thead class="govuk-table__head">
    <tr class="govuk-table__row">
      <th scope="col" class="govuk-table__header"></th>
      <th scope="col" class="govuk-table__header">Min</th>
      <th scope="col" class="govuk-table__header">Most likely</th>
      <th scope="col" class="govuk-table__header">Max</th>
      <th scope="col" class="govuk-table__header">Units</th>
    </tr>
    </thead>
    <tbody class="govuk-table__body">
      <#nested>
    </tbody>
  </table>
</#macro>

<#macro probabilityDistributionTableHeader
  withDistributionType=true>
  <table class="govuk-table remi-table">
    <thead class="govuk-table__head">
    <tr class="govuk-table__row">
      <th scope="col" class="govuk-table__header"/>
      <#if withDistributionType>
        <th scope="col" class="govuk-table__header">Min</th>
      </#if>
      <th scope="col" class="govuk-table__header">P99</th>
      <th scope="col" class="govuk-table__header">P90</th>
      <th scope="col" class="govuk-table__header">Mode</th>
      <th scope="col" class="govuk-table__header">P50</th>
      <th scope="col" class="govuk-table__header">Mean / constant</th>
      <th scope="col" class="govuk-table__header">P10</th>
      <th scope="col" class="govuk-table__header">P1</th>
      <#if withDistributionType>
        <th scope="col" class="govuk-table__header">Max</th>
      </#if>
      <th scope="col" class="govuk-table__header">Units</th>
    </tr>
    </thead>
    <tbody class="govuk-table__body">
      <#nested>
    </tbody>
  </table>
</#macro>
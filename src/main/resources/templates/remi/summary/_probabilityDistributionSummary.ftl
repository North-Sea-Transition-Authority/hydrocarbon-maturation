<#include '../layout/layout.ftl'>
<#import 'tableSummaryCard.ftl' as tableSummaryCard>

<#-- @ftlvariable name="tableView" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView>" -->
<#-- @ftlvariable name="probabilityDistribution" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView" -->
<#-- @ftlvariable name="summaryBehaviour" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.SummaryBehaviour" -->

<#macro probabilityDistributionTable
tableView
withDistributionType=true
withContingentUncertaintyCategories=false
heading=""
summaryCardId="summary-data-card-list">
  <#if tableView.probabilityDistributions()?has_content>
    <#if heading?has_content>
      <@tableSummaryCard.tableCard
      headingText=heading
      headingSize="h3"
      summaryCardId=summaryCardId>
        <@probabilityDistributionTableInner
        tableView=tableView
        withDistributionType=withDistributionType
        withContingentUncertaintyCategories=withContingentUncertaintyCategories/>
      </@tableSummaryCard.tableCard>
    <#else>
      <@probabilityDistributionTableInner
      tableView=tableView
      withDistributionType=withDistributionType
      withContingentUncertaintyCategories=withContingentUncertaintyCategories/>
    </#if>
  </#if>
</#macro>

<#macro probabilityDistributionTableInner
tableView
withDistributionType=true
withContingentUncertaintyCategories=false>
  <#assign summaryBehaviour = tableView.summaryBehaviour()/>
  <@probabilityDistributionTableHeader
  tableView=tableView
  withDistributionType=withDistributionType
  withContingentUncertaintyCategories=withContingentUncertaintyCategories>
    <#list tableView.probabilityDistributions() as probabilityDistribution>
      <tr class="govuk-table__row">
        <th scope="row" class="govuk-table__header">

          <#if withDistributionType>
            ${probabilityDistribution.probabilityDistributionName()}
            <#if probabilityDistribution.probabilityDistributionType()?has_content>
              <#assign hintClass>
                govuk-hint <#if !probabilityDistribution.comments()?has_content>govuk-!-margin-bottom-0</#if>
              </#assign>
              <div class="${hintClass}">
                ${probabilityDistribution.probabilityDistributionType()}
              </div>
            </#if>
          <#else>
            ${probabilityDistribution.probabilityDistributionName()}
          </#if>

          <#if probabilityDistribution.comments()?has_content>
            <#assign comments>
              <p class="govuk-body govuk-body__preserve-whitespace">${probabilityDistribution.comments()}</p>
            </#assign>
            <#assign commentsTitle>
              Comments<span
                    class="govuk-visually-hidden"> for ${probabilityDistribution.probabilityDistributionName()}</span>
            </#assign>
            <#assign detailsClass>
              govuk-!-margin-bottom-0 <#if !withDistributionType> govuk-!-margin-top-3</#if>
            </#assign>
            <@fdsDetails.details detailsText=comments detailsTitle=commentsTitle detailsClass=detailsClass/>
          </#if>

        </th>

        <#if withDistributionType && summaryBehaviour.isMinAvailable()>
          <td class="govuk-table__cell">${probabilityDistribution.min()}</td>
        </#if>

        <#if summaryBehaviour.isP99Available()>
          <td class="govuk-table__cell">${probabilityDistribution.p99()}</td>
        </#if>

        <#if withContingentUncertaintyCategories && (summaryBehaviour.isP90Available() || summaryBehaviour.is1CAvailable())>
          <#if probabilityDistribution.oneC()?has_content>
            <td class="govuk-table__cell">${probabilityDistribution.oneC()}</td>
          <#else>
            <td class="govuk-table__cell">${probabilityDistribution.p90()}</td>
          </#if>
        <#else>
          <#if summaryBehaviour.isP90Available()>
            <td class="govuk-table__cell">${probabilityDistribution.p90()}</td>
          </#if>

          <#if summaryBehaviour.is1CAvailable()>
            <td class="govuk-table__cell">${probabilityDistribution.oneC()}</td>
          </#if>
        </#if>

        <#if summaryBehaviour.isModeAvailable()>
          <td class="govuk-table__cell">${probabilityDistribution.mode()}</td>
        </#if>

        <#if withContingentUncertaintyCategories && (summaryBehaviour.isP50Available() || summaryBehaviour.is2CAvailable())>
          <#if probabilityDistribution.twoC()?has_content>
            <td class="govuk-table__cell">${probabilityDistribution.twoC()}</td>
          <#else>
            <td class="govuk-table__cell">${probabilityDistribution.p50()}</td>
          </#if>
        <#else>
          <#if summaryBehaviour.isP50Available()>
            <td class="govuk-table__cell">${probabilityDistribution.p50()}</td>
          </#if>
          <#if summaryBehaviour.is2CAvailable()>
            <td class="govuk-table__cell">${probabilityDistribution.twoC()}</td>
          </#if>
        </#if>

        <#if summaryBehaviour.isConstantAvailable() || summaryBehaviour.isMeanAvailable()>
          <td class="govuk-table__cell">${probabilityDistribution.meanOrConstant()}</td>
        </#if>

        <#if withContingentUncertaintyCategories && (summaryBehaviour.isP10Available() || summaryBehaviour.is3CAvailable())>
          <#if probabilityDistribution.threeC()?has_content>
            <td class="govuk-table__cell">${probabilityDistribution.threeC()}</td>
          <#else>
            <td class="govuk-table__cell">${probabilityDistribution.p10()}</td>
          </#if>
        <#else>
          <#if summaryBehaviour.isP10Available()>
            <td class="govuk-table__cell">${probabilityDistribution.p10()}</td>
          </#if>
          <#if summaryBehaviour.is3CAvailable()>
            <td class="govuk-table__cell">${probabilityDistribution.threeC()}</td>
          </#if>
        </#if>

        <#if summaryBehaviour.is1PAvailable()>
          <td class="govuk-table__cell">${probabilityDistribution.oneP()}</td>
        </#if>

        <#if summaryBehaviour.is2PAvailable()>
          <td class="govuk-table__cell">${probabilityDistribution.twoP()}</td>
        </#if>

        <#if summaryBehaviour.is3PAvailable()>
          <td class="govuk-table__cell">${probabilityDistribution.threeP()}</td>
        </#if>

        <#if summaryBehaviour.isP1Available()>
          <td class="govuk-table__cell">${probabilityDistribution.p1()}</td>
        </#if>

        <#if withDistributionType && summaryBehaviour.isMostLikelyAvailable()>
          <td class="govuk-table__cell">${probabilityDistribution.mostLikely()}</td>
        </#if>

        <#if withDistributionType && summaryBehaviour.isMaxAvailable()>
          <td class="govuk-table__cell">${probabilityDistribution.max()}</td>
        </#if>

        <td class="govuk-table__cell">${probabilityDistribution.units()}</td>
      </tr>
    </#list>
  </@probabilityDistributionTableHeader>
</#macro>

<#macro probabilityDistributionTableHeader
  tableView
  withDistributionType=true
  withContingentUncertaintyCategories=false>
  <#assign summaryBehaviour = tableView.summaryBehaviour()/>
  <table class="govuk-table remi-table">
    <thead class="govuk-table__head">
    <tr class="govuk-table__row">
      <th scope="col" class="govuk-table__header">
        <span class="govuk-visually-hidden">Probability distribution fields</span>
      </th>

      <#if withDistributionType && summaryBehaviour.isMinAvailable()>
        <th scope="col" class="govuk-table__header">Min</th>
      </#if>

      <#if summaryBehaviour.isP99Available()>
        <th scope="col" class="govuk-table__header">P99</th>
      </#if>

      <#if withContingentUncertaintyCategories && (summaryBehaviour.isP90Available() || summaryBehaviour.is1CAvailable())>
        <th scope="col" class="govuk-table__header">P90 or 1C</th>
      <#else>
        <#if summaryBehaviour.isP90Available()>
          <th scope="col" class="govuk-table__header">P90</th>
        </#if>

        <#if summaryBehaviour.is1CAvailable()>
          <th scope="col" class="govuk-table__header">1C</th>
        </#if>
      </#if>

      <#if summaryBehaviour.isModeAvailable()>
        <th scope="col" class="govuk-table__header">Mode</th>
      </#if>

      <#if withContingentUncertaintyCategories && (summaryBehaviour.isP50Available() || summaryBehaviour.is2CAvailable())>
        <th scope="col" class="govuk-table__header">P50 or 2C</th>
      <#else>
        <#if summaryBehaviour.isP50Available()>
          <th scope="col" class="govuk-table__header">P50</th>
        </#if>
        <#if summaryBehaviour.is2CAvailable()>
          <th scope="col" class="govuk-table__header">2C</th>
        </#if>
      </#if>

      <#if summaryBehaviour.isConstantAvailable() || summaryBehaviour.isMeanAvailable()>
        <th scope="col" class="govuk-table__header">Mean / <br/>constant</th>
      </#if>

      <#if withContingentUncertaintyCategories && (summaryBehaviour.isP10Available() || summaryBehaviour.is3CAvailable())>
        <th scope="col" class="govuk-table__header">P10 or 3C</th>
      <#else>
        <#if summaryBehaviour.isP10Available()>
          <th scope="col" class="govuk-table__header">P10</th>
        </#if>
        <#if summaryBehaviour.is3CAvailable()>
          <th scope="col" class="govuk-table__header">3C</th>
        </#if>
      </#if>

      <#if summaryBehaviour.is1PAvailable()>
        <th scope="col" class="govuk-table__header">1P</th>
      </#if>

      <#if summaryBehaviour.is2PAvailable()>
        <th scope="col" class="govuk-table__header">2P</th>
      </#if>

      <#if summaryBehaviour.is3PAvailable()>
        <th scope="col" class="govuk-table__header">3P</th>
      </#if>

      <#if summaryBehaviour.isP1Available()>
        <th scope="col" class="govuk-table__header">P1</th>
      </#if>

      <#if withDistributionType && summaryBehaviour.isMostLikelyAvailable()>
        <th scope="col" class="govuk-table__header">Most likely</th>
      </#if>

      <#if withDistributionType && summaryBehaviour.isMaxAvailable()>
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

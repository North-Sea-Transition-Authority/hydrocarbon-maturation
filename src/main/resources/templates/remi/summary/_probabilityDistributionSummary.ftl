<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="probabilityDistributions" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView>" -->
<#-- @ftlvariable name="probabilityDistribution" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView" -->

<#macro probabilityDistributionTable
probabilityDistributions = []
withDistributionType=true
withContingentUncertaintyCategories=false
heading="">
  <#if heading?has_content>
    <@fdsSummaryList.summaryListCard
    headingText=heading
    headingSize="h3"
    summaryListId="summary-data-card-list">
      <@probabilityDistributionTableInner
      probabilityDistributions
      withDistributionType
      withContingentUncertaintyCategories />
    </@fdsSummaryList.summaryListCard>
  <#else>
    <@probabilityDistributionTableInner
    probabilityDistributions
    withDistributionType
    withContingentUncertaintyCategories />
  </#if>
</#macro>

<#macro probabilityDistributionTableInner
probabilityDistributions = []
withDistributionType=true
withContingentUncertaintyCategories=false>
  <@probabilityDistributionTableHeader withDistributionType=withDistributionType withContingentUncertaintyCategories=withContingentUncertaintyCategories>
    <#list probabilityDistributions as probabilityDistribution>
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
        <#if withDistributionType>
          <td class="govuk-table__cell">${probabilityDistribution.min()}</td>
        </#if>
        <td class="govuk-table__cell">${probabilityDistribution.p99()}</td>
        <#if withContingentUncertaintyCategories && probabilityDistribution.oneC()?has_content>
          <td class="govuk-table__cell">${probabilityDistribution.oneC()}</td>
        <#else>
          <td class="govuk-table__cell">${probabilityDistribution.p90()}</td>
        </#if>
        <td class="govuk-table__cell">${probabilityDistribution.mode()}</td>
        <#if withContingentUncertaintyCategories && probabilityDistribution.twoC()?has_content>
          <td class="govuk-table__cell">${probabilityDistribution.twoC()}</td>
        <#else>
          <td class="govuk-table__cell">${probabilityDistribution.p50()}</td>
        </#if>
        <td class="govuk-table__cell">${probabilityDistribution.meanOrConstant()}</td>
        <#if withContingentUncertaintyCategories && probabilityDistribution.threeC()?has_content>
          <td class="govuk-table__cell">${probabilityDistribution.threeC()}</td>
        <#else>
          <td class="govuk-table__cell">${probabilityDistribution.p10()}</td>
        </#if>
        <td class="govuk-table__cell">${probabilityDistribution.p1()}</td>
        <#if withDistributionType>
          <td class="govuk-table__cell">${probabilityDistribution.max()}</td>
        </#if>
        <td class="govuk-table__cell govuk-table__cell">${probabilityDistribution.units()}</td>
      </tr>
    </#list>
  </@probabilityDistributionTableHeader>
</#macro>

<#macro fieldProbabilityDistributionTable
probabilityDistributions = []
withDistributionType=true>
  <@fieldProbabilityDistributionTableHeader>
    <#list probabilityDistributions as probabilityDistribution>
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
              Comments<span class="govuk-visually-hidden"> for ${probabilityDistribution.probabilityDistributionName()}</span>
            </#assign>
            <#assign detailsClass>
              govuk-!-margin-bottom-0 <#if !withDistributionType> govuk-!-margin-top-3</#if>
            </#assign>
            <@fdsDetails.details detailsText=comments detailsTitle=commentsTitle detailsClass=detailsClass/>
          </#if>
        </th>

        <td class="govuk-table__cell">${probabilityDistribution.oneC()}</td>
        <td class="govuk-table__cell">${probabilityDistribution.twoC()}</td>
        <td class="govuk-table__cell">${probabilityDistribution.threeC()}</td>
        <td class="govuk-table__cell">${probabilityDistribution.oneP()}</td>
        <td class="govuk-table__cell">${probabilityDistribution.twoP()}</td>
        <td class="govuk-table__cell">${probabilityDistribution.threeP()}</td>
        <td class="govuk-table__cell govuk-table__cell">${probabilityDistribution.units()}</td>
      </tr>
    </#list>
  </@fieldProbabilityDistributionTableHeader>

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
  withDistributionType=true
  withContingentUncertaintyCategories=false>
  <table class="govuk-table remi-table">
    <thead class="govuk-table__head">
    <tr class="govuk-table__row">
      <th scope="col" class="govuk-table__header"/>
      <#if withDistributionType>
        <th scope="col" class="govuk-table__header">Min</th>
      </#if>
      <th scope="col" class="govuk-table__header">P99</th>
      <#if withContingentUncertaintyCategories>
        <th scope="col" class="govuk-table__header">P90 or 1C</th>
      <#else>
        <th scope="col" class="govuk-table__header">P90</th>
      </#if>
      <th scope="col" class="govuk-table__header">Mode</th>
      <#if withContingentUncertaintyCategories>
        <th scope="col" class="govuk-table__header">P50 or 2C</th>
      <#else>
        <th scope="col" class="govuk-table__header">P50</th>
      </#if>
      <th scope="col" class="govuk-table__header">Mean / <br/>constant</th>
      <#if withContingentUncertaintyCategories>
        <th scope="col" class="govuk-table__header">P10 or 3C</th>
      <#else>
        <th scope="col" class="govuk-table__header">P10</th>
      </#if>
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

<#macro fieldProbabilityDistributionTableHeader>
  <table class="govuk-table remi-table">
    <thead class="govuk-table__head">
    <tr class="govuk-table__row">
      <th scope="col" class="govuk-table__header"/>
      <th scope="col" class="govuk-table__header">1C</th>
      <th scope="col" class="govuk-table__header">2C</th>
      <th scope="col" class="govuk-table__header">3C</th>
      <th scope="col" class="govuk-table__header">1P</th>
      <th scope="col" class="govuk-table__header">2P</th>
      <th scope="col" class="govuk-table__header">3P</th>
      <th scope="col" class="govuk-table__header">Units</th>
    </tr>
    </thead>
    <tbody class="govuk-table__body">
    <#nested>
    </tbody>
  </table>
</#macro>
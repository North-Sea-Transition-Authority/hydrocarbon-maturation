<#include '../layout/layout.ftl'>

<#-- @ftlvariable name = "permeabilityDetailsValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailSummaryView" -->
<#-- @ftlvariable name = "optionalPermeability" type="java.lang.Boolean" -->

<#macro permeabilityDetailsSummaryCard permeabilityDetailsValues optionalPermeability=false>
  <@fdsSummaryList.summaryListCard headingText="Permeability" summaryListId="permeability">
    <#if optionalPermeability>
      <@fdsSummaryList.summaryListRowNoAction keyText="Do you have permeability details?">
        ${permeabilityDetailsValues.hasPermeability()}
      </@fdsSummaryList.summaryListRowNoAction>
    </#if>
    <#if permeabilityDetailsValues.hasPermeabilityBol() || !optionalPermeability>
      <@fdsSummaryList.summaryListRowNoAction keyText="Permeability (md) min">
        ${permeabilityDetailsValues.permeabilityMin()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Permeability (md) max">
        ${permeabilityDetailsValues.permeabilityMax()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Permeability (md) average">
        ${permeabilityDetailsValues.permeabilityAverage()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Permeability (md) average method used">
        ${permeabilityDetailsValues.averageMethodUsed()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Permeability (md) source">
        ${permeabilityDetailsValues.permeabilitySource()}
      </@fdsSummaryList.summaryListRowNoAction>
      <#if permeabilityDetailsValues.permeabilitySource() == "Other">
        <@fdsSummaryList.summaryListRowNoAction keyText="Other permeability (md) source">
          <p class="govuk-body govuk-body__preserve-whitespace">${permeabilityDetailsValues.otherPermeabilitySource()}</p>
        </@fdsSummaryList.summaryListRowNoAction>
      </#if>
      <@fdsSummaryList.summaryListRowNoAction keyText="Permeability (md) comments">
        <p class="govuk-body govuk-body__preserve-whitespace">${permeabilityDetailsValues.permeabilityComments()}</p>
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Kv/Kh average">
        ${permeabilityDetailsValues.kvKhAverage()}
      </@fdsSummaryList.summaryListRowNoAction>
    </#if>
  </@fdsSummaryList.summaryListCard>
</#macro>
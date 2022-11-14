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
          ${permeabilityDetailsValues.otherPermeabilitySource()}
        </@fdsSummaryList.summaryListRowNoAction>
      </#if>
      <@fdsSummaryList.summaryListRowNoAction keyText="Permeability (md) comments">
        ${permeabilityDetailsValues.permeabilityComments()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Kv/kh min">
        ${permeabilityDetailsValues.kvKhMin()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Kv/kh max">
        ${permeabilityDetailsValues.kvKhMax()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Kv/kh average">
        ${permeabilityDetailsValues.kvKhAverage()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Kv/kh average method used">
        ${permeabilityDetailsValues.kvKhAverageMethodUsed()}
      </@fdsSummaryList.summaryListRowNoAction>
      <#if permeabilityDetailsValues.kvKhAverageMethodUsed() == "Other">
        <@fdsSummaryList.summaryListRowNoAction keyText="Kv/kh other average method used">
          ${permeabilityDetailsValues.kvKhAverageMethodUsedOther()}
        </@fdsSummaryList.summaryListRowNoAction>
      </#if>
      <@fdsSummaryList.summaryListRowNoAction keyText="Kv/kh source">
        ${permeabilityDetailsValues.kvKhSource()}
      </@fdsSummaryList.summaryListRowNoAction>
      <#if permeabilityDetailsValues.kvKhSource() == "Other">
        <@fdsSummaryList.summaryListRowNoAction keyText="Kv/kh other source">
          ${permeabilityDetailsValues.otherKvKhSource()}
        </@fdsSummaryList.summaryListRowNoAction>
      </#if>
      <@fdsSummaryList.summaryListRowNoAction keyText="Kv/kh comments">
        ${permeabilityDetailsValues.kvKhComments()}
      </@fdsSummaryList.summaryListRowNoAction>
    </#if>
  </@fdsSummaryList.summaryListCard>
</#macro>
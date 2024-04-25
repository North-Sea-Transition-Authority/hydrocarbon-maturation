<#include '../layout/layout.ftl'>
<#import '_emptySummary.ftl' as emptySummary>
<#import '_simpleSummary.ftl' as simpleSummary>

<#-- @ftlvariable name="summaryCard" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard>" -->

<#macro undevelopedDiscoverySummaryCard summaryCard>
  <#assign actionsContent>
    <@fdsSummaryList.summaryListCardActionList>
      <#list summaryCard.summaryActions() as action>
        <@fdsSummaryList.summaryListCardActionItem itemUrl=springUrl(action.url()) itemText=action.label() itemScreenReaderText=action.screenReaderText()/>
      </#list>
    </@fdsSummaryList.summaryListCardActionList>
  </#assign>

  <@fdsSummaryList.summaryListCard
    headingText=summaryCard.displayName()
    headingSize="h3"
    summaryListId="summary-data-card-list"
    cardActionsContent=actionsContent>
      <#if summaryCard.summaryCardType() == "SIMPLE_SUMMARY">
        <@simpleSummary.simpleSummary summaryDataView=summaryCard.summaryData()/>
      <#elseif summaryCard.summaryCardType() == "EMPTY_SUMMARY">
        <@emptySummary.emptySummary/>
      </#if>
  </@fdsSummaryList.summaryListCard>
</#macro>
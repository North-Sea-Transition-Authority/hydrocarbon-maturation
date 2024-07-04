<#include '../layout/layout.ftl'>
<#import '_simpleSummary.ftl' as simpleSummary>
<#import '_tableSummary.ftl' as tableSummary>
<#import '_emptySummary.ftl' as emptySummary>
<#import '_fileSummary.ftl' as filesSummary>
<#import '_probabilityDistributionSummary.ftl' as probabilityDistributionSummary>

<#-- @ftlvariable name="summarySections" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection>" -->
<#-- @ftlvariable name="summaryItem" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem>" -->
<#-- @ftlvariable name="summaryCard" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard>" -->

<#macro submissionSummary accordionId>
  <@fdsAccordion.accordion accordionId="summaryaccordian-${accordionId}">
    <#list summarySections as summarySection>
      <#list summarySection.summaryItems() as summaryItem>
        <@fdsAccordion.accordionSection sectionHeading=summaryItem.displayName() summaryText=summarySection.summaryText()
          openSection=(summarySection?index == 0 && summaryItem?index == 0)>
          <#list summaryItem.summaryCards() as summaryCard>
            <#if summaryCard.summaryCardType() == "EMPTY_SUMMARY">
              <@emptySummary.emptySummaryCard
                heading=summaryCard.displayName()/>
            <#elseif summaryCard.summaryCardType() == "PROBABILITY_DISTRIBUTION_SUMMARY">
              <@probabilityDistributionSummary.probabilityDistributionTable
              tableView=summaryCard.summaryData()
              heading=summaryCard.displayName()!""/>
            <#else>
              <#assign actionsContent>
                <@fdsSummaryList.summaryListCardActionList>
                  <#list summaryCard.summaryActions() as action>
                    <@fdsSummaryList.summaryListCardActionItem itemUrl=springUrl(action.url()) itemText=action.label() itemScreenReaderText=""/>
                  </#list>
                </@fdsSummaryList.summaryListCardActionList>
              </#assign>

              <@fdsSummaryList.summaryListCard
              headingText=summaryCard.displayName()
              headingSize="h3"
              summaryListId="summary-data-card-list"
              cardActionsContent=actionsContent>
                  <#if summaryCard.summaryCardType() == "SIMPLE_SUMMARY">
                    <@simpleSummary.simpleSummary
                    summaryDataView=summaryCard.summaryData()/>
                  <#elseif summaryCard.summaryCardType() == "TABLE_SUMMARY">
                    <@tableSummary.tableSummary
                    summaryTableView=summaryCard.summaryData()/>
                  <#elseif summaryCard.summaryCardType() == "FILES_SUMMARY">
                    <@filesSummary.fileSummary
                    fileViews=summaryCard.summaryData()/>
                  <#elseif summaryCard.summaryCardType() == "TABLE_FIXED_WIDTH_SUMMARY">
                    <@tableSummary.tableFixedWidthSummary
                    summaryTableView=summaryCard.summaryData()/>
                  </#if>
              </@fdsSummaryList.summaryListCard>
            </#if>
          </#list>
        </@fdsAccordion.accordionSection>
      </#list>
    </#list>
      <#nested/>
  </@fdsAccordion.accordion>
</#macro>

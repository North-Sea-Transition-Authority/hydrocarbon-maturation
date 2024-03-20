<#include '../layout/layout.ftl'>
<#import '_simpleSummary.ftl' as simpleSummary>
<#import '_timelineSummary.ftl' as timelineSummary>
<#import '_emptySummary.ftl' as emptySummary>
<#import '_tableSummary.ftl' as tableSummary>

<#-- @ftlvariable name="summarySections" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection>" -->
<#-- @ftlvariable name="summaryItem" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem>" -->
<#-- @ftlvariable name="summaryCard" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard>" -->

<#macro projectSummary accordionId>
    <@fdsAccordion.accordion accordionId="summaryaccordian-${accordionId}">
        <#list summarySections as summarySection>
            <#list summarySection.summaryItems() as summaryItem>
                <@fdsAccordion.accordionSection sectionHeading=summaryItem.displayName() summaryText=summarySection.summaryText()
                openSection=(summarySection?index == 0 && summaryItem?index == 0)>
                    <#list summaryItem.summaryCards() as summaryCard>
                        <#if summaryCard.summaryCardType() == "SIMPLE_SUMMARY_WITH_ACTIONS">
                            <@simpleSummary.simpleSummaryWithActions
                            summaryDataViewWithActions=summaryCard.summaryData()
                            summaryHeading=summaryCard.displayName()!""/>
                        <#elseif summaryCard.summaryCardType() == "TIMELINE_SUMMARY">
                            <@timelineSummary.timelineSummary
                            summaryTimelineView=summaryCard.summaryData()
                            summaryHeading=summaryCard.displayName()!""/>
                        <#elseif summaryCard.summaryCardType() == "EMPTY_SUMMARY_WITH_ACTIONS">
                            <@emptySummary.emptySummaryWithActions
                            heading=summaryCard.displayName()
                            actionsMap=summaryCard.summaryData()/>
                        <#elseif summaryCard.summaryCardType() == "TABLE_SUMMARY">
                            <@tableSummary.tableSummary
                            summaryTableView=summaryCard.summaryData()
                            summaryHeading=summaryCard.displayName()!""
                            summaryActions=summaryCard.actionsMap()/>
                        </#if>
                    </#list>
                </@fdsAccordion.accordionSection>
            </#list>
        </#list>
        <#nested/>
    </@fdsAccordion.accordion>
</#macro>

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
                            <#elseif summaryCard.summaryCardType() == "EMPTY_SUMMARY">
                                <@emptySummary.emptySummary/>
                            <#elseif summaryCard.summaryCardType() == "TIMELINE_SUMMARY">
                                <@timelineSummary.timelineSummary
                                summaryTimelineView=summaryCard.summaryData()/>
                            <#elseif summaryCard.summaryCardType() == "TABLE_SUMMARY">
                                <@tableSummary.tableSummary
                                summaryTableView=summaryCard.summaryData()/>
                            </#if>
                        </@fdsSummaryList.summaryListCard>
                    </#list>
                </@fdsAccordion.accordionSection>
            </#list>
        </#list>
        <#nested/>
    </@fdsAccordion.accordion>
</#macro>

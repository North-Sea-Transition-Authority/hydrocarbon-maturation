<#include '../layout/layout.ftl'>
<#import '_simpleSummary.ftl' as simpleSummary>
<#import '_tableSummary.ftl' as tableSummary>
<#import '_emptySummary.ftl' as emptySummary>
<#import '_probabilityDistributionSummary.ftl' as probabilityDistributionSummary>

<#-- @ftlvariable name="summarySections" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection>" -->
<#-- @ftlvariable name="summaryItem" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem>" -->
<#-- @ftlvariable name="summaryCard" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard>" -->
<#-- @ftlvariable name="reservoirSummaries" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ReservoirSummary>" -->

<#macro reservoirSummary reservoirSummaries>
    <#list reservoirSummaries as reservoirSummary>
        <@fdsAccordion.accordionSection sectionHeading="${reservoirSummary.reservoirName()}" summaryText="General reservoir information, properties, volumetric parameters, and outputs for ${reservoirSummary.reservoirName()}">
            <@reservoirSummarySections reservoirSummary=reservoirSummary/>
        </@fdsAccordion.accordionSection>
    </#list>

</#macro>

<#macro reservoirSummarySections reservoirSummary>
    <#list reservoirSummary.summarySections() as summarySection>
        <#list summarySection.summaryItems() as summaryItem>
            <#assign summaryItemId = "reservoirsummaryitem" + summaryItem?index + summarySection?index/>
            <@fdsSummaryList.summaryListCard headingText="${summaryItem.displayName()}" summaryListId=summaryItemId>
                <#list summaryItem.summaryCards() as summaryCard>
                    <#if summaryCard.summaryCardType() == "PROBABILITY_DISTRIBUTION_SUMMARY">
                        <@probabilityDistributionSummary.probabilityDistributionTable
                        probabilityDistributions=summaryCard.summaryData()/>
                    <#elseif summaryCard.summaryCardType() == "EMPTY_SUMMARY">
                        <@emptySummary.emptySummaryCard/>
                    <#elseif summaryCard.summaryCardType() == "MIN_MAX_MOST_LIKELY_SUMMARY">
                        <@probabilityDistributionSummary.minMaxMostLikelyTable
                        probabilityDistributions=summaryCard.summaryData()/>
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
                            </#if>
                        </@fdsSummaryList.summaryListCard>
                    </#if>
                </#list>
            </@fdsSummaryList.summaryListCard>
        </#list>
    </#list>
</#macro>
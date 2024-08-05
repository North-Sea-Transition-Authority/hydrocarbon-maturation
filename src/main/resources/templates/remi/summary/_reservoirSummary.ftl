<#include '../layout/layout.ftl'>
<#import '_simpleSummary.ftl' as simpleSummary>
<#import '_tableSummary.ftl' as tableSummary>
<#import '_emptySummary.ftl' as emptySummary>
<#import '_probabilityDistributionSummary.ftl' as probabilityDistributionSummary>
<#import 'tableSummaryCard.ftl' as tableSummaryCard>

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
            <#assign headingText = summaryItem.displayName()/>

            <#list summaryItem.summaryCards() as summaryCard>
                <#assign actionsContent>
                    <@fdsSummaryList.summaryListCardActionList>
                        <#list summaryCard.summaryActions() as action>
                            <@fdsSummaryList.summaryListCardActionItem
                            itemUrl=springUrl(action.url())
                            itemText=action.label()
                            itemScreenReaderText=""/>
                        </#list>
                    </@fdsSummaryList.summaryListCardActionList>
                </#assign>

                <#if summaryCard.summaryCardType() == "PROBABILITY_DISTRIBUTION_SUMMARY">
                    <@probabilityDistributionSummary.probabilityDistributionTable
                      heading=headingText
                      summaryCardId=summaryItemId
                      tableView=summaryCard.summaryData()/>
                <#elseif summaryCard.summaryCardType() == "TABLE_SUMMARY">
                    <@tableSummaryCard.tableCard
                      headingText=headingText
                      headingSize="h3"
                      summaryCardId=summaryItemId
                      cardActionsContent=actionsContent>
                        <@tableSummary.tableSummary
                          summaryTableView=summaryCard.summaryData()
                          hiddenHeadingText=summaryCard.displayName()/>
                    </@tableSummaryCard.tableCard>
                <#elseif summaryCard.summaryCardType() == "EMPTY_SUMMARY">
                    <@emptySummary.emptySummaryCard heading=headingText/>
                <#else>
                    <@fdsSummaryList.summaryListCard
                      headingText=headingText
                      headingSize="h3"
                      summaryListId="summary-data-card-list"
                      cardActionsContent=actionsContent>
                        <@simpleSummary.simpleSummary
                          summaryDataView=summaryCard.summaryData()/>
                    </@fdsSummaryList.summaryListCard>
                </#if>
            </#list>
        </#list>
    </#list>
</#macro>
<#include '../layout/layout.ftl'>
<#import '_simpleSummary.ftl' as simpleSummary>
<#import '_tableSummary.ftl' as tableSummary>
<#import '_emptySummary.ftl' as emptySummary>

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
            <#if summaryCard.summaryCardType() == "SIMPLE_SUMMARY">
              <@simpleSummary.simpleSummary
                summaryDataView=summaryCard.summaryData()
                summaryHeading=summaryCard.displayName()!""/>
            <#elseif summaryCard.summaryCardType() == "TABLE_SUMMARY">
              <@tableSummary.tableSummary
                summaryTableView=summaryCard.summaryData()
                summaryHeading=summaryCard.displayName()!""/>
            <#elseif summaryCard.summaryCardType() == "EMPTY_SUMMARY">
              <@emptySummary.emptySummary heading=summaryCard.displayName()/>
            </#if>
          </#list>
        </@fdsAccordion.accordionSection>
      </#list>
    </#list>
      <#nested/>
  </@fdsAccordion.accordion>
</#macro>

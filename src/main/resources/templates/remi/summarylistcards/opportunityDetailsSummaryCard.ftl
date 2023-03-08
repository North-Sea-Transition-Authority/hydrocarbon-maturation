<#include '../layout/layout.ftl'>
<#import 'fileUploadSummaryCard.ftl' as fileUploadSummary>

<#-- @ftlvariable name="opportunityDetailsValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryView" -->

<#macro opportunityDetailsSummaryCard opportunityDetailsValues>
  <@fdsSummaryList.summaryListCard headingText="Opportunity details" summaryListId="opportunitydetails">
      <@fdsSummaryList.summaryListRowNoAction keyText="Opportunity type">
          ${opportunityDetailsValues.opportunityType()}
      </@fdsSummaryList.summaryListRowNoAction>
      <#if opportunityDetailsValues.otherOpportunity()>
        <@fdsSummaryList.summaryListRowNoAction keyText="Other opportunity type">
          ${opportunityDetailsValues.otherOpportunityType()}
        </@fdsSummaryList.summaryListRowNoAction>
      </#if>
      <@fdsSummaryList.summaryListRowNoAction keyText="Opportunity comments">
        ${opportunityDetailsValues.opportunityComments()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Key evaluation work carried out">
        ${opportunityDetailsValues.keyEvaluationWork()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Key work required for further maturation">
        ${opportunityDetailsValues.keyWorkRequired()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fileUploadSummary.fileUploadSummary
        filesUploaded=opportunityDetailsValues.mapOfOpportunity()
        fieldName="Map of opportunity"/>
      <@fileUploadSummary.fileUploadSummary
        filesUploaded=opportunityDetailsValues.geologicalCrossSection()
        fieldName="Geological cross section"/>
  </@fdsSummaryList.summaryListCard>
</#macro>
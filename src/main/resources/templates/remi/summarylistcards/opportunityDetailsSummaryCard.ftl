<#include '../layout/layout.ftl'>
<#import 'fileUploadSummaryCard.ftl' as fileUploadSummary>

<#-- @ftlvariable name="opportunityDetailsValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryView" -->

<#macro opportunityDetailsSummaryCard opportunityDetailsValues mapName>
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
      <p class="govuk-body govuk-body__preserve-whitespace">${opportunityDetailsValues.opportunityComments()}</p>
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Key evaluation work carried out">
      <p class="govuk-body govuk-body__preserve-whitespace">${opportunityDetailsValues.keyEvaluationWork()}</p>
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Key work required for further maturation">
      <p class="govuk-body govuk-body__preserve-whitespace">${opportunityDetailsValues.keyWorkRequired()}</p>
    </@fdsSummaryList.summaryListRowNoAction>
    <@fileUploadSummary.fileUploadSummary
      filesUploaded=opportunityDetailsValues.mapOfOpportunity()
      fieldName=mapName/>
    <@fileUploadSummary.fileUploadSummary
      filesUploaded=opportunityDetailsValues.representativeSeismicSection()
      fieldName="Representative seismic section"/>
    <@fileUploadSummary.fileUploadSummary
      filesUploaded=opportunityDetailsValues.geologicalCrossSection()
      fieldName="Geological cross section"/>
  </@fdsSummaryList.summaryListCard>
</#macro>
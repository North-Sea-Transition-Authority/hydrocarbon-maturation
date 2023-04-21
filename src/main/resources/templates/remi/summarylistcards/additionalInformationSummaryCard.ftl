<#include '../layout/layout.ftl'>
<#import 'fileUploadSummaryCard.ftl' as fileUploadSummary>

<#-- @ftlvariable name="additionalInformationValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation.AdditionalInformationSummaryView" -->

<#macro additionalInformationSummaryCard additionalInformationValues>
  <@fdsSummaryList.summaryListCard headingText="Additional information" summaryListId="additionalinformation">
    <@fdsSummaryList.summaryListRowNoAction keyText="Do you have any additional comments?">
      ${additionalInformationValues.providingAdditionalComments()}
    </@fdsSummaryList.summaryListRowNoAction>
    <#if additionalInformationValues.providingAdditionalComments() == "Yes">
      <@fdsSummaryList.summaryListRowNoAction keyText="Additional comments">
        <p class="govuk-body govuk-body__preserve-whitespace">${additionalInformationValues.additionalComments()}</p>
      </@fdsSummaryList.summaryListRowNoAction>
    </#if>
    <@fdsSummaryList.summaryListRowNoAction keyText="Do you have any additional documents?">
      ${additionalInformationValues.providingAdditionalDocuments()}
    </@fdsSummaryList.summaryListRowNoAction>

    <#if additionalInformationValues.providingAdditionalDocuments() == "Yes">
      <@fileUploadSummary.fileUploadSummary
        filesUploaded=additionalInformationValues.additionalDocuments()
        fieldName="Additional documents"/>
    </#if>
  </@fdsSummaryList.summaryListCard>
</#macro>
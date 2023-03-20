<#include '../layout/layout.ftl'>
<#import 'fileUploadSummaryCard.ftl' as fileUploadSummary>

<#-- @ftlvariable name="regulatorManagedInformationValues" type="uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryView" -->
<#-- @ftlvariable name="customerBranding" type="uk.co.nstauthority.hydrocarbonmaturation.branding.CustomerConfigurationProperties" -->

<#macro regulatorManagedInformationSummaryCard regulatorManagedInformationValues>
  <@fdsSummaryList.summaryListCard headingText="${customerBranding.mnemonic()} managed information" summaryListId="regulatormanagedinformation">
    <@fdsSummaryList.summaryListRowNoAction keyText="Responsible team">
      ${regulatorManagedInformationValues.responsibleTeamType()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Geographical area">
      ${regulatorManagedInformationValues.geographicalArea()}
    </@fdsSummaryList.summaryListRowNoAction>
    <#if regulatorManagedInformationValues.isBeingRelinquished()>
      <@fileUploadSummary.fileUploadSummary
        filesUploaded=regulatorManagedInformationValues.relinquishmentReport()
        fieldName="Relinquishment report"/>
    </#if>
  </@fdsSummaryList.summaryListCard>
</#macro>
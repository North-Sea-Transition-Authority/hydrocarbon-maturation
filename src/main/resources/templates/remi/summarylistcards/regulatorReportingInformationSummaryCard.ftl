<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="regulatorManagedInformationValues" type="uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryView" -->
<#-- @ftlvariable name="resourceGroupsValues" type="uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupSummaryView" -->
<#-- @ftlvariable name="customerBranding" type="uk.co.nstauthority.hydrocarbonmaturation.branding.CustomerConfigurationProperties" -->
<#-- @ftlvariable name="resourceType" type="uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType" -->

<#macro regulatorReportingInformationSummaryCard regulatorReportingInformationValues>
    <@fdsSummaryList.summaryListCard headingText="${customerBranding.mnemonic()} reporting information" summaryListId="regulatorreportinginformation">
        <@fdsSummaryList.summaryListRowNoAction keyText="${customerBranding.mnemonic()} resource name">
            ${regulatorReportingInformationValues.resourceName()}
        </@fdsSummaryList.summaryListRowNoAction>
        <@fdsSummaryList.summaryListRowNoAction keyText="${customerBranding.mnemonic()} hydrocarbon case">
            ${regulatorReportingInformationValues.hydrocarbonCase()}
        </@fdsSummaryList.summaryListRowNoAction>
        <@fdsSummaryList.summaryListRowNoAction keyText="${customerBranding.mnemonic()} opportunity type">
            ${regulatorReportingInformationValues.opportunityType()}
        </@fdsSummaryList.summaryListRowNoAction>
        <@fdsSummaryList.summaryListRowNoAction keyText="${customerBranding.mnemonic()} principal well">
            ${regulatorReportingInformationValues.wellName()}
        </@fdsSummaryList.summaryListRowNoAction>
    </@fdsSummaryList.summaryListCard>
</#macro>
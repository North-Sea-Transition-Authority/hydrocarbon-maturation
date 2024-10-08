<#include '../layout/layout.ftl'>
<#import 'fileUploadSummaryRows.ftl' as fileUploadSummary>

<#-- @ftlvariable name="regulatorManagedInformationValues" type="uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationSummaryView" -->
<#-- @ftlvariable name="resourceGroupsValues" type="uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceGroupSummaryView" -->
<#-- @ftlvariable name="customerBranding" type="uk.co.nstauthority.hydrocarbonmaturation.branding.CustomerConfigurationProperties" -->

<#macro regulatorManagedInformationSummaryCard regulatorManagedInformationValues resourceGroupsValues canSeePostFieldLink=false>
  <@fdsSummaryList.summaryListCard headingText="${customerBranding.mnemonic()} managed information" summaryListId="regulatormanagedinformation">
    <@fdsSummaryList.summaryListRowNoAction keyText="Responsible team">
      ${regulatorManagedInformationValues.responsibleTeamType()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Geographical area">
      ${regulatorManagedInformationValues.geographicalArea()}
    </@fdsSummaryList.summaryListRowNoAction>
    <#if regulatorManagedInformationValues.isBeingRelinquished()>
      <@fileUploadSummary.fileUploadSummary filesUploaded=regulatorManagedInformationValues.relinquishmentReport()/>
    </#if>
    <@fdsSummaryList.summaryListRowNoAction keyText="${resourceGroupsValues.resourceType().getDisplayName()} group preferred view">
      ${resourceGroupsValues.isPreferredView()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="${resourceGroupsValues.resourceType().getPluralName()?capitalize} in group">
      <#list resourceGroupsValues.resourcesInGroup() as resourceInGroup>
        <div>
          <#if resourceInGroup.disableLink()>
            ${resourceInGroup.resourceName()}
          <#else>
            <@fdsAction.link linkText=resourceInGroup.resourceName() linkUrl=springUrl(resourceInGroup.viewSorUrl())/>
          </#if>
          ${resourceInGroup.preferredView()}
        </div>
      </#list>
    </@fdsSummaryList.summaryListRowNoAction>
    <#if regulatorManagedInformationValues.linkedToFieldName()?has_content && canSeePostFieldLink>
      <@fdsSummaryList.summaryListRowNoAction keyText="Created from field">
        <@fdsAction.link linkText=regulatorManagedInformationValues.linkedToFieldName()
        linkUrl=springUrl(regulatorManagedInformationValues.linkedToFieldUrl())/>
      </@fdsSummaryList.summaryListRowNoAction>
    </#if>
  </@fdsSummaryList.summaryListCard>
</#macro>
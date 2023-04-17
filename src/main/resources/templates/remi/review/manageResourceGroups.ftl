<#include '../layout/layout.ftl'>
<#import '_relatedResources.ftl' as relatedResources>
<#-- @ftlvariable name="pageTitle" type="String" -->
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="resourceGroupsEndpoint" type="String" -->
<#-- @ftlvariable name="resourcesInGroup" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup.ResourceItemSummaryView>" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
errorItems=errorList
breadcrumbsMap=breadcrumbsMap>
  <#if resourcesInGroup?size != 0>
    <@fdsInsetText.insetText>
      This ${resourceType} is currently in a group with the following ${resourceTypePlural}:
      <ul>
        <#list resourcesInGroup as resourceInGroup>
          <li>
            ${resourceInGroup.resourceName()} ${resourceInGroup.preferredView()}
          </li>
        </#list>
      </ul>
    </@fdsInsetText.insetText>
  </#if>
  <@fdsForm.htmlForm>
    <@relatedResources.relatedResources
      resourceType=resourceType
      resourceGroupsEndpoint=resourceGroupsEndpoint
      relatedSubmissionDetail=relatedSubmissionDetail/>
    <@fdsAction.submitButtons primaryButtonText="Save and complete"
      secondaryLinkText="Cancel"
      linkSecondaryAction=true
      linkSecondaryActionUrl=springUrl(cancelUrl)/>
  </@fdsForm.htmlForm>
</@defaultPage>
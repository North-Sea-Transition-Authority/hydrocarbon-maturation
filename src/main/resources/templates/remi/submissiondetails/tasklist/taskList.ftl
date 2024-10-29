<#include '../../layout/layout.ftl'>
<#import '../../tasklist/standardTaskList.ftl' as taskList>
<#import '../../review/updateSummaryDetails.ftl' as updateSummaryDetails>
<#import '../../review/_submissionBlockedBanner.ftl' as banner>

<#-- @ftlvariable name="taskListSections" type="java.util.List<uk.co.nstauthority.offshoresafetydirective.tasklist.TaskListSectionView>" -->
<#-- @ftlvariable name="serviceBranding" type="uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="submissionTypeName" type="String" -->
<#-- @ftlvariable name="deleteSubmissionUrl" type="String" -->

<@defaultPage
  htmlTitle=submissionTypeName
  pageHeading=submissionTypeName
  pageSize=PageSize.TWO_THIRDS_COLUMN
  breadcrumbsMap=breadcrumbsMap
  caption=resourceName>

  <#if submissionBlocked!false>
    <@banner.submissionBlockedNotificationBanner/>
  </#if>

  <#if isSubmissionUpdate>
    <@updateSummaryDetails.updateDetails submissionUpdateReason=submissionUpdateReason/>
  </#if>

  <@fdsAction.link
    linkText="${deleteLinkText}"
    linkUrl=springUrl(deleteSubmissionUrl)
    linkClass="govuk-button govuk-button--secondary"
    role=true/>

  <@taskList.standardTaskList taskListSections=taskListSections />
</@defaultPage>
<#include '../../layout/layout.ftl'>
<#import '../../tasklist/standardTaskList.ftl' as taskList>

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
  caption=resourceName
>

  <@fdsAction.link
    linkText="Delete submission"
    linkUrl=springUrl(deleteSubmissionUrl)
    linkClass="govuk-button govuk-button--secondary"
    role=true/>


  <@taskList.standardTaskList taskListSections=taskListSections />
</@defaultPage>
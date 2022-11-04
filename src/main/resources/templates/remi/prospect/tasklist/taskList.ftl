<#include '../../layout/layout.ftl'>
<#import '../../tasklist/standardTaskList.ftl' as taskList>

<#-- @ftlvariable name="taskListSections" type="java.util.List<uk.co.nstauthority.offshoresafetydirective.tasklist.TaskListSectionView>" -->
<#-- @ftlvariable name="serviceBranding" type="uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="submissionTypeName" type="String" -->

<@defaultPage
  htmlTitle=submissionTypeName
  pageHeading=submissionTypeName
  pageSize=PageSize.TWO_THIRDS_COLUMN
  breadcrumbsMap=breadcrumbsMap
>
  <@taskList.standardTaskList taskListSections=taskListSections />
</@defaultPage>
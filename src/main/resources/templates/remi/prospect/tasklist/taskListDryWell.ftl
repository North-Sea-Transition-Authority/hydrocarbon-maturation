<#include '../../layout/layout.ftl'>

<#-- @ftlvariable name="taskListSections" type="java.util.List<uk.co.nstauthority.fieldconsents.tasklist.TaskListSection>" -->
<#-- @ftlvariable name="serviceBranding" type="uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="submissionTypeName" type="String" -->

<#assign pageTitle = "Post-drill dry hole submission" />

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  breadcrumbsMap=breadcrumbsMap
>

  <#list taskListSections as section>
    <@fdsTaskList.taskList>
      <@fdsTaskList.taskListSection
        sectionNumber="${section?index + 1}"
        sectionHeadingText=section.displayName()>

        <#list section.items() as item>
          <@fdsTaskList.taskListItem
            itemText=item.displayName()
            itemUrl=springUrl(item.actionUrl())
            showTag=item.label()?has_content
            completed=item.label()?has_content && item.label().name() == 'COMPLETE'
            useNotCompletedLabels=true />
        </#list>
      </@fdsTaskList.taskListSection>
    </@fdsTaskList.taskList>
  </#list>
</@defaultPage>
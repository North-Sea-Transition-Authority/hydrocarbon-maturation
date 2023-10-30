<#include '../../layout/layout.ftl'>
<#import '../../tasklist/standardTaskList.ftl' as taskList>

<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
breadcrumbsMap=breadcrumbsMap
singleErrorMessage=singleErrorMessage>
    <#list reservoirTaskListItems as reservoirs>
        <#assign actions>
            <@fdsSummaryList.summaryListCardActionList>
                <@fdsSummaryList.summaryListCardActionItem itemUrl=springUrl(reservoirs.reservoirDeleteUrl()) itemText="Remove" itemScreenReaderText="${reservoirs.reservoirName()}"/>
            </@fdsSummaryList.summaryListCardActionList>
        </#assign>
        <@fdsSummaryList.summaryListCard headingText="${reservoirs.reservoirName()}" cardActionsContent=actions summaryListId="reservoir${reservoirs?index}">
            <div class="remi-single-task-list">
                <@fdsTaskList.singleTaskList>
                    <#list reservoirs.taskListItems() as item>
                        <@fdsTaskList.taskListItem
                        itemText=item.displayName()
                        itemUrl=springUrl(item.actionUrl())
                        showTag=item.label()?has_content
                        completed=item.label()?has_content && item.label().name() == 'COMPLETE'
                        useNotCompletedLabels=true />
                    </#list>
                </@fdsTaskList.singleTaskList>
            </div>
        </@fdsSummaryList.summaryListCard>
    </#list>

    <@fdsForm.htmlForm>
        <@fdsRadio.radioGroup
            path="form.reservoirAdditionStatus"
            fieldsetHeadingClass="govuk-fieldset__legend--l"
            labelText="Do you want to add another reservoir?"
            hiddenContent=true>
            <#assign firstItem=true/>
            <#list reservoirAdditionStatusOptions as key, value>
              <@fdsRadio.radioItem path="form.reservoirAdditionStatus" itemMap={key : value} isFirstItem=firstItem/>
              <#assign firstItem=false/>
            </#list>
          </@fdsRadio.radioGroup>

          <@fdsAction.button buttonText="Save and continue" buttonValue=springUrl(actionUrl!"#")/>
    </@fdsForm.htmlForm>
</@defaultPage>
<#include '../layout/layout.ftl'>

<#macro emptySummary heading="">
    <#if heading?has_content>
        <@fdsSummaryList.summaryListCard
        headingText=heading
        headingSize="h3"
        summaryListId="summary-data-card-list">
            You haven't started this section yet
        </@fdsSummaryList.summaryListCard>
    <#else>
        <@_emptySummaryInset/>
    </#if>
</#macro>

<#macro _emptySummaryInset>
    <@fdsInsetText.insetText insetTextClass="govuk-!-margin-top-0">
        You haven't started this section yet
    </@fdsInsetText.insetText>
</#macro>

<#macro emptySummaryWithActions heading actionsMap>
    <#assign content>
        <@fdsSummaryList.summaryListCardActionList>
            <#list actionsMap as actionText, actionUrl>
                <@fdsSummaryList.summaryListCardActionItem itemUrl=springUrl(actionUrl) itemText=actionText itemScreenReaderText=""/>
            </#list>
        </@fdsSummaryList.summaryListCardActionList>
    </#assign>
    <@fdsSummaryList.summaryListCard
    headingText=heading
    headingSize="h3"
    summaryListId="summary-data-card-list"
    cardActionsContent=content>
        No information has been provided for this section
    </@fdsSummaryList.summaryListCard>
</#macro>
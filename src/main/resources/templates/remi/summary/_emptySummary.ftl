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
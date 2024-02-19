<#include '../layout/layout.ftl'>

<#macro fileSummary heading fileViews>
    <@fdsSummaryList.summaryListCard
    headingText=heading
    headingSize="h3"
    summaryListId="files-summary-card-list">
        <#list fileViews as fileView>
            <@fdsSummaryList.summaryListRow
            keyText=fileView.filename()
            actionText="Download"
            actionUrl=springUrl(fileView.downloadUrl())
            screenReaderActionText="Download ${fileView.filename()}">
                <p class="govuk-body">${fileView.description()!""}</p>
            </@fdsSummaryList.summaryListRow>
        </#list>
    </@fdsSummaryList.summaryListCard>
</#macro>
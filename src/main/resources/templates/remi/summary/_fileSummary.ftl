<#include '../layout/layout.ftl'>

<#macro fileSummary fileViews>
    <#list fileViews as fileView>
        <@fdsSummaryList.summaryListRow
        keyText=fileView.filename()
        actionText="Download"
        actionUrl=springUrl(fileView.downloadUrl())
        screenReaderActionText="Download ${fileView.filename()}">
            <p class="govuk-body">${fileView.description()!""}</p>
        </@fdsSummaryList.summaryListRow>
    </#list>
</#macro>
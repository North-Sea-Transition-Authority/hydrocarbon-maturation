<#include '../layout/layout.ftl'>

<#macro simpleSummary summaryDataView>
    <#list summaryDataView.keyValues() as keyValue>
      <@fdsSummaryList.summaryListRowNoAction keyText=keyValue.key()>
        ${(keyValue.value())!""}
      </@fdsSummaryList.summaryListRowNoAction>
    </#list>
</#macro>

<#macro simpleSummaryCard summaryDataView summaryHeading>
  <@fdsSummaryList.summaryListCard
  headingText=summaryHeading
  headingSize="h3"
  summaryListId="summary-data-card-list">
    <@simpleSummary summaryDataView=summaryDataView/>
    <#nested/>
  </@fdsSummaryList.summaryListCard>
</#macro>
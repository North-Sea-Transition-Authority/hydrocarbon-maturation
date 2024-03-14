<#include '../layout/layout.ftl'>

<#macro simpleSummary summaryDataView summaryHeading>
  <@fdsSummaryList.summaryListCard
    headingText=summaryHeading
    headingSize="h3"
    summaryListId="summary-data-card-list">
    <#list summaryDataView.keyValues() as keyValue>
      <@fdsSummaryList.summaryListRowNoAction keyText=keyValue.key()>
        ${(keyValue.value())!""}
      </@fdsSummaryList.summaryListRowNoAction>
    </#list>
  </@fdsSummaryList.summaryListCard>
</#macro>

<#macro simpleSummaryWithActions summaryDataViewWithActions summaryHeading>
  <#assign content>
    <@fdsSummaryList.summaryListCardActionList>
      <#list summaryDataViewWithActions.actionsMap() as actionText, actionUrl>
        <@fdsSummaryList.summaryListCardActionItem itemUrl=springUrl(actionUrl) itemText=actionText itemScreenReaderText=""/>
      </#list>
    </@fdsSummaryList.summaryListCardActionList>
  </#assign>

  <@fdsSummaryList.summaryListCard
  headingText=summaryHeading
  headingSize="h3"
  summaryListId="summary-data-card-list"
  cardActionsContent=content>
    <#list summaryDataViewWithActions.keyValues() as keyValue>
      <@fdsSummaryList.summaryListRowNoAction keyText=keyValue.key()>
        ${(keyValue.value())!""}
      </@fdsSummaryList.summaryListRowNoAction>
    </#list>
  </@fdsSummaryList.summaryListCard>
</#macro>
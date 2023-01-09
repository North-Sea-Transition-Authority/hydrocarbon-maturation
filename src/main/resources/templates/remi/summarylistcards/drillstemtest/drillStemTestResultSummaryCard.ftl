<#include '../../layout/layout.ftl'>

<#macro drillStemTestResult drillStemTestResultValues withResultNumber=true>
  <#if drillStemTestResultValues.summaryMode() == "EDIT">
    <#assign headingText = "Drill stem test result ${drillStemTestResultValues.resultNumber()}"/>
    <#assign content>
      <@fdsSummaryList.summaryListCardActionList>
        <@fdsSummaryList.summaryListCardActionItem itemUrl=springUrl(drillStemTestResultValues.changeUrl()) itemText="Change" itemScreenReaderText="something"/>
        <@fdsSummaryList.summaryListCardActionItem itemUrl=springUrl(drillStemTestResultValues.deleteUrl()) itemText="Remove" itemScreenReaderText="something"/>
      </@fdsSummaryList.summaryListCardActionList>
    </#assign>
  <#else>
    <#assign headingText = "Drill stem test result"/>
    <#assign content = ""/>
  </#if>

  <@fdsSummaryList.summaryListCard
    headingText=headingText
    summaryListId="drillstemtestresult"
    cardActionsContent=content>
    <@fdsSummaryList.summaryListRowNoAction keyText="Formation name">
      ${drillStemTestResultValues.formationName()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Top depth">
      ${drillStemTestResultValues.topDepth()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Bottom depth">
      ${drillStemTestResultValues.bottomDepth()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Bottomhole flowing pressure">
      ${drillStemTestResultValues.bhfp()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Wellhead flowing pressure">
      ${drillStemTestResultValues.whfp()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Flow duration">
      ${drillStemTestResultValues.flowDuration()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Gas flowrate">
      ${drillStemTestResultValues.gasFlowrate()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Oil flowrate">
      ${drillStemTestResultValues.oilFlowrate()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Water flowrate">
      ${drillStemTestResultValues.waterFlowrate()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Comments and interpretation">
      ${drillStemTestResultValues.commentsAndInterpretation()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>
</#macro>
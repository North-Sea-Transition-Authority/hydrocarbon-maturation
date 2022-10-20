<#include '../layout/layout.ftl'>
<#-- @ftlvariable name="generalDetailValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView" -->

<#macro generalDetailCard generalDetailValues>
  <@fdsSummaryList.summaryListCard headingText="General details" summaryListId="generaldetails">
    <@fdsSummaryList.summaryListRowNoAction keyText="Prospect name">
      ${generalDetailValues.prospectName()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Onshore or offshore">
      ${generalDetailValues.onshoreOffshore()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Wholly located within UKCS">
      ${generalDetailValues.prospectLocatedInUkcs()}
    </@fdsSummaryList.summaryListRowNoAction>
    <#if generalDetailValues.prospectLocatedInUkcs() == "No">
      <@fdsSummaryList.summaryListRowNoAction keyText="Percentage located in UKCS">
        ${generalDetailValues.percentageLocatedInUkcs()}
      </@fdsSummaryList.summaryListRowNoAction>
    </#if>
    <@fdsSummaryList.summaryListRowNoAction keyText="Information source">
      ${generalDetailValues.informationSource()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>
</#macro>
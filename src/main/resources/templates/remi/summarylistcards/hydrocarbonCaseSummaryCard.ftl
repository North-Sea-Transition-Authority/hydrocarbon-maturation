<#include '../layout/layout.ftl'>
<#-- @ftlvariable name="hydrocarbonCaseValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseSummaryView" -->

<#macro hydrocarbonCaseCard hydrocarbonCaseValues>
  <@fdsSummaryList.summaryListCard headingText="Hydrocarbon case" summaryListId="hydrocarboncase">
    <@fdsSummaryList.summaryListRowNoAction keyText="Oil only">
      ${hydrocarbonCaseValues.oilOnly()}
    </@fdsSummaryList.summaryListRowNoAction>
    <#if hydrocarbonCaseValues.oilOnly() == "Yes">
      <@fdsSummaryList.summaryListRowNoAction keyText="Oil only chance of occuring">
        ${hydrocarbonCaseValues.oilOnlyProbability()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Oil only comments">
        <p class="govuk-body govuk-body__preserve-whitespace">${hydrocarbonCaseValues.oilOnlyComments()}</p>
      </@fdsSummaryList.summaryListRowNoAction>
    </#if>
    <@fdsSummaryList.summaryListRowNoAction keyText="Gas only">
      ${hydrocarbonCaseValues.gasOnly()}
    </@fdsSummaryList.summaryListRowNoAction>
    <#if hydrocarbonCaseValues.gasOnly() == "Yes">
      <@fdsSummaryList.summaryListRowNoAction keyText="Gas only chance of occuring">
        ${hydrocarbonCaseValues.gasOnlyProbability()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Gas only comments">
        <p class="govuk-body govuk-body__preserve-whitespace">${hydrocarbonCaseValues.gasOnlyComments()}</p>
      </@fdsSummaryList.summaryListRowNoAction>
    </#if>
      <@fdsSummaryList.summaryListRowNoAction keyText="Oil and gas">
        ${hydrocarbonCaseValues.oilAndGas()}
      </@fdsSummaryList.summaryListRowNoAction>
    <#if hydrocarbonCaseValues.oilAndGas() == "Yes">
      <@fdsSummaryList.summaryListRowNoAction keyText="Oil and gas chance of occuring">
        ${hydrocarbonCaseValues.oilAndGasProbability()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Oil and gas comments">
        <p class="govuk-body govuk-body__preserve-whitespace">${hydrocarbonCaseValues.oilAndGasComments()}</p>
      </@fdsSummaryList.summaryListRowNoAction>
    </#if>

  </@fdsSummaryList.summaryListCard>
</#macro>
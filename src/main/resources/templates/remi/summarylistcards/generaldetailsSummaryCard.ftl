<#include '../layout/layout.ftl'>
<#-- @ftlvariable name="generalDetailValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView" -->

<#macro generalDetailCard generalDetailValues>
  <@fdsSummaryList.summaryListCard headingText="General details" summaryListId="generaldetails">
    <@fdsSummaryList.summaryListRowNoAction keyText="${generalDetailValues.resourceType()?capitalize} name">
      ${generalDetailValues.resourceName()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Responsible organisation">
      ${generalDetailValues.organisationNameAndNumber()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Onshore or offshore">
      ${generalDetailValues.onshoreOffshore()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Wholly located within UKCS">
      ${generalDetailValues.resourceLocatedInUkcs()}
    </@fdsSummaryList.summaryListRowNoAction>
    <#if generalDetailValues.resourceLocatedInUkcs() == "No">
      <@fdsSummaryList.summaryListRowNoAction keyText="Percentage located in UKCS">
        ${generalDetailValues.percentageLocatedInUkcs()}
      </@fdsSummaryList.summaryListRowNoAction>
    </#if>
    <@fdsSummaryList.summaryListRowNoAction keyText="Information source">
      ${generalDetailValues.informationSource()}
    </@fdsSummaryList.summaryListRowNoAction>
    <#if generalDetailValues.isLicenceApplicationInfoSource()>
      <@fdsSummaryList.summaryListRowNoAction keyText="Licence application">
        ${generalDetailValues.licenceApplication()}
      </@fdsSummaryList.summaryListRowNoAction>
    </#if>
    <@fdsSummaryList.summaryListRowNoAction keyText="Quadrant and blocks">
        <#list generalDetailValues.quadAndBlockDisplayList() as quadAndBlock>
          <div>${quadAndBlock}</div>
        </#list>
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>
</#macro>
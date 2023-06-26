<#include '../layout/layout.ftl'>
<#-- @ftlvariable name="generalDetailValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView" -->

<#macro generalDetailCard generalDetailValues isOffshore>
  <@fdsSummaryList.summaryListCard headingText="General details" summaryListId="generaldetails">
    <#if generalDetailValues.submissionType() != "RELINQUISHMENT_PROSPECT"
      && generalDetailValues.submissionType() != "RELINQUISHMENT_DISCOVERY" >
      <@fdsSummaryList.summaryListRowNoAction keyText="Is this ${generalDetailValues.resourceType()} on a licence?">
        ${generalDetailValues.isResourceOnALicence()}
      </@fdsSummaryList.summaryListRowNoAction>
      <#if generalDetailValues.showLicences()>
        <@fdsSummaryList.summaryListRowNoAction keyText="Licences">
          <#list generalDetailValues.licenceDisplayList() as licence>
            <div>${licence}</div>
          </#list>
        </@fdsSummaryList.summaryListRowNoAction>
      </#if>
    </#if>
    <#if isOffshore>
      <@fdsSummaryList.summaryListRowNoAction keyText="Wholly located within UKCS">
        ${generalDetailValues.resourceLocatedInUkcs()}
      </@fdsSummaryList.summaryListRowNoAction>
      <#if generalDetailValues.resourceLocatedInUkcs() == "No">
        <@fdsSummaryList.summaryListRowNoAction keyText="Percentage located in UKCS">
          ${generalDetailValues.percentageLocatedInUkcs()}
        </@fdsSummaryList.summaryListRowNoAction>
      </#if>
    <#else>
      <@fdsSummaryList.summaryListRowNoAction keyText="Wholly located within England">
        ${generalDetailValues.resourceLocatedInEngland()}
      </@fdsSummaryList.summaryListRowNoAction>
      <#if generalDetailValues.resourceLocatedInEngland() == "No">
        <@fdsSummaryList.summaryListRowNoAction keyText="Percentage located in England">
          ${generalDetailValues.percentageLocatedInEngland()}
        </@fdsSummaryList.summaryListRowNoAction>
      </#if>
    </#if>
    <#if generalDetailValues.submissionType() != "POST_DRILL_DRY_HOLE">
      <@fdsSummaryList.summaryListRowNoAction keyText="Information source">
        ${generalDetailValues.informationSource()}
      </@fdsSummaryList.summaryListRowNoAction>
      <#if generalDetailValues.isLicenceApplicationInfoSource()>
        <@fdsSummaryList.summaryListRowNoAction keyText="Licence application">
          ${generalDetailValues.licenceApplication()}
        </@fdsSummaryList.summaryListRowNoAction>
      </#if>
    </#if>
    <@fdsSummaryList.summaryListRowNoAction keyText="Quadrant and blocks">
      <#list generalDetailValues.quadAndBlockDisplayList() as quadAndBlock>
        <div>${quadAndBlock}</div>
      </#list>
    </@fdsSummaryList.summaryListRowNoAction>
    <#if generalDetailValues.submissionType() == "RELINQUISHMENT_PROSPECT"
      || generalDetailValues.submissionType() == "RELINQUISHMENT_DISCOVERY">
      <@fdsSummaryList.summaryListRowNoAction keyText="Date of relinquishment">
        ${generalDetailValues.relinquishmentDate()}
      </@fdsSummaryList.summaryListRowNoAction>
    </#if>
  </@fdsSummaryList.summaryListCard>
</#macro>
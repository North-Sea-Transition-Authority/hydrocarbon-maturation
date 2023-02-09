<#include '../layout/layout.ftl'>
<#-- @ftlvariable name="resourceDetailValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailSummaryView" -->

<#macro resourceDetailCard resourceDetailValues>
  <@fdsSummaryList.summaryListCard headingText="Resource details" summaryListId="resourcedetails">
    <@fdsSummaryList.summaryListRowNoAction keyText="${resourceDetailValues.resourceType()?capitalize} name">
      ${resourceDetailValues.resourceName()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Responsible organisation">
      ${resourceDetailValues.organisationNameAndNumber()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Onshore or offshore">
      ${resourceDetailValues.onshoreOffshore()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>
</#macro>
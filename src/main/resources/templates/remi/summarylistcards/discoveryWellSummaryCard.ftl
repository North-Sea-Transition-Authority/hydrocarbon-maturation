<#include '../layout/layout.ftl'>
<#-- @ftlvariable name="discoveryWellValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsSummaryView" -->

<#macro discoveryWellDetailCard discoveryWellValues>
  <@fdsSummaryList.summaryListCard headingText="Discovery well details" summaryListId="discoverywelldetails">
    <@fdsSummaryList.summaryListRowNoAction keyText="Principal well">
      ${discoveryWellValues.principalWell()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Top reservoir latitude">
      ${discoveryWellValues.latitudeDegrees()}
      ${discoveryWellValues.latitudeMinutes()}
      ${discoveryWellValues.latitudeSeconds()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Top reservoir longitude">
      ${discoveryWellValues.longitudeDegrees()}
      ${discoveryWellValues.longitudeMinutes()}
      ${discoveryWellValues.longitudeSeconds()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Top reservoir longitude hemisphere">
      ${discoveryWellValues.longitudeHemisphere()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Top reservoir datum">
      ${discoveryWellValues.datum()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Total depth formation">
      ${discoveryWellValues.totalDepthFormation()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Total depth formation comments">
      <p class="govuk-body govuk-body__preserve-whitespace">${discoveryWellValues.totalDepthFormationComments()}</p>
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Data acquisition programme">
      <p class="govuk-body govuk-body__preserve-whitespace">${discoveryWellValues.dataAcquisitionProgramme()}</p>
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Geophysical update commentary">
      <p class="govuk-body govuk-body__preserve-whitespace">${discoveryWellValues.geophysicalUpdate()}</p>
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Hydrocarbons discovered">
      ${discoveryWellValues.hydrocarbonsDiscovered()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Discovery status">
      ${discoveryWellValues.discoveryStatus()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Comments regarding chance of commerciality">
      <p class="govuk-body govuk-body__preserve-whitespace">${discoveryWellValues.commentsRegardingCommerciality()}</p>
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Total well cost">
      ${discoveryWellValues.totalWellCost()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Success-related costs">
      ${discoveryWellValues.successRelatedCosts()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>
</#macro>
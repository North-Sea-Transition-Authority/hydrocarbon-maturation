<#include '../layout/layout.ftl'>
<#-- @ftlvariable name="dryWellValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSummaryView" -->

<#macro dryWellDetailCard dryWellValues>
  <@fdsSummaryList.summaryListCard headingText="Dry well details" summaryListId="drywelldetails">
    <@fdsSummaryList.summaryListRowNoAction keyText="Principal well">
      ${dryWellValues.principalWell()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Top reservoir latitude">
      ${dryWellValues.latitudeDegrees()}
      ${dryWellValues.latitudeMinutes()}
      ${dryWellValues.latitudeSeconds()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Top reservoir longitude">
      ${dryWellValues.longitudeDegrees()}
      ${dryWellValues.longitudeMinutes()}
      ${dryWellValues.longitudeSeconds()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Top reservoir longitude hemisphere">
      ${dryWellValues.longitudeHemisphere()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Top reservoir datum">
      ${dryWellValues.datum()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Total depth formation">
      ${dryWellValues.totalDepthFormation()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Total depth formation comments">
      <p class="govuk-body govuk-body__preserve-whitespace">${dryWellValues.totalDepthFormationComments()}</p>
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Data acquisition programme">
      <p class="govuk-body govuk-body__preserve-whitespace">${dryWellValues.dataAcquisitionProgramme()}</p>
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Geophysical update commentary">
      <p class="govuk-body govuk-body__preserve-whitespace">${dryWellValues.geophysicalUpdate()}</p>
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Dry hole shows type">
      ${dryWellValues.dryHoleType()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Dry hole shows description">
      <p class="govuk-body govuk-body__preserve-whitespace">${dryWellValues.dryHoleDescription()}</p>
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Dry hole scope for remaining potential">
      <p class="govuk-body govuk-body__preserve-whitespace">${dryWellValues.dryHoleScope()}</p>
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Source failure category for post-well analysis">
      ${dryWellValues.sourceFailureCategory()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Reservoir failure category for post-well analysis">
      ${dryWellValues.reservoirFailureCategory()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Seal failure category for post-well analysis">
      ${dryWellValues.sealFailureCategory()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Trap failure category for post-well analysis">
      ${dryWellValues.trapFailureCategory()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Migration failure category for post-well analysis">
      ${dryWellValues.migrationFailureCategory()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Dry hole comments">
      <p class="govuk-body govuk-body__preserve-whitespace">${dryWellValues.dryHoleComments()}</p>
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Dry hole cost">
      ${dryWellValues.dryHoleCost()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>
</#macro>
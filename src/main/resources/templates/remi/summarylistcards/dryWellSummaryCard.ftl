<#include '../layout/layout.ftl'>
<#-- @ftlvariable name="dryWellValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSummaryView" -->

<#macro dryWellDetailCard dryWellValues>
  <@fdsSummaryList.summaryListCard headingText="Dry well details" summaryListId="drywelldetails">
    <@fdsSummaryList.summaryListRowNoAction keyText="Principal well">
      ${dryWellValues.principalWell()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Latitude ">
      ${dryWellValues.latitudeDegrees()}
      ${dryWellValues.latitudeMinutes()}
      ${dryWellValues.latitudeSeconds()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Longitude">
      ${dryWellValues.longitudeDegrees()}
      ${dryWellValues.longitudeMinutes()}
      ${dryWellValues.longitudeSeconds()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Longitude hemisphere">
      ${dryWellValues.longitudeHemisphere()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Top reservoir datum">
      ${dryWellValues.datum()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Total depth formation">
      ${dryWellValues.totalDepthFormation()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Total depth formation comments">
      ${dryWellValues.totalDepthFormationComments()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Data acquisition programme">
      ${dryWellValues.dataAcquisitionProgramme()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Geophysical update commentary">
      ${dryWellValues.geophysicalUpdate()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Dry hole shows type">
      ${dryWellValues.dryHoleType()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Dry hole shows description">
      ${dryWellValues.dryHoleDescription()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Dry hole scope for remaining potential">
      ${dryWellValues.dryHoleScope()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Total depth formation">
      ${dryWellValues.dryHoleComments()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Dry hole cost">
      ${dryWellValues.dryHoleCost()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>
</#macro>
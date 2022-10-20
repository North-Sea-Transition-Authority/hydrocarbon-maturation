<#include '../layout/layout.ftl'>
<#-- @ftlvariable name="sourceAndSealValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView" -->

<#macro sourceAndSealCard sourceAndSealValues>
  <@fdsSummaryList.summaryListCard headingText="Source and seal" summaryListId="sourceandseal">
    <@fdsSummaryList.summaryListRowNoAction keyText="Source formation name">
      ${sourceAndSealValues.sourceFormationName()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Source age stage or series">
      ${sourceAndSealValues.sourceAgeStage()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Source lithology">
      ${sourceAndSealValues.sourceLithology()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Top seal formation name">
      ${sourceAndSealValues.sealFormationName()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Top seal age stage or series">
      ${sourceAndSealValues.sealAgeStage()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Top seal lithology">
      ${sourceAndSealValues.sealLithology()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>
</#macro>
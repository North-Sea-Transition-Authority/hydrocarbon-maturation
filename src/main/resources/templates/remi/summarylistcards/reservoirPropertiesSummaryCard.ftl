<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="reservoirPropertiesValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView" -->

<#macro reservoirPropertiesSummaryCard reservoirPropertiesValues>
  <@fdsSummaryList.summaryListCard headingText="Reservoir properties" summaryListId="reservoirproperties">
    <@fdsSummaryList.summaryListRowNoAction keyText="Formation name">
      ${reservoirPropertiesValues.formationName()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Age stage or series">
      ${reservoirPropertiesValues.ageStageOrSeries()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Lithology">
      ${reservoirPropertiesValues.lithology()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>
  <@probabilityDistribution.probabilityDistributionTable
    probabilityDistributions=reservoirPropertiesValues.probabilityDistributionSummaryViews()/>
</#macro>
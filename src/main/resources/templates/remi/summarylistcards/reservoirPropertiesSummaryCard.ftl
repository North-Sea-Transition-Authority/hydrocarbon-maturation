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
    <#if reservoirPropertiesValues.submissionType() == "POST_DRILL_DRY_HOLE"
      || reservoirPropertiesValues.submissionType() == "CREATE_DISCOVERY"
      || reservoirPropertiesValues.submissionType() == "UPDATE_DISCOVERY">
      <@fdsSummaryList.summaryListRowNoAction keyText="Reservoir facies">
        ${reservoirPropertiesValues.reservoirFacies()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Reservoir connectivity">
        ${reservoirPropertiesValues.reservoirConnectivity()}
      </@fdsSummaryList.summaryListRowNoAction>
    </#if>
    </@fdsSummaryList.summaryListCard>
    <@fdsSummaryList.summaryListCard headingText="Reservoir property distributions" summaryListId="reservoirpropertydistributions">
      <@probabilityDistribution.probabilityDistributionTable probabilityDistributions=reservoirPropertiesValues.probabilityDistributionSummaryViews()/>
    </@fdsSummaryList.summaryListCard>
    <#if (reservoirPropertiesValues.submissionType() == "CREATE_DISCOVERY"
    || reservoirPropertiesValues.submissionType() == "UPDATE_DISCOVERY")
    && reservoirPropertiesValues.listOfDepthToContactDistributions()?has_content>
      <@fdsSummaryList.summaryListCard headingText="Depth to hydrocarbon contact" summaryListId="reservoirpropertydistributions">
        <@probabilityDistribution.minMaxMostLikelyTable
          probabilityDistributions=reservoirPropertiesValues.listOfDepthToContactDistributions()/>
      </@fdsSummaryList.summaryListCard>
    </#if>
</#macro>
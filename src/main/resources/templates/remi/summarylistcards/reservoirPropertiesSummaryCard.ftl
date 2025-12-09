<#include '../layout/layout.ftl'>
<#import '../summary/_probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="reservoirPropertiesValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView" -->

<#macro reservoirPropertiesSummaryCard reservoirPropertiesValues>
  <@fdsSummaryList.summaryListCard headingText="Reservoir properties" summaryListId="reservoirproperties">
      <@fdsSummaryList.summaryListRowNoAction keyText="Reservoir age">
          ${reservoirPropertiesValues.ageStageOrSeries()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Group name">
          ${reservoirPropertiesValues.groupName()}
      </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Formation name">
      ${reservoirPropertiesValues.formationName()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Member or sub unit">
        ${reservoirPropertiesValues.subUnit()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Lithology">
      ${reservoirPropertiesValues.lithology()}
    </@fdsSummaryList.summaryListRowNoAction>
    <#if reservoirPropertiesValues.submissionType() == "POST_DRILL_DRY_HOLE"
      || reservoirPropertiesValues.submissionType() == "CREATE_DISCOVERY"
      || reservoirPropertiesValues.submissionType() == "UPDATE_DISCOVERY"
      || reservoirPropertiesValues.submissionType() == "CREATE_DISCOVERY_FROM_PROSPECT"
      || reservoirPropertiesValues.submissionType() == "RELINQUISHMENT_DISCOVERY">
      <@fdsSummaryList.summaryListRowNoAction keyText="Reservoir facies">
        <p class="govuk-body govuk-body__preserve-whitespace">${reservoirPropertiesValues.reservoirFacies()}</p>
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Reservoir connectivity">
        <p class="govuk-body govuk-body__preserve-whitespace">${reservoirPropertiesValues.reservoirConnectivity()}</p>
      </@fdsSummaryList.summaryListRowNoAction>
    </#if>
    </@fdsSummaryList.summaryListCard>
    <@probabilityDistribution.probabilityDistributionTable
      heading="Reservoir property distributions"
      summaryCardId="reservoir-property-distributions-table"
      tableView=reservoirPropertiesValues.tableView()/>
    <#if (reservoirPropertiesValues.submissionType() == "CREATE_DISCOVERY"
    || reservoirPropertiesValues.submissionType() == "UPDATE_DISCOVERY"
    || reservoirPropertiesValues.submissionType() == "CREATE_DISCOVERY_FROM_PROSPECT"
    || reservoirPropertiesValues.submissionType() == "RELINQUISHMENT_DISCOVERY")
    && reservoirPropertiesValues.listOfDepthToContactDistributions()?has_content>
      <@probabilityDistribution.probabilityDistributionTable
        heading="Depth to hydrocarbon contact"
        summaryCardId="reservoir-property-depth-to-hydrocarbon-distributions-table"
        withDistributionType=false
        tableView=reservoirPropertiesValues.depthToContactDistributions()/>
    </#if>
</#macro>
<#include '../layout/layout.ftl'>
<#-- @ftlvariable name="generalFluidValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySummaryView" -->

<#macro generalFluidPropertiesCard generalFluidValues>
  <@fdsSummaryList.summaryListCard headingText="General properties" summaryListId="generalproperties">
    <@fdsSummaryList.summaryListRowNoAction keyText="Datum depth">
      ${generalFluidValues.datumDepth()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Temperature at datum depth">
      ${generalFluidValues.temperatureAtDatumDepth()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Pressure at datum depth">
      ${generalFluidValues.pressureAtDatumDepth()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Saturation pressure at datum depth">
      ${generalFluidValues.saturationPressureAtDatumDepth()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Water salinity">
      ${generalFluidValues.waterSalinity()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>
</#macro>
<#include '../layout/layout.ftl'>
<#-- @ftlvariable name="oilFluidValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySummaryView" -->

<#macro oilFluidPropertiesCard oilFluidValues>
  <@fdsSummaryList.summaryListCard headingText="Oil properties" summaryListId="oilproperties">
    <@fdsSummaryList.summaryListRowNoAction keyText="Oil Gravity (°API)">
      ${oilFluidValues.oilGravity()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Oil Viscosity (cP)">
      ${oilFluidValues.oilViscosity()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Oil Sulphur (ppm)">
      ${oilFluidValues.oilSulphur()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Oil wax (weight%)">
      ${oilFluidValues.oilWax()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Oil other incl wax appearance temperature (°F)">
      ${oilFluidValues.oilOtherWaxAppearanceTemperature()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>
</#macro>
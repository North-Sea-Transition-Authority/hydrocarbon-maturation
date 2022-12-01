<#include '../layout/layout.ftl'>
<#-- @ftlvariable name="oilFluidValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySummaryView" -->

<#macro oilFluidPropertiesCard oilFluidValues>
  <@fdsSummaryList.summaryListCard headingText="Oil properties" summaryListId="oilproperties">
    <@fdsSummaryList.summaryListRowNoAction keyText="Oil Gravity">
      ${oilFluidValues.oilGravity()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Oil Viscosity">
      ${oilFluidValues.oilViscosity()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Oil Sulphur">
      ${oilFluidValues.oilSulphur()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Oil wax">
      ${oilFluidValues.oilWax()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Oil other incl wax appearance temperature">
      ${oilFluidValues.oilOtherWaxAppearanceTemperature()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>
</#macro>
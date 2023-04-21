<#include '../layout/layout.ftl'>
<#-- @ftlvariable name="gasFluidValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySummaryView" -->

<#macro gasFluidPropertiesCard gasFluidValues>
  <@fdsSummaryList.summaryListCard headingText="Gas properties" summaryListId="gasproperties">
    <@fdsSummaryList.summaryListRowNoAction keyText="Gas specific gravity (air=1)">
      ${gasFluidValues.gasGravity()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Gas H₂S">
      ${gasFluidValues.gasH2s()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Gas CO₂">
      ${gasFluidValues.gasCo2()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Gas N₂">
      ${gasFluidValues.gasN2()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Is condensate present?">
      ${gasFluidValues.condensatePresent()}
    </@fdsSummaryList.summaryListRowNoAction>
    <#if gasFluidValues.showCondensate()>
      <@fdsSummaryList.summaryListRowNoAction keyText="Condensate gravity">
        ${gasFluidValues.condensateGravity()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Condensate viscosity">
        ${gasFluidValues.condensateViscosity()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Condensate sulphur">
        ${gasFluidValues.condensateSulphur()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Condensate wax">
        ${gasFluidValues.condensateWax()}
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="Condensate other incl wax appearance temperature">
        ${gasFluidValues.condensateOtherInclWax()}
      </@fdsSummaryList.summaryListRowNoAction>
    </#if>
    <@fdsSummaryList.summaryListRowNoAction keyText="Advise of any other gases and their concentrations">
      <p class="govuk-body govuk-body__preserve-whitespace">${gasFluidValues.adviseOtherGasesConcentration()}</p>
    </@fdsSummaryList.summaryListRowNoAction>

  </@fdsSummaryList.summaryListCard>
</#macro>
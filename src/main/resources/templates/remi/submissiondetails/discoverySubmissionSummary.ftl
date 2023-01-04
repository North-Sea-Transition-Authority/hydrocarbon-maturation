<#include '../layout/layout.ftl'>

<#import '../summarylistcards/permeabilitySummaryCard.ftl' as permeabilitySummary>
<#import '../summarylistcards/reservoirPropertiesSummaryCard.ftl' as reservoirPropertiesSummary>
<#import '../summarylistcards/gasFluidSummaryCard.ftl' as gasFluidSummary>
<#import '../summarylistcards/generalFluidSummaryCard.ftl' as generalFluidSummary>
<#import '../summarylistcards/oilFluidSummaryCard.ftl' as oilFluidSummary>
<#import '../summarylistcards/generaldetailsSummaryCard.ftl' as generalDetailsSummary>


<#assign pageTitle = "Check your answers before submitting" />
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH
  wrapperWidth=true
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
  <@fdsForm.htmlForm>
    <@fdsAccordion.accordion accordionId="summaryaccordian">
      <@fdsAccordion.accordionSection sectionHeading="General details">
        <@generalDetailsSummary.generalDetailCard generalDetailValues=generalDetailsSummaryView/>
      </@fdsAccordion.accordionSection>
      <@fdsAccordion.accordionSection sectionHeading="Reservoir details" summaryText="Reservoir properties, permeability">
        <@reservoirPropertiesSummary.reservoirPropertiesSummaryCard
            reservoirPropertiesValues=reservoirPropertiesSummaryView/>
        <@permeabilitySummary.permeabilityDetailsSummaryCard
          permeabilityDetailsValues=permeabiltySummaryView
          optionalPermeability=false/>
      </@fdsAccordion.accordionSection>
      <@fdsAccordion.accordionSection sectionHeading="Average hydrocarbon properties" summaryText="Oil, gas and general properties">
        <#if oilOnlyDiscovery || mixedDiscovery>
          <@oilFluidSummary.oilFluidPropertiesCard
            oilFluidValues=oilFluidPropertiesSummaryView/>
        </#if>
        <#if gasOnlyDiscovery || mixedDiscovery>
          <@gasFluidSummary.gasFluidPropertiesCard
            gasFluidValues=gasFluidPropertiesSummaryView/>
        </#if>
        <@generalFluidSummary.generalFluidPropertiesCard
          generalFluidValues=generalFluidPropertiesSummaryView/>
      </@fdsAccordion.accordionSection>
    </@fdsAccordion.accordion>

    <@fdsAction.submitButtons
      primaryButtonText="Submit"
      secondaryLinkText="Cancel"
      linkSecondaryAction=true
      linkSecondaryActionUrl="${springUrl(cancelUrl)}"/>
  </@fdsForm.htmlForm>
</@defaultPage>
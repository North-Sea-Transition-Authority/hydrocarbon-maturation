<#include '../../layout/layout.ftl'>

<#import '../../summarylistcards/permeabilitySummaryCard.ftl' as permeabilitySummary>
<#import '../../summarylistcards/reservoirPropertiesSummaryCard.ftl' as reservoirPropertiesSummary>
<#import '../../summarylistcards/gasFluidSummaryCard.ftl' as gasFluidSummary>
<#import '../../summarylistcards/generalFluidSummaryCard.ftl' as generalFluidSummary>
<#import '../../summarylistcards/oilFluidSummaryCard.ftl' as oilFluidSummary>
<#import '../../summarylistcards/generaldetailsSummaryCard.ftl' as generalDetailsSummary>

<#macro discoverySubmissionSummary
  generalDetailsSummaryView
  permeabilitySummaryView
  oilOnlyDiscovery
  gasOnlyDiscovery
  mixedDiscovery
  oilFluidPropertiesSummaryView
  gasFluidPropertiesSummaryView
  generalFluidPropertiesSummaryView>
  <@fdsAccordion.accordion accordionId="summaryaccordian">
    <@fdsAccordion.accordionSection sectionHeading="General details">
      <@generalDetailsSummary.generalDetailCard generalDetailValues=generalDetailsSummaryView/>
    </@fdsAccordion.accordionSection>
    <@fdsAccordion.accordionSection sectionHeading="Reservoir details" summaryText="Reservoir properties, permeability">
      <@reservoirPropertiesSummary.reservoirPropertiesSummaryCard
        reservoirPropertiesValues=reservoirPropertiesSummaryView/>
      <@permeabilitySummary.permeabilityDetailsSummaryCard
        permeabilityDetailsValues=permeabilitySummaryView
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
</#macro>
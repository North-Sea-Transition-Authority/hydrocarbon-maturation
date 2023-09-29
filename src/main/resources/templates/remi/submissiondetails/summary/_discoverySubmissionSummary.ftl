<#include '../../layout/layout.ftl'>

<#import '../../summarylistcards/permeabilitySummaryCard.ftl' as permeabilitySummary>
<#import '../../summarylistcards/reservoirPropertiesSummaryCard.ftl' as reservoirPropertiesSummary>
<#import '../../summarylistcards/gasFluidSummaryCard.ftl' as gasFluidSummary>
<#import '../../summarylistcards/generalFluidSummaryCard.ftl' as generalFluidSummary>
<#import '../../summarylistcards/oilFluidSummaryCard.ftl' as oilFluidSummary>
<#import '../../summarylistcards/generaldetailsSummaryCard.ftl' as generalDetailsSummary>
<#import '../../summarylistcards/oilCaseVolumetricsSummaryCard.ftl' as oilCaseVolumetricsSummary>
<#import '../../summarylistcards/oilCaseOutputsSummaryCard.ftl' as oilCaseOutputsSummary>
<#import '../../summarylistcards/gasCaseVolumetricsSummaryCard.ftl' as gasCaseVolumetricsSummary>
<#import '../../summarylistcards/gasCaseOutputsSummaryCard.ftl' as gasCaseOutputsSummary>
<#import '../../summarylistcards/mixedCaseVolumetricsSummaryCard.ftl' as mixedCaseVolumetricsSummary>
<#import '../../summarylistcards/mixedCaseOutputsSummaryCard.ftl' as mixedCaseOutputsSummary>
<#import '../../summarylistcards/drillstemtest/drillStemTestSummary.ftl' as drillStemTestSummary>
<#import '../../summarylistcards/opportunityDetailsSummaryCard.ftl' as opportunityDetailsSummary>
<#import '../../summarylistcards/outlineForMaximumCaseSummaryCard.ftl' as outlineForMaximumCaseSummary>
<#import '../../summarylistcards/discoveryWellSummaryCard.ftl' as discoveryWellDetailSummary>
<#import '../../summarylistcards/sourceAndSealSummaryCard.ftl' as sourceAndSealSummary>
<#import '../../summarylistcards/trapPropertiesSummaryCard.ftl' as trapPropertiesSummary>
<#import '../../summarylistcards/resourceDetailsSummaryCard.ftl' as resourceDetailSummary>

<#-- @ftlvariable name="accordionId" type="String" -->

<#macro discoverySubmissionSummary
  generalDetailsSummaryView
  permeabilitySummaryView
  oilOnlyDiscovery
  gasOnlyDiscovery
  mixedDiscovery
  oilFluidPropertiesSummaryView
  gasFluidPropertiesSummaryView
  generalFluidPropertiesSummaryView
  reservoirPropertiesSummaryView
  oilCaseVolumetricsSummaryView
  oilCaseOutputsSummaryView
  gasCaseVolumetricsSummaryView
  gasCaseOutputsSummaryView
  mixedCaseVolumetricsSummaryView
  mixedCaseOutputsSummaryView
  drillStemTestSummaryView
  discoveryWellDetailsSummaryView
  opportunityDetailsSummaryView
  outlineForMaximumCaseSummaryView
  sourceAndSealSummaryView
  trapPropertiesSummaryView
  resourceDetailSummaryView>
  <@fdsAccordion.accordion accordionId="summaryaccordian-${accordionId}">
    <@fdsAccordion.accordionSection sectionHeading="General details" summaryText="Resource details, general details" openSection=true>
      <@resourceDetailSummary.resourceDetailCard resourceDetailValues=resourceDetailSummaryView/>
      <@generalDetailsSummary.generalDetailCard generalDetailValues=generalDetailsSummaryView isOffshore=resourceDetailSummaryView.isOffshore()/>
    </@fdsAccordion.accordionSection>
    <@fdsAccordion.accordionSection sectionHeading="Opportunity" summaryText="Opportunity details, trap properties, source and seal">
      <@outlineForMaximumCaseSummary.outlineForMaximumCaseSummaryCard outlineForMaximumCaseValues=outlineForMaximumCaseSummaryView />
      <@opportunityDetailsSummary.opportunityDetailsSummaryCard opportunityDetailsValues=opportunityDetailsSummaryView mapName="Map of discovery"/>
      <@trapPropertiesSummary.trapPropertiesSummaryCard trapPropertiesValues=trapPropertiesSummaryView/>
      <@sourceAndSealSummary.sourceAndSealCard sourceAndSealValues=sourceAndSealSummaryView/>
    </@fdsAccordion.accordionSection>
<#-- TODO: Uncomment out when the discovery summaries have been finlaised - have a reservoir submission detial to tie to -->
<#--    <@fdsAccordion.accordionSection sectionHeading="Reservoir details" summaryText="Reservoir properties, permeability">-->
<#--        <@reservoirPropertiesSummary.reservoirPropertiesSummaryCard-->
<#--          reservoirPropertiesValues=reservoirPropertiesSummaryView/>-->
<#--        <@permeabilitySummary.permeabilityDetailsSummaryCard-->
<#--          permeabilityDetailsValues=permeabilitySummaryView-->
<#--          optionalPermeability=false/>-->
<#--    </@fdsAccordion.accordionSection>-->
<#--    <@fdsAccordion.accordionSection sectionHeading="Volumetrics" summaryText="Oil case, gas case, and mixed case volumetric parameters and outputs">-->
<#--        <#if oilOnlyDiscovery>-->
<#--            <@oilCaseVolumetricsSummary.oilCaseVolumetricsSummary oilCaseVolumetricsValues=oilCaseVolumetricsSummaryView/>-->
<#--            <@oilCaseOutputsSummary.oilCaseOutputsSummaryCard oilCaseOutputsValues=oilCaseOutputsSummaryView withContingentUncertaintyCategories=true/>-->
<#--        </#if>-->
<#--        <#if gasOnlyDiscovery>-->
<#--            <@gasCaseVolumetricsSummary.gasCaseVolumetricsSummary gasCaseVolumetricsValues=gasCaseVolumetricsSummaryView/>-->
<#--            <@gasCaseOutputsSummary.gasCaseOutputsSummaryCard gasCaseOutputsValues=gasCaseOutputsSummaryView withContingentUncertaintyCategories=true/>-->
<#--        </#if>-->
<#--        <#if mixedDiscovery>-->
<#--            <@mixedCaseVolumetricsSummary.mixedCaseVolumetricsSummary mixedCaseVolumetricsValues=mixedCaseVolumetricsSummaryView/>-->
<#--            <@mixedCaseOutputsSummary.mixedCaseOutputsSummaryCard mixedCaseOutputsValues=mixedCaseOutputsSummaryView withContingentUncertaintyCategories=true/>-->
<#--        </#if>-->
<#--    </@fdsAccordion.accordionSection>-->
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
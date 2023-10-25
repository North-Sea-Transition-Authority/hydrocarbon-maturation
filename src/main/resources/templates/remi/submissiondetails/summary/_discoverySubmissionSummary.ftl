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
<#import '../../summarylistcards/discoveryOilCaseVolumetricTotalsSummaryCard.ftl' as discoveryOilCaseVolumetricTotalsSummary>
<#import '../../summarylistcards/discoveryGasCaseVolumetricTotalsSummaryCard.ftl' as discoveryGasCaseVolumetricTotalsSummary>
<#import '../../summary/_reservoirSummary.ftl' as reservoirSummary>

<#-- @ftlvariable name="accordionId" type="String" -->
<#-- @ftlvariable name="totalsRequired" type="boolean" -->
<#-- @ftlvariable name="isOilCaseOnly" type="boolean" -->
<#-- @ftlvariable name="isGasCaseOnly" type="boolean" -->
<#-- @ftlvariable name="isCombinedCase" type="boolean" -->

<#macro discoverySubmissionSummary
  generalDetailsSummaryView
  permeabilitySummaryView
  reservoirPropertiesSummaryView
  oilCaseVolumetricsSummaryView
  oilCaseOutputsSummaryView
  gasCaseVolumetricsSummaryView
  gasCaseOutputsSummaryView
  mixedCaseVolumetricsSummaryView
  mixedCaseOutputsSummaryView
  discoveryWellDetailsSummaryView
  opportunityDetailsSummaryView
  outlineForMaximumCaseSummaryView
  sourceAndSealSummaryView
  trapPropertiesSummaryView
  resourceDetailSummaryView
  oilCaseVolumetricTotalsSummaryView
  gasCaseVolumetricTotalsSummaryView
  reservoirSummaries>
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
    <@fdsAccordion.accordionSection sectionHeading="Discovery well details" summaryText="Discovery well details">
      <@discoveryWellDetailSummary.discoveryWellDetailCard discoveryWellDetailsSummaryView/>
    </@fdsAccordion.accordionSection>
    <#if totalsRequired>
        <@fdsAccordion.accordionSection sectionHeading="Total volumes for discovery">
            <#if isOilCaseOnly>
                <@discoveryOilCaseVolumetricTotalsSummary.oilCaseVolumetricTotalsSummaryCard oilCaseVolumetricTotals=oilCaseVolumetricTotalsSummaryView/>
            </#if>
            <#if isGasCaseOnly>
                <@discoveryGasCaseVolumetricTotalsSummary.gasCaseVolumetricTotalsSummaryCard gasCaseVolumetricTotals=gasCaseVolumetricTotalsSummaryView/>
            </#if>
        </@fdsAccordion.accordionSection>
    </#if>
    <@reservoirSummary.reservoirSummary reservoirSummaries/>
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
<#--    <@fdsAccordion.accordionSection sectionHeading="Average hydrocarbon properties" summaryText="Oil, gas and general properties">-->
<#--        <#if oilOnlyDiscovery || mixedDiscovery>-->
<#--            <@oilFluidSummary.oilFluidPropertiesCard-->
<#--            oilFluidValues=oilFluidPropertiesSummaryView/>-->
<#--        </#if>-->
<#--        <#if gasOnlyDiscovery || mixedDiscovery>-->
<#--            <@gasFluidSummary.gasFluidPropertiesCard-->
<#--            gasFluidValues=gasFluidPropertiesSummaryView/>-->
<#--        </#if>-->
<#--        <@generalFluidSummary.generalFluidPropertiesCard-->
<#--        generalFluidValues=generalFluidPropertiesSummaryView/>-->
<#--    </@fdsAccordion.accordionSection>-->
  </@fdsAccordion.accordion>
</#macro>
<#include '../../layout/layout.ftl'>

<#import '../../summarylistcards/generaldetailsSummaryCard.ftl' as generalDetailsSummary>
<#import '../../summarylistcards/sourceAndSealSummaryCard.ftl' as sourceAndSealSummary>
<#import '../../summarylistcards/hydrocarbonCaseSummaryCard.ftl' as hydrocarbonCaseSummary>
<#import '../../summarylistcards/reservoirPropertiesSummaryCard.ftl' as reservoirPropertiesSummary>
<#import '../../summarylistcards/chanceOfSuccessSummaryCard.ftl' as chanceOfSuccessSummary>
<#import '../../summarylistcards/trapPropertiesSummaryCard.ftl' as trapPropertiesSummary>
<#import '../../summarylistcards/oilCaseVolumetricsSummaryCard.ftl' as oilCaseVolumetricsSummary>
<#import '../../summarylistcards/oilCaseOutputsSummaryCard.ftl' as oilCaseOutputsSummary>
<#import '../../summarylistcards/gasCaseVolumetricsSummaryCard.ftl' as gasCaseVolumetricsSummary>
<#import '../../summarylistcards/gasCaseOutputsSummaryCard.ftl' as gasCaseOutputsSummary>
<#import '../../summarylistcards/mixedCaseVolumetricsSummaryCard.ftl' as mixedCaseVolumetricsSummary>
<#import '../../summarylistcards/mixedCaseOutputsSummaryCard.ftl' as mixedCaseOutputsSummary>
<#import '../../summarylistcards/opportunityDetailsSummaryCard.ftl' as opportunityDetailsSummary>
<#import '../../summarylistcards/resourceDetailsSummaryCard.ftl' as resourceDetailSummary>

<#-- @ftlvariable name="accordionId" type="String" -->

<#macro prospectSubmissionSummary
  generalDetailsSummaryView
  opportunityDetailsSummaryView
  trapPropertiesSummaryView
  sourceAndSealSummaryView
  hydrocarbonCaseSummaryView
  chanceOfSuccessSummaryView
  reservoirPropertiesSummaryView
  oilCase
  oilCaseVolumetricsSummaryView
  oilCaseOutputsSummaryView
  gasCase
  gasCaseVolumetricsSummaryView
  gasCaseOutputsSummaryView
  mixedCase
  mixedCaseVolumetricsSummaryView
  mixedCaseOutputsSummaryView,
  resourceDetailSummaryView>
    <@fdsAccordion.accordion accordionId="summaryaccordian-${accordionId}">
        <@fdsAccordion.accordionSection sectionHeading="General details" summaryText="Resource details, general details">
          <@resourceDetailSummary.resourceDetailCard resourceDetailValues=resourceDetailSummaryView/>
          <@generalDetailsSummary.generalDetailCard generalDetailValues=generalDetailsSummaryView/>
        </@fdsAccordion.accordionSection>
        <@fdsAccordion.accordionSection sectionHeading="Opportunity" summaryText="Opportunity details, trap properties, source and seal, hydrocarbon case, chance of success">
            <@opportunityDetailsSummary.opportunityDetailsSummaryCard opportunityDetailsValues=opportunityDetailsSummaryView/>
            <@trapPropertiesSummary.trapPropertiesSummaryCard trapPropertiesValues=trapPropertiesSummaryView/>
            <@sourceAndSealSummary.sourceAndSealCard sourceAndSealValues=sourceAndSealSummaryView/>
            <@hydrocarbonCaseSummary.hydrocarbonCaseCard hydrocarbonCaseValues=hydrocarbonCaseSummaryView/>
            <@chanceOfSuccessSummary.chanceOfSuccessCards chanceOfSuccessValues=chanceOfSuccessSummaryView/>
        </@fdsAccordion.accordionSection>
        <@fdsAccordion.accordionSection sectionHeading="Reservoir" summaryText="Reservoir properties">
            <@reservoirPropertiesSummary.reservoirPropertiesSummaryCard reservoirPropertiesValues=reservoirPropertiesSummaryView/>
        </@fdsAccordion.accordionSection>
        <@fdsAccordion.accordionSection sectionHeading="Volumetrics" summaryText="Oil case, gas case, and mixed case volumetric parameters and outputs">
            <#if oilCase>
                <@oilCaseVolumetricsSummary.oilCaseVolumetricsSummary oilCaseVolumetricsValues=oilCaseVolumetricsSummaryView/>
                <@oilCaseOutputsSummary.oilCaseOutputsSummaryCard oilCaseOutputsValues=oilCaseOutputsSummaryView/>
            </#if>
            <#if gasCase>
                <@gasCaseVolumetricsSummary.gasCaseVolumetricsSummary gasCaseVolumetricsValues=gasCaseVolumetricsSummaryView/>
                <@gasCaseOutputsSummary.gasCaseOutputsSummaryCard gasCaseOutputsValues=gasCaseOutputsSummaryView/>
            </#if>
            <#if mixedCase>
                <@mixedCaseVolumetricsSummary.mixedCaseVolumetricsSummary mixedCaseVolumetricsValues=mixedCaseVolumetricsSummaryView/>
                <@mixedCaseOutputsSummary.mixedCaseOutputsSummaryCard mixedCaseOutputsValues=mixedCaseOutputsSummaryView/>
            </#if>
        </@fdsAccordion.accordionSection>
    </@fdsAccordion.accordion>
</#macro>
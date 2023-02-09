<#include '../../layout/layout.ftl'>

<#import '../../summarylistcards/dryWellSummaryCard.ftl' as dryWellSummary>
<#import '../../summarylistcards/permeabilitySummaryCard.ftl' as permeabilitySummary>
<#import '../../summarylistcards/reservoirPropertiesSummaryCard.ftl' as reservoirPropertiesSummary>
<#import '../../summarylistcards/generaldetailsSummaryCard.ftl' as generalDetailsSummary>
<#import '../../summarylistcards/opportunityDetailsSummaryCard.ftl' as opportunityDetailsSummary>
<#import '../../summarylistcards/sourceAndSealSummaryCard.ftl' as sourceAndSealSummary>
<#import '../../summarylistcards/trapPropertiesSummaryCard.ftl' as trapPropertiesSummary>
<#import '../../summarylistcards/resourceDetailsSummaryCard.ftl' as resourceDetailSummary>

<#macro dryWellSubmissionSummary
  generalDetailsSummaryView
  dryWellDetailsSummaryView
  reservoirPropertiesSummaryView
  permeabilitySummaryView
  opportunityDetailsSummaryView
  sourceAndSealSummaryView
  trapPropertiesSummaryView
  resourceDetailSummaryView>
    <@fdsAccordion.accordion accordionId="summaryaccordian">
        <@fdsAccordion.accordionSection sectionHeading="General details" summaryText="Resource details, general details">
          <@resourceDetailSummary.resourceDetailCard resourceDetailValues=resourceDetailSummaryView/>
          <@generalDetailsSummary.generalDetailCard generalDetailValues=generalDetailsSummaryView/>
        </@fdsAccordion.accordionSection>
        <@fdsAccordion.accordionSection sectionHeading="Opportunity" summaryText="Opportunity details, trap properties, source and seal">
          <@opportunityDetailsSummary.opportunityDetailsSummaryCard opportunityDetailsValues=opportunityDetailsSummaryView/>
          <@trapPropertiesSummary.trapPropertiesSummaryCard trapPropertiesValues=trapPropertiesSummaryView/>
          <@sourceAndSealSummary.sourceAndSealCard sourceAndSealValues=sourceAndSealSummaryView/>
        </@fdsAccordion.accordionSection>
        <@fdsAccordion.accordionSection sectionHeading="Post well drill" summaryText="Dry well details">
            <@dryWellSummary.dryWellDetailCard dryWellValues=dryWellDetailsSummaryView/>
        </@fdsAccordion.accordionSection>
        <@fdsAccordion.accordionSection sectionHeading="Reservoir details" summaryText="Reservoir properties, permeability">
            <@reservoirPropertiesSummary.reservoirPropertiesSummaryCard
              reservoirPropertiesValues=reservoirPropertiesSummaryView />
            <@permeabilitySummary.permeabilityDetailsSummaryCard
              permeabilityDetailsValues=permeabilitySummaryView
              optionalPermeability=true/>
        </@fdsAccordion.accordionSection>
    </@fdsAccordion.accordion>
</#macro>
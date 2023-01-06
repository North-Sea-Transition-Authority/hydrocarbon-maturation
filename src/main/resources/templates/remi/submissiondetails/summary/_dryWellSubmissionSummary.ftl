<#include '../../layout/layout.ftl'>

<#import '../../summarylistcards/dryWellSummaryCard.ftl' as dryWellSummary>
<#import '../../summarylistcards/permeabilitySummaryCard.ftl' as permeabilitySummary>
<#import '../../summarylistcards/reservoirPropertiesSummaryCard.ftl' as reservoirPropertiesSummary>
<#import '../../summarylistcards/generaldetailsSummaryCard.ftl' as generalDetailsSummary>

<#macro dryWellSubmissionSummary
  generalDetailsSummaryView
  dryWellDetailsSummaryView
  reservoirPropertiesSummaryView
  permeabilitySummaryView>
    <@fdsAccordion.accordion accordionId="summaryaccordian">
        <@fdsAccordion.accordionSection sectionHeading="General details">
            <@generalDetailsSummary.generalDetailCard generalDetailValues=generalDetailsSummaryView/>
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
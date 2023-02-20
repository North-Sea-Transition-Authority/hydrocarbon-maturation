<#include '../../layout/layout.ftl'>

<#import './_prospectSubmissionSummary.ftl' as prospectSubmissionSummary>
<#import './_dryWellSubmissionSummary.ftl' as dryWellSubmissionSummary>
<#import './_discoverySubmissionSummary.ftl' as discoverySubmissionSummary>

<#macro submissionSummary submissionType>
    <#if submissionType == "CREATE_PROSPECT" || submissionType == "PROSPECT_UPDATE" || submissionType== "RELINQUISHMENT_PROSPECT">
      <@prospectSubmissionSummary.prospectSubmissionSummary
        generalDetailsSummaryView=generalDetailsSummaryView
        opportunityDetailsSummaryView=opportunityDetailsSummaryView
        trapPropertiesSummaryView=trapPropertiesSummaryView
        sourceAndSealSummaryView=sourceAndSealSummaryView
        hydrocarbonCaseSummaryView=hydrocarbonCaseSummaryView
        chanceOfSuccessSummaryView=chanceOfSuccessSummaryView
        reservoirPropertiesSummaryView=reservoirPropertiesSummaryView
        oilCase=oilCase
        oilCaseVolumetricsSummaryView=oilCaseVolumetricsSummaryView
        oilCaseOutputsSummaryView=oilCaseOutputsSummaryView
        gasCase=gasCase
        gasCaseVolumetricsSummaryView=gasCaseVolumetricsSummaryView
        gasCaseOutputsSummaryView=gasCaseOutputsSummaryView
        mixedCase=mixedCase
        mixedCaseVolumetricsSummaryView=mixedCaseVolumetricsSummaryView
        mixedCaseOutputsSummaryView=mixedCaseOutputsSummaryView
        resourceDetailSummaryView=resourceDetailSummaryView
        />
    <#elseif submissionType == "POST_DRILL_DRY_HOLE">
      <@dryWellSubmissionSummary.dryWellSubmissionSummary
        generalDetailsSummaryView=generalDetailsSummaryView
        dryWellDetailsSummaryView=dryWellDetailsSummaryView
        reservoirPropertiesSummaryView=reservoirPropertiesSummaryView
        permeabilitySummaryView=permeabilitySummaryView
        opportunityDetailsSummaryView=opportunityDetailsSummaryView
        sourceAndSealSummaryView=sourceAndSealSummaryView
        trapPropertiesSummaryView=trapPropertiesSummaryView
        resourceDetailSummaryView=resourceDetailSummaryView/>
    <#elseif submissionType == "CREATE_DISCOVERY"
    || submissionType == "UPDATE_DISCOVERY"
    || submissionType == "CREATE_DISCOVERY_FROM_PROSPECT"
    || submissionType == "RELINQUISHMENT_DISCOVERY">
      <@discoverySubmissionSummary.discoverySubmissionSummary
        generalDetailsSummaryView=generalDetailsSummaryView
        permeabilitySummaryView=permeabilitySummaryView
        oilOnlyDiscovery=oilOnlyDiscovery
        gasOnlyDiscovery=gasOnlyDiscovery
        mixedDiscovery=mixedDiscovery
        oilFluidPropertiesSummaryView=oilFluidPropertiesSummaryView
        gasFluidPropertiesSummaryView=gasFluidPropertiesSummaryView
        generalFluidPropertiesSummaryView=generalFluidPropertiesSummaryView
        reservoirPropertiesSummaryView=reservoirPropertiesSummaryView
        oilCaseOutputsSummaryView=oilCaseOutputsSummaryView
        oilCaseVolumetricsSummaryView=oilCaseVolumetricsSummaryView
        gasCaseVolumetricsSummaryView=gasCaseVolumetricsSummaryView
        gasCaseOutputsSummaryView=gasCaseOutputsSummaryView
        mixedCaseVolumetricsSummaryView=mixedCaseVolumetricsSummaryView
        mixedCaseOutputsSummaryView=mixedCaseOutputsSummaryView
        drillStemTestSummaryView=drillStemTestSummaryView
        opportunityDetailsSummaryView=opportunityDetailsSummaryView
        discoveryWellDetailsSummaryView=discoveryWellDetailsSummaryView
        sourceAndSealSummaryView=sourceAndSealSummaryView
        trapPropertiesSummaryView=trapPropertiesSummaryView
        resourceDetailSummaryView=resourceDetailSummaryView/>
    </#if>
</#macro>
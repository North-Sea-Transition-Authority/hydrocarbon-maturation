<#include '../../layout/layout.ftl'>

<#import './_prospectSubmissionSummary.ftl' as prospectSubmissionSummary>
<#import './_dryWellSubmissionSummary.ftl' as dryWellSubmissionSummary>
<#import './_discoverySubmissionSummary.ftl' as discoverySubmissionSummary>

<#-- @ftlvariable name="generalDetailsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView" -->
<#-- @ftlvariable name="opportunityDetailsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryView" -->
<#-- @ftlvariable name="outlineForMaximumCaseSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase.OutlineForMaximumCaseSummaryView" -->
<#-- @ftlvariable name="trapPropertiesSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryView" -->
<#-- @ftlvariable name="sourceAndSealSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView" -->
<#-- @ftlvariable name="reservoirPropertiesSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView" -->
<#-- @ftlvariable name="hydrocarbonCaseSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseSummaryView" -->
<#-- @ftlvariable name="chanceOfSuccessSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSummaryView" -->
<#-- @ftlvariable name="oilCase" type="Boolean" -->
<#-- @ftlvariable name="oilCaseVolumetricsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView" -->
<#-- @ftlvariable name="oilCaseOutputsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryView" -->
<#-- @ftlvariable name="gasCase" type="Boolean" -->
<#-- @ftlvariable name="gasCaseVolumetricsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView" -->
<#-- @ftlvariable name="gasCaseOutputsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryView" -->
<#-- @ftlvariable name="mixedCase" type="Boolean" -->
<#-- @ftlvariable name="mixedCaseVolumetricsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView" -->
<#-- @ftlvariable name="mixedCaseOutputsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryView" -->
<#-- @ftlvariable name="resourceDetailSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailSummaryView" -->
<#-- @ftlvariable name="isOffshore" type="Boolean" -->
<#-- @ftlvariable name="dryWellDetailsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSummaryView" -->
<#-- @ftlvariable name="permeabilitySummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailSummaryView" -->
<#-- @ftlvariable name="oilOnlyDiscovery" type="Boolean" -->
<#-- @ftlvariable name="gasOnlyDiscovery" type="Boolean" -->
<#-- @ftlvariable name="mixedDiscovery" type="Boolean" -->
<#-- @ftlvariable name="oilFluidPropertiesSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySummaryView" -->
<#-- @ftlvariable name="gasFluidPropertiesSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySummaryView" -->
<#-- @ftlvariable name="generalFluidPropertiesSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySummaryView" -->
<#-- @ftlvariable name="drillStemTestSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestSummaryView" -->
<#-- @ftlvariable name="discoveryWellDetailsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsSummaryView" -->
<#-- @ftlvariable name="reservoirSummaries" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ReservoirSummary>" -->

<#macro submissionSummary submissionType>
    <#if submissionType == "CREATE_PROSPECT" || submissionType == "PROSPECT_UPDATE" || submissionType== "RELINQUISHMENT_PROSPECT">
      <@prospectSubmissionSummary.prospectSubmissionSummary
        generalDetailsSummaryView=generalDetailsSummaryView
        opportunityDetailsSummaryView=opportunityDetailsSummaryView
        outlineForMaximumCaseSummaryView=outlineForMaximumCaseSummaryView
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
        resourceDetailSummaryView=resourceDetailSummaryView/>
    <#elseif submissionType == "POST_DRILL_DRY_HOLE">
      <@dryWellSubmissionSummary.dryWellSubmissionSummary
        generalDetailsSummaryView=generalDetailsSummaryView
        dryWellDetailsSummaryView=dryWellDetailsSummaryView
        reservoirPropertiesSummaryView=reservoirPropertiesSummaryView
        permeabilitySummaryView=permeabilitySummaryView
        opportunityDetailsSummaryView=opportunityDetailsSummaryView
        outlineForMaximumCaseSummaryView=outlineForMaximumCaseSummaryView
        sourceAndSealSummaryView=sourceAndSealSummaryView
        trapPropertiesSummaryView=trapPropertiesSummaryView
        resourceDetailSummaryView=resourceDetailSummaryView/>
    <#elseif submissionType == "CREATE_DISCOVERY"
    || submissionType == "UPDATE_DISCOVERY"
    || submissionType == "CREATE_DISCOVERY_FROM_PROSPECT"
    || submissionType == "RELINQUISHMENT_DISCOVERY">
      <@discoverySubmissionSummary.discoverySubmissionSummary
        generalDetailsSummaryView=generalDetailsSummaryView
        permeabilitySummaryView=permeabilitySummaryView!""
        generalFluidPropertiesSummaryView=generalFluidPropertiesSummaryView
        reservoirPropertiesSummaryView=reservoirPropertiesSummaryView!""
        oilCaseOutputsSummaryView=oilCaseOutputsSummaryView!""
        oilCaseVolumetricsSummaryView=oilCaseVolumetricsSummaryView!""
        gasCaseVolumetricsSummaryView=gasCaseVolumetricsSummaryView!""
        gasCaseOutputsSummaryView=gasCaseOutputsSummaryView!""
        mixedCaseVolumetricsSummaryView=mixedCaseVolumetricsSummaryView!""
        mixedCaseOutputsSummaryView=mixedCaseOutputsSummaryView!""
        opportunityDetailsSummaryView=opportunityDetailsSummaryView
        outlineForMaximumCaseSummaryView=outlineForMaximumCaseSummaryView
        discoveryWellDetailsSummaryView=discoveryWellDetailsSummaryView
        sourceAndSealSummaryView=sourceAndSealSummaryView
        trapPropertiesSummaryView=trapPropertiesSummaryView
        resourceDetailSummaryView=resourceDetailSummaryView
        reservoirSummaries=reservoirSummaries/>
    </#if>
</#macro>
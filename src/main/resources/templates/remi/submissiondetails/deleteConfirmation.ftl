<#include '../layout/layout.ftl'>
<#import './summary/_prospectSubmissionSummary.ftl' as prospectSubmissionSummary>
<#import './summary/_dryWellSubmissionSummary.ftl' as dryWellSubmissionSummary>
<#import './summary/_discoverySubmissionSummary.ftl' as discoverySubmissionSummary>

<#-- @ftlvariable name="pageName" type="String" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="resourceName" type="String" -->
<#-- @ftlvariable name="deleteUrl" type="String" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
<#-- @ftlvariable name="submissionType" type="String" -->

<@defaultPage
  htmlTitle=pageName
  pageHeading=pageName
  pageSize=PageSize.FULL_WIDTH
  wrapperWidth=true
  breadcrumbsMap=breadcrumbsMap
  caption=resourceName
>
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
      />
  <#elseif submissionType == "POST_DRILL_DRY_HOLE">
    <@dryWellSubmissionSummary.dryWellSubmissionSummary
      generalDetailsSummaryView=generalDetailsSummaryView
      dryWellDetailsSummaryView=dryWellDetailsSummaryView
      reservoirPropertiesSummaryView=reservoirPropertiesSummaryView
      permeabilitySummaryView=permeabilitySummaryView
      opportunityDetailsSummaryView=opportunityDetailsSummaryView
      sourceAndSealSummaryView=sourceAndSealSummaryView
      trapPropertiesSummaryView=trapPropertiesSummaryView/>
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
      trapPropertiesSummaryView=trapPropertiesSummaryView/>
  </#if>
  <@fdsForm.htmlForm actionUrl=springUrl(deleteUrl)>
    <@fdsAction.submitButtons
      primaryButtonText="Delete"
      secondaryLinkText="Cancel"
      linkSecondaryAction=true
      linkSecondaryActionUrl=springUrl(cancelUrl)
      primaryButtonClass="govuk-button govuk-button--warning"/>
  </@fdsForm.htmlForm>
</@defaultPage>
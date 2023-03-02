<#include '../../layout/layout.ftl'>

<#import '_discoverySubmissionSummary.ftl' as discoverySubmissionSummary>
<#import '_summarySubmissionNotificationBanner.ftl' as banner>

<#assign pageTitle = "Check your answers before submitting" />
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
<#-- @ftlvariable name="submittable" type="Boolean" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="oilOnlyDiscovery" type="Boolean" -->
<#-- @ftlvariable name="gasOnlyDiscovery" type="Boolean" -->
<#-- @ftlvariable name="mixedDiscovery" type="Boolean" -->
<#-- @ftlvariable name="generalDetailsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView" -->
<#-- @ftlvariable name="permeabilitySummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailSummaryView" -->
<#-- @ftlvariable name="oilFluidPropertiesSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties.OilFluidPropertySummaryView" -->
<#-- @ftlvariable name="gasFluidPropertiesSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties.GasFluidPropertySummaryView" -->
<#-- @ftlvariable name="generalFluidPropertiesSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties.GeneralFluidPropertySummaryView" -->
<#-- @ftlvariable name="reservoirPropertiesSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView" -->
<#-- @ftlvariable name="oilCaseOutputsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryView" -->
<#-- @ftlvariable name="oilCaseVolumetricsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView" -->
<#-- @ftlvariable name="gasCaseVolumetricsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView" -->
<#-- @ftlvariable name="gasCaseOutputsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryService" -->
<#-- @ftlvariable name="mixedCaseVolumetricsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView" -->
<#-- @ftlvariable name="mixedCaseOutputsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryView" -->
<#-- @ftlvariable name="drillStemTestSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drillstemtestresults.DrillStemTestSummaryView" -->
<#-- @ftlvariable name="opportunityDetailsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryView" -->
<#-- @ftlvariable name="sourceAndSealSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView" -->
<#-- @ftlvariable name="discoveryWellDetailsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.DiscoveryWellDetailsSummaryView" -->
<#-- @ftlvariable name="trapPropertiesSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryView" -->
<#-- @ftlvariable name="resourceDetailSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailSummaryView" -->

<#if !submittable>
  <#assign notificationBanner>
      <@banner.submissionSummaryNotificationBanner isRegulator=isRegulator/>
  </#assign>
<#else>
  <#assign notificationBanner = ""/>
</#if>

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH
  wrapperWidth=true
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap
  notificationBanner=notificationBanner>
  <@fdsForm.htmlForm>
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
      <#if !submittable && isRegulator>
        <@fdsCheckbox.checkbox
          labelText="I want to complete this submission without providing all the required information"
          path="form.completeWithoutMandatoryInformation"/>
      </#if>
    <#if isRegulator || submittable>
      <@fdsAction.submitButtons
        primaryButtonText="Submit"
        secondaryLinkText="Back to task list"
        linkSecondaryAction=true
        linkSecondaryActionUrl="${springUrl(cancelUrl)}"/>
    <#else>
      <@fdsAction.link linkText="Back to task list" linkUrl="${springUrl(cancelUrl)}"/>
    </#if>
  </@fdsForm.htmlForm>
</@defaultPage>
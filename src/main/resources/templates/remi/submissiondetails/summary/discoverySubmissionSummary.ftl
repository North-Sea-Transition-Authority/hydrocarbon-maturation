<#include '../../layout/layout.ftl'>

<#import '_discoverySubmissionSummary.ftl' as discoverySubmissionSummary>

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

<#if !submittable>
    <#assign notificationBanner>
        <@fdsNotificationBanner.notificationBannerInfo fullWidth=true bannerTitleText="Missing information">
            <@fdsNotificationBanner.notificationBannerContent headingText="Not all sections shown on the task list have been completed.">
              You will be allowed to submit without this information but this will mean the information will not be available in
              the approved view of the resource.
            </@fdsNotificationBanner.notificationBannerContent>
        </@fdsNotificationBanner.notificationBannerInfo>
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
      sourceAndSealSummaryView=sourceAndSealSummaryView/>
      <#if !submittable>
          <@fdsCheckbox.checkbox
          labelText="I want to complete this submission without providing all the required information"
          path="form.completeWithoutMandatoryInformation"/>
      </#if>
    <@fdsAction.submitButtons
      primaryButtonText="Submit"
      secondaryLinkText="Cancel"
      linkSecondaryAction=true
      linkSecondaryActionUrl="${springUrl(cancelUrl)}"/>
  </@fdsForm.htmlForm>
</@defaultPage>
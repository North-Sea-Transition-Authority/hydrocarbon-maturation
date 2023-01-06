<#include '../../layout/layout.ftl'>

<#import '_discoverySubmissionSummary.ftl' as discoverySubmissionSummary>

<#assign pageTitle = "Check your answers before submitting" />
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
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

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH
  wrapperWidth=true
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
  <@fdsForm.htmlForm>
    <@discoverySubmissionSummary.discoverySubmissionSummary
      generalDetailsSummaryView=generalDetailsSummaryView
      permeabilitySummaryView=permeabilitySummaryView
      oilOnlyDiscovery=oilOnlyDiscovery
      gasOnlyDiscovery=gasOnlyDiscovery
      mixedDiscovery=mixedDiscovery
      oilFluidPropertiesSummaryView=oilFluidPropertiesSummaryView
      gasFluidPropertiesSummaryView=gasFluidPropertiesSummaryView
      generalFluidPropertiesSummaryView=generalFluidPropertiesSummaryView/>

    <@fdsAction.submitButtons
      primaryButtonText="Submit"
      secondaryLinkText="Cancel"
      linkSecondaryAction=true
      linkSecondaryActionUrl="${springUrl(cancelUrl)}"/>
  </@fdsForm.htmlForm>
</@defaultPage>
<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilitydistribution.ftl' as probabilityDistribution>

<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="pageTitle" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="oilInPlaceTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="oilInPlaceError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="oilRecoverableResourcesTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="oilRecoverableResourcesError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="associatedGasRecoverableResourcesTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="associatedGasRecoverableResourcesError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="oilEquivalentRecoverableResourcesTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="oilEquivalentRecoverableResourcesError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="gasInPlaceTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="gasInPlaceError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="gasRecoverableResourcesTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="gasRecoverableResourcesError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="associatedCondensateRecoverableResourcesTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="associatedCondensateRecoverableResourcesError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
  <@fdsForm.htmlForm>
    <@probabilityDistribution.probabilityDistributionSingularTemplate
      probabilityDistributionPath="form.oilInPlace"
      labelText="Oil in place"
      template=oilInPlaceTemplate
      error=oilInPlaceError/>

    <@probabilityDistribution.probabilityDistributionSingularTemplate
      probabilityDistributionPath="form.oilRecoverableResources"
      labelText="Oil recoverable resources"
      template=oilRecoverableResourcesTemplate
      error=oilRecoverableResourcesError/>

    <#if resourceType == 'PROSPECT'>
      <@fdsRadio.radioGroup hiddenContent=true path="form.hasAssociatedGasRecoverableResources"
      labelText="Do you expect to find associated gas recoverable resources?">
          <@fdsRadio.radioYes path="form.hasAssociatedGasRecoverableResources">
              <@probabilityDistribution.probabilityDistributionSingularTemplate
              probabilityDistributionPath="form.associatedGasRecoverableResources"
              labelText="Associated gas recoverable resources"
              template=associatedGasRecoverableResourcesTemplate
              error=associatedGasRecoverableResourcesError/>
          </@fdsRadio.radioYes>
          <@fdsRadio.radioNo path="form.hasAssociatedGasRecoverableResources"/>
      </@fdsRadio.radioGroup>
      <#else>
          <@probabilityDistribution.probabilityDistributionSingularTemplate
          probabilityDistributionPath="form.associatedGasRecoverableResources"
          labelText="Associated gas recoverable resources"
          template=associatedGasRecoverableResourcesTemplate
          error=associatedGasRecoverableResourcesError/>
      </#if>

    <@probabilityDistribution.probabilityDistributionSingularTemplate
      probabilityDistributionPath="form.gasInPlace"
      labelText="Gas in place"
      template=gasInPlaceTemplate
      error=gasInPlaceError/>

    <@probabilityDistribution.probabilityDistributionSingularTemplate
      probabilityDistributionPath="form.gasRecoverableResources"
      labelText="Gas recoverable resources"
      template=gasRecoverableResourcesTemplate
      error=gasRecoverableResourcesError/>

    <@fdsRadio.radioGroup
      path="form.hasAssociatedCondensateRecoverableResources"
      labelText="Do you expect to find associated condensate recoverable resources?"
      hiddenContent=true>
          <@fdsRadio.radioYes path="form.hasAssociatedCondensateRecoverableResources">
              <@probabilityDistribution.probabilityDistributionSingularTemplate
              probabilityDistributionPath="form.associatedCondensateRecoverableResources"
              labelText="Associated condensate recoverable resources"
              template=associatedCondensateRecoverableResourcesTemplate
              error=associatedCondensateRecoverableResourcesError/>
          </@fdsRadio.radioYes>
          <@fdsRadio.radioNo path="form.hasAssociatedCondensateRecoverableResources"/>
      </@fdsRadio.radioGroup>

    <@probabilityDistribution.probabilityDistributionSingularTemplate
      probabilityDistributionPath="form.oilEquivalentRecoverableResources"
      labelText="Oil equivalent recoverable resources"
      template=oilEquivalentRecoverableResourcesTemplate
      error=oilEquivalentRecoverableResourcesError/>

    <#if resourceType == 'PROSPECT'>
        <@probabilityDistribution.probabilityDistributionSingularTemplate
        probabilityDistributionPath="form.riskedOilEquivalentRecoverableMean"
        labelText="Risked oil equivalent recoverable"
        template=riskedOilEquivalentRecoverableMeanTemplate
        error=riskedOilEquivalentRecoverableMeanError/>
    </#if>

    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>

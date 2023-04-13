<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilitydistribution.ftl' as probabilityDistribution>

<#assign pageTitle = "Oil case outputs (resources)"/>
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="oilInPlaceTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="oilInPlaceError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="oilRecoverableResourcesTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="oilRecoverableResourcesError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="associatedGasRecoverableResourcesTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="associatedGasRecoverableResourcesError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="oilEquivalentRecoverableResourcesTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="oilEquivalentRecoverableResourcesError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>

  <@fdsForm.htmlForm>
    <@probabilityDistribution.probabilityDistributionCustomTemplate
      probabilityDistributionPath="form.oilInPlace"
      labelText="Oil in place"
      template=oilInPlaceTemplate
      error=oilInPlaceError/>

    <@probabilityDistribution.probabilityDistributionCustomTemplate
      probabilityDistributionPath="form.oilRecoverableResources"
      labelText="Oil recoverable resources"
      template=oilRecoverableResourcesTemplate
      error=oilRecoverableResourcesError/>

    <@fdsRadio.radioGroup
      path="form.hasAssociatedGasRecoverableResources"
      labelText="Do you expect to find associated gas recoverable resources?"
      hiddenContent=true>
      <@fdsRadio.radioYes path="form.hasAssociatedGasRecoverableResources">
        <@probabilityDistribution.probabilityDistributionCustomTemplate
          probabilityDistributionPath="form.associatedGasRecoverableResources"
          labelText="Associated gas recoverable resources"
          template=associatedGasRecoverableResourcesTemplate
          error=associatedGasRecoverableResourcesError/>
      </@fdsRadio.radioYes>
      <@fdsRadio.radioNo path="form.hasAssociatedGasRecoverableResources"/>
    </@fdsRadio.radioGroup>

    <@probabilityDistribution.probabilityDistributionCustomTemplate
      probabilityDistributionPath="form.oilEquivalentRecoverableResources"
      labelText="Oil equivalent recoverable resources"
      template=oilEquivalentRecoverableResourcesTemplate
      error=oilEquivalentRecoverableResourcesError/>

    <@fdsTextInput.textInput path="form.riskedOilEquivalentRecoverableMean.decimalInputValue" labelText="Risked oil equivalent recoverable mean" inputClass="govuk-input--width-3"/>

    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>

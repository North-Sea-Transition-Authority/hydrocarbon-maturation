<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilitydistribution.ftl' as probabilityDistribution>

<#assign pageTitle = "Mixed case outputs (resources)"/>
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
    <@probabilityDistribution.probabilityDistributionCustomTemplate
      probabilityDistributionTypePath="form.oilInPlace.probabilityDistributionType"
      labelText="Oil in place"
      formId="oilInPlace"
      template=oilInPlaceTemplate
      error=oilInPlaceError/>

    <@probabilityDistribution.probabilityDistributionCustomTemplate
      probabilityDistributionTypePath="form.oilRecoverableResources.probabilityDistributionType"
      labelText="Oil recoverable resources"
      formId="oilRecoverableResources"
      template=oilRecoverableResourcesTemplate
      error=oilRecoverableResourcesError/>

    <@fdsRadio.radioGroup hiddenContent=true path="form.hasAssociatedGasRecoverableResources"
      labelText="Do you expect to find associated gas recoverable resources?">
      <@fdsRadio.radioYes path="form.hasAssociatedGasRecoverableResources">
        <@probabilityDistribution.probabilityDistributionCustomTemplate
          probabilityDistributionTypePath="form.associatedGasRecoverableResources.probabilityDistributionType"
          labelText="Associated gas recoverable resources"
          formId="associatedGasRecoverableResources"
          template=associatedGasRecoverableResourcesTemplate
          error=associatedGasRecoverableResourcesError/>
      </@fdsRadio.radioYes>
      <@fdsRadio.radioNo path="form.hasAssociatedGasRecoverableResources"/>
    </@fdsRadio.radioGroup>

    <@probabilityDistribution.probabilityDistributionCustomTemplate
      probabilityDistributionTypePath="form.gasInPlace.probabilityDistributionType"
      labelText="Gas in place"
      formId="gasInPlace"
      template=gasInPlaceTemplate
      error=gasInPlaceError/>

    <@probabilityDistribution.probabilityDistributionCustomTemplate
      probabilityDistributionTypePath="form.gasRecoverableResources.probabilityDistributionType"
      labelText="Gas recoverable resources"
      formId="gasRecoverableResources"
      template=gasRecoverableResourcesTemplate
      error=gasRecoverableResourcesError/>

    <@probabilityDistribution.probabilityDistributionCustomTemplate
      probabilityDistributionTypePath="form.associatedCondensateRecoverableResources.probabilityDistributionType"
      labelText="Associated condensate recoverable resources"
      formId="associatedCondensateRecoverableResources"
      template=associatedCondensateRecoverableResourcesTemplate
      error=associatedCondensateRecoverableResourcesError/>

    <@probabilityDistribution.probabilityDistributionCustomTemplate
      probabilityDistributionTypePath="form.oilEquivalentRecoverableResources.probabilityDistributionType"
      labelText="Oil equivalent recoverable resources"
      formId="oilEquivalentRecoverableResources"
      template=oilEquivalentRecoverableResourcesTemplate
      error=oilEquivalentRecoverableResourcesError/>

    <@fdsTextInput.textInput path="form.riskedOilEquivalentRecoverableMean.decimalInputValue" labelText="Risked oil equivalent recoverable mean" inputClass="govuk-input--width-3"/>

    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>

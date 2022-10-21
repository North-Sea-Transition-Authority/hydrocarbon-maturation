<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilitydistribution.ftl' as probabilityDistribution>

<#assign pageTitle = "Gas case outputs (resources)"/>
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="gasInPlaceTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="gasInPlaceError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="gasRecoverableResourcesTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="gasRecoverableResourcesError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="associatedCondensateRecoverableResourcesTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="associatedCondensateRecoverableResourcesError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
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

    <@fdsRadio.radioGroup
      path="form.hasAssociatedCondensateRecoverableResources"
      labelText="Do you expect to find associated condensate recoverable resources?"
      hiddenContent=true>
        <@fdsRadio.radioYes path="form.hasAssociatedCondensateRecoverableResources">
          <@probabilityDistribution.probabilityDistributionCustomTemplate
            probabilityDistributionTypePath="form.associatedCondensateRecoverableResources.probabilityDistributionType"
            labelText="Associated condensate recoverable resources"
            formId="associatedCondensateRecoverableResources"
            template=associatedCondensateRecoverableResourcesTemplate
            error=associatedCondensateRecoverableResourcesError/>
        </@fdsRadio.radioYes>
        <@fdsRadio.radioNo path="form.hasAssociatedCondensateRecoverableResources"/>
    </@fdsRadio.radioGroup>

    <@probabilityDistribution.probabilityDistributionCustomTemplate
      probabilityDistributionTypePath="form.oilEquivalentRecoverableResources.probabilityDistributionType"
      labelText="Oil equivalent recoverable resources"
      formId="oilEquivalentRecoverableResources"
      template=oilEquivalentRecoverableResourcesTemplate
      error=oilEquivalentRecoverableResourcesError/>

    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>

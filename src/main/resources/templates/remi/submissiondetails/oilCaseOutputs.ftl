<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilitydistribution.ftl' as probabilityDistribution>

<#assign pageTitle = "Oil case ouputs (resources)"/>
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
      probabilityDistributionTypePath="form.oilInPlace.probabilityDistributionType"
      labelText="Formation volume factor distribution used"
      formId="oilInPlace"
      template=oilInPlaceTemplate
      error=oilInPlaceError/>

    <@probabilityDistribution.probabilityDistributionCustomTemplate
    probabilityDistributionTypePath="form.oilRecoverableResources.probabilityDistributionType"
      labelText="Oil hydrocarbon saturation distribution used"
      formId="oilRecoverableResources"
      template=oilRecoverableResourcesTemplate
      error=oilRecoverableResourcesError/>

    <@probabilityDistribution.probabilityDistributionCustomTemplate
    probabilityDistributionTypePath="form.associatedGasRecoverableResources.probabilityDistributionType"
      labelText="Associated gas-oil ratio, life-of-field distribution used"
      formId="associatedGasRecoverableResources"
      template=associatedGasRecoverableResourcesTemplate
      error=associatedGasRecoverableResourcesError/>

    <@probabilityDistribution.probabilityDistributionCustomTemplate
    probabilityDistributionTypePath="form.oilEquivalentRecoverableResources.probabilityDistributionType"
      labelText="Oil recovery factor distribution used"
      formId="oilEquivalentRecoverableResources"
      template=oilEquivalentRecoverableResourcesTemplate
      error=oilEquivalentRecoverableResourcesError/>

    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>

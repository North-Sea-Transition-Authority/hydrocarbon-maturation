<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilitydistribution.ftl' as probabilityDistribution>
<#import './_hydrocarbonCaseInsetText.ftl' as hydrocarbonCaseInsetText>

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

<#assign submissionTypeOrBlank = submissionType!""/>
<#assign showReserves = submissionTypeOrBlank != "CREATE_CSR"/>
<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
  <@fdsForm.htmlForm>
      <#if hydrocarbonCase?has_content>
          <@hydrocarbonCaseInsetText.hydrocarbonCaseInsetText hydrocarbonCase=hydrocarbonCase/>
      </#if>

      <@probabilityDistribution.probabilityDistributionSingularTemplate
        probabilityDistributionPath="form.gasInPlace"
        labelText="Gas initially in place"
        template=gasInPlaceTemplate
        error=gasInPlaceError/>

      <#if resourceType != 'FIELD'>
        <@probabilityDistribution.probabilityDistributionSingularTemplate
          probabilityDistributionPath="form.gasRecoverableResources"
          labelText="Gas recoverable resources"
          template=gasRecoverableResourcesTemplate
          error=gasRecoverableResourcesError/>
      </#if>

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

    <#if resourceType == 'FIELD'>
      <@probabilityDistribution.probabilityDistributionSingularTemplate
        probabilityDistributionPath="form.naturalGasLiquidsResources"
        labelText="Natural gas liquids resources"
        template=naturalGasLiquidsResourcesTemplate
        error=naturalGasLiquidsResourcesError/>

      <@probabilityDistribution.probabilityDistributionSingularTemplate
        probabilityDistributionPath="form.salesGasResources"
        labelText="Total sales gas resources"
        template=salesGasResourcesTemplate
        error=salesGasResourcesError/>
    </#if>

    <#if resourceType == 'FIELD' && showReserves>
      <@probabilityDistribution.probabilityDistributionSingularTemplate
        probabilityDistributionPath="form.associatedCondensateRecoverableReserves"
        labelText="Associated condensate reserves"
        template=associatedCondensateRecoverableReservesTemplate
        error=associatedCondensateRecoverableReservesError/>

      <@probabilityDistribution.probabilityDistributionSingularTemplate
        probabilityDistributionPath="form.naturalGasLiquidsReserves"
        labelText="Natural gas liquids reserves"
        template=naturalGasLiquidsReservesTemplate
        error=naturalGasLiquidsReservesError/>

      <@probabilityDistribution.probabilityDistributionSingularTemplate
        probabilityDistributionPath="form.salesGasReserves"
        labelText="Sales gas reserves"
        template=salesGasReservesTemplate
        error=salesGasReservesError/>
    </#if>

    <@probabilityDistribution.probabilityDistributionSingularTemplate
      probabilityDistributionPath="form.oilEquivalentRecoverableResources"
      labelText="Oil equivalent recoverable resources"
      template=oilEquivalentRecoverableResourcesTemplate
      error=oilEquivalentRecoverableResourcesError/>

    <#if resourceType == 'FIELD' && showReserves>
      <@probabilityDistribution.probabilityDistributionSingularTemplate
        probabilityDistributionPath="form.oilEquivalentRecoverableReserves"
        labelText="Oil equivalent reserves"
        template=oilEquivalentRecoverableReservesTemplate
        error=oilEquivalentRecoverableReservesError/>
    </#if>

    <#if fieldSubmissionCarriedForward?? && fieldSubmissionCarriedForward==true>
        <@fdsCheckbox.checkboxItem
        labelText="The field volumetric information reflects the current state of the field"
        path="form.fieldVolumetricInformationCorrect"/>
    </#if>

    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>

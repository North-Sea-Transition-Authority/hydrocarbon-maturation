<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilitydistribution.ftl' as probabilityDistribution>

<#assign pageTitle = "Gas case volumetrics parameters" />
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="gasFormationVolumeFactorTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="gasFormationVolumeFactorError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="gasHydrocarbonSaturationTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="gasHydrocarbonSaturationError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="gasExpansionFactorTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="gasExpansionFactorError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="condensateGasRatioLifeOfFieldTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="condensateGasRatioLifeOfFieldError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="gasRecoveryFactorTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="gasRecoveryFactorError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
  <@fdsForm.htmlForm>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.gasFormationVolumeFactor"
      labelText="Gas formation volume factor (Bg)"
      template=gasFormationVolumeFactorTemplate
      error=gasFormationVolumeFactorError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.gasHydrocarbonSaturation"
      labelText="Gas hydrocarbon saturation distribution used"
      template=gasHydrocarbonSaturationTemplate
      error=gasHydrocarbonSaturationError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.gasExpansionFactor"
      labelText="Gas expansion factor distribution used"
      template=gasExpansionFactorTemplate
      error=gasExpansionFactorError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.condensateGasRatioLifeOfField"
      labelText="Condensate-gas ratio, life-of-field distribution used"
      template=condensateGasRatioLifeOfFieldTemplate
      error=condensateGasRatioLifeOfFieldError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.gasRecoveryFactor"
      labelText="Gas recovery factor distribution used"
      template=gasRecoveryFactorTemplate
      error=gasRecoveryFactorError/>

    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>

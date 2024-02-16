<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilitydistribution.ftl' as probabilityDistribution>

<#assign pageTitle = "Mixed case volumetrics parameters" />
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="oilFormationVolumeFactorTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="oilFormationVolumeFactorError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="oilHydrocarbonSaturationTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="oilHydrocarbonSaturationError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="gasHydrocarbonSaturationTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="gasHydrocarbonSaturationError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="associatedGasOilRatioLifeOfFieldTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="associatedGasOilRatioLifeOfFieldError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="condensateGasRatioLifeOfFieldTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="condensateGasRatioLifeOfFieldError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="oilRecoveryFactorTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="oilRecoveryFactorError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="gasRecoveryFactorTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="gasRecoveryFactorError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="gasFormationVolumeFactorTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="gasFormationVolumeFactorError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
  <@fdsForm.htmlForm>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.oilFormationVolumeFactor"
      labelText="Oil formation volume factor (Bo)"
      template=oilFormationVolumeFactorTemplate
      error=oilFormationVolumeFactorError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.oilHydrocarbonSaturation"
      labelText="Oil hydrocarbon saturation"
      template=oilHydrocarbonSaturationTemplate
      error=oilHydrocarbonSaturationError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.associatedGasOilRatioLifeOfField"
      labelText="Associated gas-oil ratio, life-of-field"
      template=associatedGasOilRatioLifeOfFieldTemplate
      error=associatedGasOilRatioLifeOfFieldError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.oilRecoveryFactor"
      labelText="Oil recovery factor"
      template=oilRecoveryFactorTemplate
      error=oilRecoveryFactorError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.gasFormationVolumeFactor"
      labelText="Gas formation volume factor (Bg)"
      template=gasFormationVolumeFactorTemplate
      error=gasFormationVolumeFactorError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.gasHydrocarbonSaturation"
      labelText="Gas hydrocarbon saturation"
      template=gasHydrocarbonSaturationTemplate
      error=gasHydrocarbonSaturationError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.condensateGasRatioLifeOfField"
      labelText="Condensate-gas ratio, life-of-field"
      template=condensateGasRatioLifeOfFieldTemplate
      error=condensateGasRatioLifeOfFieldError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.gasRecoveryFactor"
      labelText="Gas recovery factor"
      template=gasRecoveryFactorTemplate
      error=gasRecoveryFactorError/>

    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>

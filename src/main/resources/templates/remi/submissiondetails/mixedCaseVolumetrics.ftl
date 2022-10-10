<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilitydistribution.ftl' as probabilityDistribution>

<#assign pageTitle = "Mixed case volumetrics parameters" />
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="formationVolumeFactorTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="formationVolumeFactorError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="oilHydrocarbonSaturationTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="oilHydrocarbonSaturationError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="gasHydrocarbonSaturationTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="gasHydrocarbonSaturationError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="associatedGasOilRatioLifeOfFieldTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="associatedGasOilRatioLifeOfFieldError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="gasExpansionFactorTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="gasExpansionFactorError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="condensateGasRatioLifeOfFieldTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="condensateGasRatioLifeOfFieldError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="oilRecoveryFactorTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="oilRecoveryFactorError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
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
      probabilityDistributionPath="form.formationVolumeFactor"
      labelText="Formation volume factor distribution used"
      formId="formationVolumeFactor"
      template=formationVolumeFactorTemplate
      error=formationVolumeFactorError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.oilHydrocarbonSaturation"
      labelText="Oil hydrocarbon saturation distribution used"
      formId="oilHydrocarbonSaturation"
      template=oilHydrocarbonSaturationTemplate
      error=oilHydrocarbonSaturationError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.associatedGasOilRatioLifeOfField"
      labelText="Associated gas-oil ratio, life-of-field distribution used"
      formId="associatedGasOilRatioLifeOfField"
      template=associatedGasOilRatioLifeOfFieldTemplate
      error=associatedGasOilRatioLifeOfFieldError
      optionalLabel=true/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.oilRecoveryFactor"
      labelText="Oil recovery factor distribution used"
      formId="oilRecoveryFactor"
      template=oilRecoveryFactorTemplate
      error=oilRecoveryFactorError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.gasHydrocarbonSaturation"
      labelText="Gas hydrocarbon saturation distribution used"
      formId="gasHydrocarbonSaturation"
      template=gasHydrocarbonSaturationTemplate
      error=gasHydrocarbonSaturationError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.gasExpansionFactor"
      labelText="Gas expansion factor distribution used"
      formId="gasExpansionFactor"
      template=gasExpansionFactorTemplate
      error=gasExpansionFactorError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.condensateGasRatioLifeOfField"
      labelText="Condensate-gas ratio, life-of-field distribution used"
      formId="condensateGasRatioLifeOfField"
      template=condensateGasRatioLifeOfFieldTemplate
      error=condensateGasRatioLifeOfFieldError
      optionalLabel=true/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.gasRecoveryFactor"
      labelText="Gas recovery factor distribution used"
      formId="gasRecoveryFactor"
      template=gasRecoveryFactorTemplate
      error=gasRecoveryFactorError/>

    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>

<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilitydistribution.ftl' as probabilityDistribution>

<#assign pageTitle = "Oil case volumetrics parameters" />
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="oilHydrocarbonSaturationTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="oilHydrocarbonSaturationError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="formationVolumeFactorTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="formationVolumeFactorError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="associatedGasOilRatioLifeOfFieldTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="associatedGasOilRatioLifeOfFieldError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="oilRecoveryFactorTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="oilRecoveryFactorError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>

  <@fdsForm.htmlForm>
    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionTypePath="form.formationVolumeFactor.probabilityDistributionType"
      labelText="Formation volume factor distribution used"
      formId="formationVolumeFactor"
      template=formationVolumeFactorTemplate
      error=formationVolumeFactorError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionTypePath="form.oilHydrocarbonSaturation.probabilityDistributionType"
      labelText="Oil hydrocarbon saturation distribution used"
      formId="oilHydrocarbonSaturation"
      template=oilHydrocarbonSaturationTemplate
      error=oilHydrocarbonSaturationError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionTypePath="form.associatedGasOilRatioLifeOfField.probabilityDistributionType"
      labelText="Associated gas-oil ratio, life-of-field distribution used"
      formId="associatedGasOilRatioLifeOfField"
      template=associatedGasOilRatioLifeOfFieldTemplate
      error=associatedGasOilRatioLifeOfFieldError
      optionalLabel=true/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionTypePath="form.oilRecoveryFactor.probabilityDistributionType"
      labelText="Oil recovery factor distribution used"
      formId="oilRecoveryFactor"
      template=oilRecoveryFactorTemplate
      error=oilRecoveryFactorError/>

    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>

<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilitydistribution.ftl' as probabilityDistribution>

<#assign pageTitle = "Reservoir properties" />
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="averageNetPayThicknessTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="averageNetPayThicknessError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="grossReservoirThicknessTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="grossReservoirThicknessError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="grossRockVolumeTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="grossRockVolumeError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="percentOfGrvOilBearingTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="percentOfGrvOilBearingError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="netToGrossTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="netToGrossError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="netPorosityTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="netPorosityError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="depthToHydrocarbonContactTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="depthToHydrocarbonContactError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="depthToOilWaterContactTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="depthToOilWaterContactError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="depthToGasWaterContactTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="depthToGasWaterContactError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="depthToGasOilContactTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="depthToGasOilContactError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.FULL_WIDTH
errorItems=errorList
breadcrumbsMap=breadcrumbsMap>

  <@fdsForm.htmlForm>
    <@fdsFieldset.fieldset>
      <@fdsTextInput.textInput path="form.formationName" labelText="Formation name"/>
      <@fdsTextInput.textInput path="form.ageStageOrSeries" labelText="Age stage or series"/>
      <@fdsTextInput.textInput path="form.lithology" labelText="Lithology"/>
     </@fdsFieldset.fieldset>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionTypePath="form.averageNetPayThickness.probabilityDistributionType"
      labelText="Average net pay thickness distribution used"
      formId="averageNetPayThickness"
      template=averageNetPayThicknessTemplate
      error=averageNetPayThicknessError
      optionalLabel=true/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionTypePath="form.grossReservoirThickness.probabilityDistributionType"
      labelText="Gross reservoir thickness distribution used"
      formId="grossReservoirThickness"
      template=grossReservoirThicknessTemplate
      error=grossReservoirThicknessError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionTypePath="form.grossRockVolume.probabilityDistributionType"
      labelText="Gross rock volume (GRV) distribution used"
      formId="grossRockVolume"
      template=grossRockVolumeTemplate
      error=grossRockVolumeError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionTypePath="form.percentOfGrvOilBearing.probabilityDistributionType"
      labelText="% of GRV which is oil bearing distribution used"
      formId="percentOfGrvOilBearing"
      template=percentOfGrvOilBearingTemplate
      error=percentOfGrvOilBearingError
      optionalLabel=true/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionTypePath="form.netToGross.probabilityDistributionType"
      labelText="Net-to-gross distribution used"
      formId="netToGross"
      template=netToGrossTemplate
      error=netToGrossError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionTypePath="form.netPorosity.probabilityDistributionType"
      labelText="Net porosity distribution used"
      formId="netPorosity"
      template=netPorosityTemplate
      error=netPorosityError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionTypePath="form.depthToHydrocarbonContact.probabilityDistributionType"
      labelText="Depth to hydrocarbon contact distribution used"
      formId="depthToHydrocarbonContact"
      template=depthToHydrocarbonContactTemplate
      error=depthToHydrocarbonContactError/>

    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>
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
      <#if submissionType == "POST_DRILL_DRY_HOLE">
        <@fdsTextarea.textarea
          path="form.reservoirFacies"
          labelText="Reservoir facies"
          optionalLabel=true
          hintText="Describe the facies, depositional environments and impact on reservoir development and performance"/>
        <@fdsTextarea.textarea
          path="form.reservoirConnectivity"
          optionalLabel=true
          labelText="Reservoir connectivity"
          hintText="Comment on sandbody geometry and architecture, fault compartmentalisation, aquifer size and connectivity"/>
      </#if>
     </@fdsFieldset.fieldset>
    <#if submissionType == "CREATE_PROSPECT" || submissionType == "PROSPECT_UPDATE">
      <@probabilityDistribution.probabilityDistributionTemplate
        probabilityDistributionTypePath="form.averageNetPayThickness.probabilityDistributionType"
        labelText="Average net pay thickness distribution used"
        formId="averageNetPayThickness"
        template=averageNetPayThicknessTemplate
        error=averageNetPayThicknessError/>
    <#elseif submissionType == "POST_DRILL_DRY_HOLE" || submissionType == "CREATE_DISCOVERY">
      <@probabilityDistribution.probabilityDistributionTemplate
        probabilityDistributionTypePath="form.averageNetPayThickness.probabilityDistributionType"
        labelText="Average net pay thickness distribution used"
        formId="averageNetPayThickness"
        template=averageNetPayThicknessOptionalTemplate
        error=averageNetPayThicknessOptionalError/>
    </#if>
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
      error=percentOfGrvOilBearingError/>

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

    <#if submissionType == "CREATE_PROSPECT" || submissionType == "PROSPECT_UPDATE">
      <@probabilityDistribution.probabilityDistributionTemplate
        probabilityDistributionTypePath="form.depthToHydrocarbonContact.probabilityDistributionType"
        labelText="Depth to hydrocarbon contact distribution used"
        formId="depthToHydrocarbonContact"
        template=depthToHydrocarbonContactTemplate
        error=depthToHydrocarbonContactError/>
    </#if>
    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>
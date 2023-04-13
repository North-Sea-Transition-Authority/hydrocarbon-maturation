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
    <@grid.gridRow>
      <@grid.twoThirdsColumn>
        <@fdsFieldset.fieldset>
            <@fdsTextInput.textInput path="form.formationName" labelText="Formation name"/>
            <@fdsTextInput.textInput path="form.ageStageOrSeries" labelText="Age stage or series"/>
            <@fdsTextInput.textInput path="form.lithology" labelText="Lithology"/>
            <#if dryHoleSubmission>
              <#assign optionalFaciesAndConnectivity = true />
            </#if>
            <#if discoverySubmission>
              <#assign optionalFaciesAndConnectivity = false/>
            </#if>
            <#if dryHoleSubmission || discoverySubmission>
              <@fdsTextarea.textarea
                path="form.reservoirFacies"
                labelText="Reservoir facies"
                optionalLabel=optionalFaciesAndConnectivity
                hintText="Describe the facies, depositional environments and impact on reservoir development and performance"/>
              <@fdsTextarea.textarea
                path="form.reservoirConnectivity"
                optionalLabel=optionalFaciesAndConnectivity
                labelText="Reservoir connectivity"
                hintText="Comment on sandbody geometry and architecture, fault compartmentalisation, aquifer size and connectivity"/>
            </#if>
        </@fdsFieldset.fieldset>
      </@grid.twoThirdsColumn>
    </@grid.gridRow>
    <#if prospectSubmission>
      <@probabilityDistribution.probabilityDistributionTemplate
        probabilityDistributionPath="form.averageNetPayThickness"
        labelText="Average net pay thickness distribution used"
        template=averageNetPayThicknessTemplate
        error=averageNetPayThicknessError/>
    <#elseif dryHoleSubmission || discoverySubmission>
      <@probabilityDistribution.probabilityDistributionTemplate
        probabilityDistributionPath="form.averageNetPayThickness"
        labelText="Average net pay thickness distribution used"
        template=averageNetPayThicknessOptionalTemplate
        error=averageNetPayThicknessOptionalError/>
    </#if>
    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.grossReservoirThickness"
      labelText="Gross reservoir thickness distribution used"
      template=grossReservoirThicknessTemplate
      error=grossReservoirThicknessError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.grossRockVolume"
      labelText="Gross rock volume (GRV) distribution used"
      template=grossRockVolumeTemplate
      error=grossRockVolumeError/>

    <#if dryHoleSubmission || mixedCase>
      <@probabilityDistribution.probabilityDistributionTemplate
        probabilityDistributionPath="form.percentOfGrvOilBearing"
        labelText="% of GRV which is oil bearing distribution used"
        template=percentOfGrvOilBearingTemplate
        error=percentOfGrvOilBearingError/>
    </#if>
      
    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.netToGross"
      labelText="Net-to-gross distribution used"
      template=netToGrossTemplate
      error=netToGrossError/>

    <@probabilityDistribution.probabilityDistributionTemplate
      probabilityDistributionPath="form.netPorosity"
      labelText="Net porosity distribution used"
      template=netPorosityTemplate
      error=netPorosityError/>

    <#if prospectSubmission>
      <@probabilityDistribution.probabilityDistributionTemplate
        probabilityDistributionPath="form.depthToHydrocarbonContact"
        labelText="Depth to hydrocarbon contact distribution used"
        template=depthToHydrocarbonContactTemplate
        error=depthToHydrocarbonContactError/>
    </#if>
    <#if discoverySubmission>
      <#if oilCase || mixedCase>
        <@probabilityDistribution.probabilityDistributionCustomTemplate
          probabilityDistributionPath="form.depthToOilWaterContact"
          labelText="Depth to oil water contact"
          template=depthToOilWaterContactTemplate
          error=depthToOilWaterContactError/>
      </#if>
      <#if gasCase || mixedCase>
        <@probabilityDistribution.probabilityDistributionCustomTemplate
          probabilityDistributionPath="form.depthToGasWaterContact"
          labelText="Depth to gas water contact"
          template=depthToGasWaterContactTemplate
          error=depthToGasWaterContactError/>
      </#if>
      <#if mixedCase>
        <@probabilityDistribution.probabilityDistributionCustomTemplate
          probabilityDistributionPath="form.depthToGasOilContact"
          labelText="Depth to gas oil contact"
          template=depthToGasOilContactTemplate
          error=depthToGasOilContactError/>
      </#if>
    </#if>
    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>
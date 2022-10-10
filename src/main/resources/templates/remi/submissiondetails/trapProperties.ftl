<#include '../layout/layout.ftl'>
<#import '../coordinates/coordinateInput.ftl' as coordinateInput/>
<#import '../probabilitydistribution/probabilitydistribution.ftl' as probabilityDistribution>

<#assign pageTitle = "Trap properties" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="trapTypes" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="longitudeHemisphere" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="depthToCrestTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="depthToCrestError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="depthToSpillPointTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="depthToSpillPointError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="productiveAreaTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="productiveAreaError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>

  <@fdsForm.htmlForm>
      <@fdsTextarea.textarea path="form.trapDescription" labelText="Trap description"
        hintText="For example, Upper Jurassic (Solan) turbidite sandstones, enclosed within and oil sourced by the lower portion of the Kimmeridge Clay Formation"/>

    <@fdsRadio.radioGroup path="form.trapType" labelText="Trap type" hiddenContent=true>
        <#assign firstItem=true/>
        <#list trapTypes as key, value>
            <@fdsRadio.radioItem path="form.trapType" itemMap={key : value} isFirstItem=firstItem>
                <#if key == "OTHER">
                    <@fdsTextInput.textInput path="form.otherTrapType" labelText="Provide the trap type" nestingPath="form.trapType"/>
                </#if>
                <#assign firstItem=false/>
            </@fdsRadio.radioItem>
        </#list>
    </@fdsRadio.radioGroup>

    <@coordinateInput.coordinateInput
      degreesLocationPath="form.latitudeCoordinates.degrees.decimalInputValue"
      minutesLocationPath="form.latitudeCoordinates.minutes.decimalInputValue"
      secondsLocationPath="form.latitudeCoordinates.seconds.decimalInputValue"
      formId="latitude"
      labelText="Latitude"/>

    <@coordinateInput.coordinateInput
      degreesLocationPath="form.longitudeCoordinates.degrees.decimalInputValue"
      minutesLocationPath="form.longitudeCoordinates.minutes.decimalInputValue"
      secondsLocationPath="form.longitudeCoordinates.seconds.decimalInputValue"
      formId="longitude"
      direction="EW"
      directionPath="form.longitudeHemisphere"
      directionList=longitudeHemisphere
      labelText="Longitude"
      hintText="From 0 to 30 for degrees, 0 to 59 for minutes, and 0 to 59.999 for seconds"/>

    <@probabilityDistribution.probabilityDistributionTemplate
        probabilityDistributionTypePath="form.depthToCrest.probabilityDistributionType"
        labelText="Depth to crest distribution used"
        formId="depthToCrest"
        template=depthToCrestTemplate
        error=depthToCrestError/>

    <@probabilityDistribution.probabilityDistributionTemplate
        probabilityDistributionTypePath="form.depthToSpillPoint.probabilityDistributionType"
        labelText="Depth to spill-point distribution used"
        formId="depthToSpillPoint"
        template=depthToSpillPointTemplate
        error=depthToSpillPointError/>

    <@probabilityDistribution.probabilityDistributionTemplate
        probabilityDistributionTypePath="form.productiveArea.probabilityDistributionType"
        labelText="Productive area distribution used"
        formId="productiveArea"
        template=productiveAreaTemplate
        error=productiveAreaError/>

      <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>
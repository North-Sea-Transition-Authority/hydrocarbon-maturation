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
<#-- @ftlvariable name="prospectDepthMapTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadTemplate" -->
<#-- @ftlvariable name="prospectDepthMapUploads" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFileView>" -->

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
      degreesLocationPath="form.coordinateInputForm.latitudeCoordinates.degrees.decimalInputValue"
      minutesLocationPath="form.coordinateInputForm.latitudeCoordinates.minutes.decimalInputValue"
      secondsLocationPath="form.coordinateInputForm.latitudeCoordinates.seconds.decimalInputValue"
      formId="latitude"
      labelText="Centre of interest latitude"
      wgs84Datum=false/>

    <@coordinateInput.coordinateInput
      degreesLocationPath="form.coordinateInputForm.longitudeCoordinates.degrees.decimalInputValue"
      minutesLocationPath="form.coordinateInputForm.longitudeCoordinates.minutes.decimalInputValue"
      secondsLocationPath="form.coordinateInputForm.longitudeCoordinates.seconds.decimalInputValue"
      formId="longitude"
      direction="EW"
      directionPath="form.coordinateInputForm.longitudeHemisphere"
      directionList=longitudeHemisphere
      wgs84Datum=false
      labelText="Centre of interest longitude"
      hintText="From 0 to 30 for degrees, 0 to 59 for minutes, and 0 to 59.999 for seconds"/>
    <@fdsRadio.radioGroup path="form.datum" labelText="Centre of interest datum">
      <#assign firstItem=true/>
      <#list datumTypes as key, value>
        <@fdsRadio.radioItem path="form.datum" itemMap={key : value} isFirstItem=firstItem/>
        <#assign firstItem=false/>
      </#list>
    </@fdsRadio.radioGroup>
    <@fdsFieldset.fieldset legendHeading="Quadrant and block the centre of interest is within"
      legendHeadingClass="govuk-fieldset__legend--s"
      legendHeadingSize="h2"
      hintText="You can only select from quadrants and blocks added in the general details section of the form">
      <@fdsSearchSelector.searchSelectorEnhanced
        path="form.centreOfInterestQuadrantBlock"
        labelHeadingClass="govuk-fieldset__heading"
        pageHeading=false
        options=quadrantAndBlocks
        labelText=""/>
    </@fdsFieldset.fieldset>
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

    <@fdsFieldset.fieldset legendHeading="Prospect depth map" legendHeadingSize="h2" legendHeadingClass="govuk-fieldset__legend--m">
      <@fdsFileUpload.fileUpload
        id="prospectDepthMap"
        path="form.prospectDepthMap"
        downloadUrl=prospectDepthMapTemplate.downloadUrl()
        uploadUrl=prospectDepthMapTemplate.uploadUrl()
        deleteUrl=prospectDepthMapTemplate.deleteUrl()
        maxAllowedSize=prospectDepthMapTemplate.maxAllowedSize()
        allowedExtensions=prospectDepthMapTemplate.allowedExtensions()
        existingFiles=prospectDepthMapUploads
        multiFile=false
      />
    </@fdsFieldset.fieldset>

      <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>
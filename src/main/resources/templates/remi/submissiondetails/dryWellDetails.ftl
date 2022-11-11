<#include '../layout/layout.ftl'>
<#import '../coordinates/coordinateInput.ftl' as coordinateInput/>
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="longitudeHemisphere" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="datumTypes" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="dryHoleTypes" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->


<#assign pageTitle = "Dry well details" />

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
  <@fdsForm.htmlForm>

    <@fdsSearchSelector.searchSelectorRest
      path="form.dryWell"
      restUrl=springUrl(dryWellEndpoint)
      labelText="Principal well"
      selectorMinInputLength=3
      preselectedItems=dryWell!{}/>

    <@coordinateInput.coordinateInput
      degreesLocationPath="form.coordinateInputForm.latitudeCoordinates.degrees.decimalInputValue"
      minutesLocationPath="form.coordinateInputForm.latitudeCoordinates.minutes.decimalInputValue"
      secondsLocationPath="form.coordinateInputForm.latitudeCoordinates.seconds.decimalInputValue"
      formId="latitude"
      labelText="Latitude"
      wgs84Datum=false
    />

    <@coordinateInput.coordinateInput
      degreesLocationPath="form.coordinateInputForm.longitudeCoordinates.degrees.decimalInputValue"
      minutesLocationPath="form.coordinateInputForm.longitudeCoordinates.minutes.decimalInputValue"
      secondsLocationPath="form.coordinateInputForm.longitudeCoordinates.seconds.decimalInputValue"
      formId="longitude"
      direction="EW"
      directionPath="form.coordinateInputForm.longitudeHemisphere"
      directionList=longitudeHemisphere
      labelText="Longitude"
      hintText="From 0 to 30 for degrees, 0 to 59 for minutes, and 0 to 59.999 for seconds"
      wgs84Datum=false
    />

    <@fdsRadio.radioGroup path="form.reservoirDatum" labelText="Datum">
      <#list datumTypes as key, value>
        <@fdsRadio.radioItem path="form.reservoirDatum" itemMap={key : value}/>
      </#list>
    </@fdsRadio.radioGroup>
    <@fdsTextInput.textInput
      path="form.totalDepthFormation"
      labelText="Total depth formation"/>
    <@fdsTextarea.textarea
      path="form.totalDepthFormationComments"
      labelText="Total depth formation comments"
      optionalLabel=true/>
    <@fdsTextarea.textarea
      path="form.dataAcquisitionProgramme"
      labelText="Data acquisition programme"
      hintText="Summarise the data acquisition programme over the objective interval(s) including logs, core and DSTs"
    />
    <@fdsTextarea.textarea
      path="form.geophysicalUpdate"
      labelText="Geophysical update"
      hintText="Provide a description of pre-drill versus post-drill outcomes for seismic picking, depth conversion,
      lithology and fluid prediction. Include the accuracy of rock physics models, seismic attribute volumes,
       the seismic acquisition, processing and derived products used."
      optionalLabel=true
    />

    <h2 class="govuk-heading-l">Dry hole details</h2>
    <@fdsRadio.radioGroup path="form.dryHoleType" labelText="Dry hole type">
      <#list dryHoleTypes as key, value>
        <@fdsRadio.radioItem path="form.dryHoleType" itemMap={key : value}/>
      </#list>
    </@fdsRadio.radioGroup>
    <@fdsTextarea.textarea
      path="form.dryHoleDescription"
      labelText="Dry hole description"
      hintText="Indicate if shows were encountered in the well and if so, over what stratigraphic intervals"
      optionalLabel=true
    />
    <@fdsTextarea.textarea
      path="form.dryHoleScope"
      labelText="Dry hole scope for remaining potential"
      hintText="Based on the location of the well, is there still considered to be a valid prospect on the mapped structure. For example, up-dip potential"
      optionalLabel=true
    />
    <@fdsTextarea.textarea
      path="form.dryHoleComments"
      labelText="Dry hole comments"
      optionalLabel=true
    />
    <h2 class="govuk-heading-l">Well cost</h2>
    <@fdsTextInput.textInput path="form.dryHoleCost.decimalInputValue"
      labelText="Dry hole cost"
      inputClass="govuk-!-width-one-third"
      prefix="£"
      nestingPath="form.dryHoleCost"/>

    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>
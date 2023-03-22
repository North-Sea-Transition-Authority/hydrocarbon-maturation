<#include '../../layout/layout.ftl'>
<#import '../../coordinates/coordinateInput.ftl' as coordinateInput/>

<#macro drillDetails datumTypes longitudeHemisphere wellEndpoint well formPath="form">
  <h2 class="govuk-heading-l">Drill details</h2>
  <@fdsSearchSelector.searchSelectorRest
    path="${formPath}.well"
    restUrl=springUrl(wellEndpoint)
    labelText="Principal well"
    selectorMinInputLength=3
    preselectedItems=well!{}/>

  <@coordinateInput.coordinateInput
    degreesLocationPath="${formPath}.coordinateInputForm.latitudeCoordinates.degrees.decimalInputValue"
    minutesLocationPath="${formPath}.coordinateInputForm.latitudeCoordinates.minutes.decimalInputValue"
    secondsLocationPath="${formPath}.coordinateInputForm.latitudeCoordinates.seconds.decimalInputValue"
    formId="latitude"
    labelText="Top reservoir latitude"
    wgs84Datum=false/>

  <@coordinateInput.coordinateInput
    degreesLocationPath="${formPath}.coordinateInputForm.longitudeCoordinates.degrees.decimalInputValue"
    minutesLocationPath="${formPath}.coordinateInputForm.longitudeCoordinates.minutes.decimalInputValue"
    secondsLocationPath="${formPath}.coordinateInputForm.longitudeCoordinates.seconds.decimalInputValue"
    formId="longitude"
    direction="EW"
    directionPath="${formPath}.coordinateInputForm.longitudeHemisphere"
    directionList=longitudeHemisphere
    labelText="Top reservoir longitude"
    hintText="From 0 to 30 for degrees, 0 to 59 for minutes, and 0 to 59.999 for seconds"
    wgs84Datum=false/>

  <@fdsRadio.radioGroup path="${formPath}.reservoirDatum" labelText="Top reservoir datum">
    <#assign firstItem=true/>
    <#list datumTypes as key, value>
      <@fdsRadio.radioItem path="${formPath}.reservoirDatum" itemMap={key : value} isFirstItem=firstItem/>
      <#assign firstItem=false/>
    </#list>
  </@fdsRadio.radioGroup>
  <@fdsTextInput.textInput
    path="${formPath}.totalDepthFormation"
    labelText="Total depth formation"/>
  <@fdsTextarea.textarea
    path="${formPath}.totalDepthFormationComments"
    labelText="Total depth formation comments"
    optionalLabel=true/>
  <@fdsTextarea.textarea
    path="${formPath}.dataAcquisitionProgramme"
    labelText="Data acquisition programme"
    hintText="Summarise the data acquisition programme over the objective interval(s) including logs, core and DSTs"/>
  <@fdsTextarea.textarea
    path="${formPath}.geophysicalUpdate"
    labelText="Geophysical update"
    hintText="Provide a description of pre-drill versus post-drill outcomes for seismic picking, depth conversion,
      lithology and fluid prediction. Include the accuracy of rock physics models, seismic attribute volumes,
       the seismic acquisition, processing and derived products used."
    optionalLabel=true/>

</#macro>
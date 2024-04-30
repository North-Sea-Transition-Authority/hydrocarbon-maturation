<#include '../layout/layout.ftl'>

<#macro inPlaceVolumetric path hydrocarbon units>
  <@fdsFieldset.fieldset
    legendHeading=hydrocarbon + " in place (" + units + ")"
    legendHeadingSize="h2"
    legendHeadingClass="govuk-fieldset__legend--s">
    <@fdsTextInput.textInput
      path=path
      labelText="P50"
      inputClass="govuk-input--width-3"/>
  </@fdsFieldset.fieldset>
</#macro>
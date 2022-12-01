<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<#assign pageTitle = "General properties" />

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
  <@fdsForm.htmlForm>

    <@fdsTextInput.textInput
      path="form.datumDepth.decimalInputValue"
      labelText="Datum depth"
      suffix="ft tvdss"
      inputClass="govuk-input--width-5"
      nestingPath="form.datumDepth"/>

    <@fdsTextInput.textInput
      path="form.temperatureAtDatumDepth.decimalInputValue"
      labelText="Temperature at datum depth"
      inputClass="govuk-input--width-5"
      suffix="°F"
      nestingPath="form.temperatureAtDatumDepth"/>

    <@fdsTextInput.textInput
      path="form.pressureAtDatumDepth.decimalInputValue"
      labelText="Pressure at datum depth"
      suffix="psia"
      inputClass="govuk-input--width-5"
      nestingPath="form.pressureAtDatumDepth"/>

    <@fdsTextInput.textInput
      path="form.saturationPressureAtDatumDepth.decimalInputValue"
      labelText="Saturation pressure at datum depth"
      inputClass="govuk-input--width-5"
      suffix="psia"
      nestingPath="form.saturationPressureAtDatumDepth"/>

    <@fdsTextInput.textInput
      path="form.waterSalinity.decimalInputValue"
      labelText="Water salinity"
      suffix="ppm"
      inputClass="govuk-input--width-5"
      nestingPath="form.waterSalinity"/>

    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>
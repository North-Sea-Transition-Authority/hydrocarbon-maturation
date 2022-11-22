<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<#assign pageTitle = "Oil properties" />

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
  <@fdsForm.htmlForm>
    <@fdsTextInput.textInput
      path="form.oilGravity.decimalInputValue"
      labelText="Oil gravity (°API)"
      inputClass="govuk-input--width-5"
      suffix="°API"
      nestingPath="form.oilGravity"/>

    <@fdsTextInput.textInput
      path="form.oilViscosity.decimalInputValue"
      labelText="Oil viscosity (cP)"
      inputClass="govuk-input--width-5"
      suffix="cP"
      nestingPath="form.oilViscosity"/>

    <@fdsTextInput.textInput
      path="form.oilSulphur.decimalInputValue"
      labelText="Oil sulphur (ppm)"
      inputClass="govuk-input--width-5"
      suffix="ppm"
      nestingPath="form.oilSulphur"/>

    <@fdsTextInput.textInput
      path="form.oilWax.decimalInputValue"
      labelText="Oil wax (weight%)"
      inputClass="govuk-input--width-5"
      suffix="weight%"
      nestingPath="form.oilWax"/>

    <@fdsTextInput.textInput
      path="form.oilOtherWaxAppearanceTemperature.decimalInputValue"
      labelText="Oil other incl wax appearance temperature (°F)"
      inputClass="govuk-input--width-5"
      suffix="°F"
      nestingPath="form.oilOtherWaxAppearanceTemperature"
      optionalLabel=true/>

    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>
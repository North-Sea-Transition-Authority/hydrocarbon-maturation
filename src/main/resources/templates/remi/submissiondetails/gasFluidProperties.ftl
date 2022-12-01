<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<#assign pageTitle = "Gas properties" />

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
  <@fdsForm.htmlForm>
    <@fdsTextInput.textInput
      path="form.gasSpecificGravity.decimalInputValue"
      labelText="Gas specific gravity (air=1)"
      inputClass="govuk-input--width-5"
      nestingPath="form.gasSpecificGravity"/>
    <@fdsTextInput.textInput
      path="form.gasH2s.decimalInputValue"
      labelText="Gas H₂S"
      inputClass="govuk-input--width-5"
      suffix="ppm"
      nestingPath="form.gasH2s"/>
    <@fdsTextInput.textInput
      path="form.gasCo2.decimalInputValue"
      labelText="Gas CO₂"
      inputClass="govuk-input--width-5"
      suffix="mol%"
      nestingPath="form.gasCo2"/>
    <@fdsTextInput.textInput
      path="form.gasN2.decimalInputValue"
      labelText="Gas N₂"
      inputClass="govuk-input--width-5"
      suffix="mol%"
      nestingPath="form.gasN2"/>
    <@fdsRadio.radioGroup
      path="form.condensatePresent"
      labelText="Is condensate present?"
      hiddenContent=true>
      <@fdsRadio.radioYes path="form.condensatePresent">
        <@fdsTextInput.textInput
          path="form.condensateGravity.decimalInputValue"
          nestingPath="form.condensateGravity"
          labelText="Condensate gravity"
          inputClass="govuk-input--width-5"
          suffix="°API"/>
        <@fdsTextInput.textInput
          path="form.condensateViscosity.decimalInputValue"
          nestingPath="form.condensateViscosity"
          labelText="Condensate viscosity"
          inputClass="govuk-input--width-5"
          suffix="cP"/>
        <@fdsTextInput.textInput
          path="form.condensateSulphur.decimalInputValue"
          nestingPath="form.condensateSulphur"
          labelText="Condensate sulphur"
          inputClass="govuk-input--width-5"
          suffix="ppm"/>
        <@fdsTextInput.textInput
          path="form.condensateWax.decimalInputValue"
          nestingPath="form.condensateWax"
          labelText="Condensate wax"
          inputClass="govuk-input--width-5"
          suffix="weight%"/>
        <@fdsTextInput.textInput
          path="form.condensateOtherInclWax.decimalInputValue"
          nestingPath="form.condensateOtherInclWax"
          labelText="Condensate other incl wax appearance temperature "
          inputClass="govuk-input--width-5"
          suffix="°F"/>
      </@fdsRadio.radioYes>
      <@fdsRadio.radioNo path="form.condensatePresent"/>
    </@fdsRadio.radioGroup>
    <@fdsTextarea.textarea
      path="form.adviseOtherGasesConcentration"
      labelText="Advise of any other gases and their concentrations"
      optionalLabel=true/>
    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>
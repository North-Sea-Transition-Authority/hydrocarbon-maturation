<#include '../layout/layout.ftl'>

<#assign pageTitle = "Hydrocarbon case" />
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>

  <@fdsForm.htmlForm>
      <@fdsCheckbox.checkboxFieldset.fieldset legendHeading="What hydrocarbons do you expect to find?" legendHeadingClass="govuk-fieldset__legend--m">
        <@fdsCheckbox.checkboxGroup path="form.oilOnlyCaseType" hiddenContent=true>
            <@fdsCheckbox.checkboxItem path="form.oilOnlyCaseType" labelText="Oil only">
                <@fdsTextInput.textInput path="form.oilOnlyProbability.decimalInputValue" labelText="Probability" suffix="%" inputClass="govuk-input--width-3" nestingPath="form.oilOnlyCaseType"/>
                <@fdsTextInput.textInput path="form.oilOnlyComments" labelText="Comments" optionalLabel=true nestingPath="form.oilOnlyCaseType"/>
            </@fdsCheckbox.checkboxItem>
            <@fdsCheckbox.checkboxItem path="form.gasOnlyCaseType" labelText="Gas only">
                <@fdsTextInput.textInput path="form.gasOnlyProbability.decimalInputValue" labelText="Probability" suffix="%" inputClass="govuk-input--width-3" nestingPath="form.gasOnlyCaseType"/>
                <@fdsTextInput.textInput path="form.gasOnlyComments" labelText="Comments" optionalLabel=true nestingPath="form.gasOnlyCaseType"/>
            </@fdsCheckbox.checkboxItem>
            <@fdsCheckbox.checkboxItem path="form.oilAndGasCaseType" labelText="Oil and gas">
                <@fdsTextInput.textInput path="form.oilAndGasProbability.decimalInputValue" labelText="Probability" suffix="%" inputClass="govuk-input--width-3" nestingPath="form.oilAndGasCaseType"/>
                <@fdsTextInput.textInput path="form.oilAndGasComments" labelText="Comments" optionalLabel=true nestingPath="form.oilAndGasCaseType"/>
            </@fdsCheckbox.checkboxItem>
        </@fdsCheckbox.checkboxGroup>
      </@fdsCheckbox.checkboxFieldset.fieldset>
      <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>

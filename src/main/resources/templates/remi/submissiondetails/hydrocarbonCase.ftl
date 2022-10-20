<#include '../layout/layout.ftl'>
<#import '/spring.ftl' as springFtl>
<#import '../../fds/utilities/utilities.ftl' as fdsUtil>

<#assign pageTitle = "Hydrocarbon case" />
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=""
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
  <@fdsForm.htmlForm>
    <@fdsCheckbox.checkboxGroup
      path="form.oilOnlyCaseType"
      fieldsetHeadingText="What hydrocarbons do you expect to find?"
      fieldsetHeadingClass="govuk-fieldset__legend--l"
      fieldsetHeadingSize="h1"
      hintText="The total hydrocarbon case must add up to 100%"
      hiddenContent=true>
      <@fdsCheckbox.checkboxItem path="form.oilOnlyCaseType" labelText="Oil only">
        <@fdsTextInput.textInput path="form.oilOnlyProbability.decimalInputValue" labelText="Chance of occuring relative to the other cases" suffix="%" inputClass="govuk-input--width-3" nestingPath="form.oilOnlyCaseType"/>
        <@fdsTextarea.textarea path="form.oilOnlyComments" labelText="Comments" optionalLabel=true nestingPath="form.oilOnlyCaseType"/>
      </@fdsCheckbox.checkboxItem>
      <@fdsCheckbox.checkboxItem path="form.gasOnlyCaseType" labelText="Gas only">
        <@fdsTextInput.textInput path="form.gasOnlyProbability.decimalInputValue" labelText="Chance of occuring relative to the other cases" suffix="%" inputClass="govuk-input--width-3" nestingPath="form.gasOnlyCaseType"/>
        <@fdsTextarea.textarea path="form.gasOnlyComments" labelText="Comments" optionalLabel=true nestingPath="form.gasOnlyCaseType"/>
      </@fdsCheckbox.checkboxItem>
      <@fdsCheckbox.checkboxItem path="form.oilAndGasCaseType" labelText="Oil and gas">
        <@fdsTextInput.textInput path="form.oilAndGasProbability.decimalInputValue" labelText="Chance of occuring relative to the other cases" suffix="%" inputClass="govuk-input--width-3" nestingPath="form.oilAndGasCaseType"/>
        <@fdsTextarea.textarea path="form.oilAndGasComments" labelText="Comments" optionalLabel=true nestingPath="form.oilAndGasCaseType"/>
      </@fdsCheckbox.checkboxItem>
    </@fdsCheckbox.checkboxGroup>

    <@fdsDataItems.dataItem>
      <@fdsDataItems.dataValues key="0.00%"  value="Total probability"></@fdsDataItems.dataValues>
    </@fdsDataItems.dataItem>

    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>

</@defaultPage>

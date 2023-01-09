<#include '../../layout/layout.ftl'>

<#assign pageTitle = "Drill stem test result" />
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
  <@fdsForm.htmlForm>
    <@fdsTextInput.textInput
      path="form.formationName"
      labelText="Formation name"
      inputClass="govuk-input--width-20"/>
    <@fdsTextInput.textInput
      path="form.topDepth.decimalInputValue"
      labelText="Top depth"
      suffix="ft tvdss"
      inputClass="govuk-input--width-5"
      nestingPath="form.topDepth"/>
    <@fdsTextInput.textInput
      path="form.bottomDepth.decimalInputValue"
      labelText="Bottom depth"
      suffix="ft tvdss"
      inputClass="govuk-input--width-5"
      nestingPath="form.bottomDepth"/>
    <@fdsTextInput.textInput
      path="form.bhfp.decimalInputValue"
      labelText="Bottomhole flowing pressure"
      suffix="psia"
      inputClass="govuk-input--width-5"
      nestingPath="form.bhfp"/>
    <@fdsTextInput.textInput
      path="form.whfp.decimalInputValue"
      labelText="Wellhead flowing pressure"
      suffix="psia"
      inputClass="govuk-input--width-5"
      nestingPath="form.whfp"/>
    <@fdsTextInput.textInput
      path="form.flowDuration.decimalInputValue"
      labelText="Flow duration"
      suffix="hours"
      inputClass="govuk-input--width-5"
      nestingPath="form.flowDuration"/>
    <@fdsTextInput.textInput
      path="form.gasFlowrate.decimalInputValue"
      labelText="Gas flowrate"
      suffix="bpd"
      inputClass="govuk-input--width-5"
      nestingPath="form.gasFlowrate"/>
    <@fdsTextInput.textInput
      path="form.oilFlowrate.decimalInputValue"
      labelText="Oil flowrate"
      suffix="bpd"
      inputClass="govuk-input--width-5"
      nestingPath="form.oilFlowrate"/>
    <@fdsTextInput.textInput
      path="form.waterFlowrate.decimalInputValue"
      labelText="Water flowrate"
      suffix="bpd"
      inputClass="govuk-input--width-5"
      nestingPath="form.waterFlowrate"/>
    <@fdsTextarea.textarea
      path="form.commentsInterpretation"
      optionalLabel=true
      labelText="Comments and interpretation"
      hintText="For example, evidence of depletion, connected volumes, or KH"/>

    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>
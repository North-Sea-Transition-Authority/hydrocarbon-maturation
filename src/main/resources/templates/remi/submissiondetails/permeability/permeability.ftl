<#include '../../layout/layout.ftl'>
<#import '../../probabilitydistribution/probabilitydistribution.ftl' as probabilityDistribution>

<#-- @ftlvariable name="permeabilityTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="permeabilityError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="horizontalPermeabilityTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="horizontalPermeabilityError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->
<#-- @ftlvariable name="verticalPermeabilityTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTemplate" -->
<#-- @ftlvariable name="verticalPermeabilityError" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTypeError" -->

<#macro permeabilityValuesForm form methodUsed source isFieldSubmission>
  <@fdsTextInput.textInput suffix="md" inputClass="govuk-input--width-4" nestingPath="form.permeabilityValuesForm.permeabilityMin.decimalInputValue" path="form.permeabilityValuesForm.permeabilityMin.decimalInputValue" labelText="Permeability (md) min"/>
  <@fdsTextInput.textInput suffix="md" inputClass="govuk-input--width-4" nestingPath="form.permeabilityValuesForm.permeabilityMax.decimalInputValue" path="form.permeabilityValuesForm.permeabilityMax.decimalInputValue" labelText="Permeability (md) max"/>
  <@fdsTextInput.textInput suffix="md" inputClass="govuk-input--width-4" nestingPath="form.permeabilityValuesForm.permeabilityAverage.decimalInputValue" path="form.permeabilityValuesForm.permeabilityAverage.decimalInputValue" labelText="Permeability (md) average"/>
  <@fdsRadio.radioGroup path="form.permeabilityValuesForm.averageMethodUsed" nestingPath="form.permeabilityValuesForm.averageMethodUsed" labelText="Permeability (md) average method used">
    <#assign firstItem=true/>
    <#list methodUsed as key, value>
      <@fdsRadio.radioItem path="form.permeabilityValuesForm.averageMethodUsed" itemMap={key : value} isFirstItem=firstItem/>
      <#assign firstItem=false/>
    </#list>
  </@fdsRadio.radioGroup>
  <@fdsRadio.radioGroup path="form.permeabilityValuesForm.permeabilitySource" nestingPath="form.permeabilityValuesForm.permeabilitySource" labelText="Permeability (md) source" hiddenContent=true>
    <#assign firstItem=true/>
    <#list source as key, value>
      <@fdsRadio.radioItem path="form.permeabilityValuesForm.permeabilitySource" itemMap={key : value} isFirstItem=firstItem>
      <#if key == "OTHER">
        <@fdsTextarea.textarea path="form.permeabilityValuesForm.otherPermeabilitySource"
          labelText="Provide the other permeability (md) source" nestingPath="form.permeabilityValuesForm.permeabilitySource"/>
      </#if>
      </@fdsRadio.radioItem>
      <#assign firstItem=false/>
    </#list>
  </@fdsRadio.radioGroup>
  <@fdsTextarea.textarea
    path="form.permeabilityValuesForm.permeabilityComments"
    nestingPath="form.permeabilityValuesForm.permeabilityComments"
    labelText="Permeability (md) comments"
    optionalLabel=true/>
  <@fdsTextInput.textInput suffix="kv/kh" inputClass="govuk-input--width-4" path="form.permeabilityValuesForm.kvKhAverage.decimalInputValue" nestingPath="form.permeabilityValuesForm.kvKhAverage.decimalInputValue" labelText="Kv/kh average"/>

  <#if isFieldSubmission>
    <@probabilityDistribution.probabilityDistributionSingularTemplate
      probabilityDistributionPath="form.permeabilityValuesForm.permeability"
      labelText="Permeability"
      template=permeabilityTemplate
      error=permeabilityError/>

    <@probabilityDistribution.probabilityDistributionSingularTemplate
      probabilityDistributionPath="form.permeabilityValuesForm.horizontalPermeability"
      labelText="Horizontal permeability"
      template=horizontalPermeabilityTemplate
      error=horizontalPermeabilityError/>

    <@probabilityDistribution.probabilityDistributionSingularTemplate
      probabilityDistributionPath="form.permeabilityValuesForm.verticalPermeability"
      labelText="Vertical permeability"
      template=verticalPermeabilityTemplate
      error=verticalPermeabilityError/>
  </#if>

</#macro>
<#include '../../layout/layout.ftl'>

<#macro permeabilityValuesForm form methodUsed source>
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
  <@fdsTextInput.textInput suffix="kv/kh" inputClass="govuk-input--width-4" path="form.permeabilityValuesForm.kvKhMin.decimalInputValue" nestingPath="form.permeabilityValuesForm.kvKhMin.decimalInputValue" labelText="Kv/kh min"/>
  <@fdsTextInput.textInput suffix="kv/kh" inputClass="govuk-input--width-4" path="form.permeabilityValuesForm.kvKhMax.decimalInputValue" nestingPath="form.permeabilityValuesForm.kvKhMax.decimalInputValue" labelText="Kv/kh max"/>
  <@fdsTextInput.textInput suffix="kv/kh" inputClass="govuk-input--width-4" path="form.permeabilityValuesForm.kvKhAverage.decimalInputValue" nestingPath="form.permeabilityValuesForm.kvKhAverage.decimalInputValue" labelText="Kv/kh average"/>
  <@fdsRadio.radioGroup path="form.permeabilityValuesForm.kvKhAverageMethodUsed" hiddenContent=true nestingPath="form.permeabilityValuesForm.kvKhAverageMethodUsed" labelText="Kv/kh average method used">
    <#assign firstItem=true/>
    <#list methodUsed as key, value>
      <@fdsRadio.radioItem path="form.permeabilityValuesForm.kvKhAverageMethodUsed" itemMap={key : value} isFirstItem=firstItem>
        <#if key == "OTHER">
          <@fdsTextarea.textarea path="form.permeabilityValuesForm.kvKhAverageMethodUsedOther"
            labelText="Provide the other kv/kh average method" nestingPath="form.permeabilityValuesForm.kvKhAverageMethodUsed"/>
        </#if>
      </@fdsRadio.radioItem>
      <#assign firstItem=false/>
    </#list>
  </@fdsRadio.radioGroup>
  <@fdsRadio.radioGroup path="form.permeabilityValuesForm.kvKhSource" nestingPath="form.permeabilityValuesForm.kvKhSource" labelText="Kv/kh source" hiddenContent=true>
    <#assign firstItem=true/>
    <#list source as key, value>
      <@fdsRadio.radioItem path="form.permeabilityValuesForm.kvKhSource" itemMap={key : value} isFirstItem=firstItem>
        <#if key == "OTHER">
          <@fdsTextarea.textarea path="form.permeabilityValuesForm.otherKvKhSource"
            labelText="Provide the other kv/kh source" nestingPath="form.permeabilityValuesForm.kvKhSource"/>
        </#if>
      </@fdsRadio.radioItem>
      <#assign firstItem=false/>
    </#list>
  </@fdsRadio.radioGroup>
  <@fdsTextarea.textarea
    path="form.permeabilityValuesForm.kvKhComments"
    nestingPath="form.permeabilityValuesForm.kvKhComments"
    labelText="Kv/kh comments"
    optionalLabel=true/>
</#macro>
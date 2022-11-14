<#include '../layout/layout.ftl'>

<#macro permeabilityForm form methodUsed source>
  <@fdsTextInput.textInput nestingPath="form.permeabilityForm.permeabilityMin.decimalInputValue" path="form.permeabilityForm.permeabilityMin.decimalInputValue" labelText="Permeability (md) min"/>
  <@fdsTextInput.textInput nestingPath="form.permeabilityForm.permeabilityMax.decimalInputValue" path="form.permeabilityForm.permeabilityMax.decimalInputValue" labelText="Permeability (md) max"/>
  <@fdsTextInput.textInput nestingPath="form.permeabilityForm.permeabilityAverage.decimalInputValue" path="form.permeabilityForm.permeabilityAverage.decimalInputValue" labelText="Permeability (md) average"/>
  <@fdsRadio.radioGroup path="form.permeabilityForm.averageMethodUsed" nestingPath="form.permeabilityForm.averageMethodUsed" labelText="Permeability (md) average method used">
    <#assign firstItem=true/>
    <#list methodUsed as key, value>
      <@fdsRadio.radioItem path="form.permeabilityForm.averageMethodUsed" itemMap={key : value} isFirstItem=firstItem/>
      <#assign firstItem=false/>
    </#list>
  </@fdsRadio.radioGroup>
  <@fdsRadio.radioGroup path="form.permeabilityForm.permeabilitySource" nestingPath="form.permeabilityForm.permeabilitySource" labelText="Permeability (md) source" hiddenContent=true>
    <#assign firstItem=true/>
    <#list source as key, value>
      <@fdsRadio.radioItem path="form.permeabilityForm.permeabilitySource" itemMap={key : value} isFirstItem=firstItem>
      <#if key == "OTHER">
        <@fdsTextarea.textarea path="form.permeabilityForm.otherPermeabilitySource"
          labelText="Provide the other permeability (md) source" nestingPath="form.permeabilityForm.permeabilitySource"/>
      </#if>
      </@fdsRadio.radioItem>
      <#assign firstItem=false/>
    </#list>
  </@fdsRadio.radioGroup>
  <@fdsTextarea.textarea
    path="form.permeabilityForm.permeabilityComments"
    nestingPath="form.permeabilityForm.permeabilityComments"
    labelText="Permeability (md) comments"
    optionalLabel=true/>
  <@fdsTextInput.textInput path="form.permeabilityForm.kvKhMin.decimalInputValue" nestingPath="form.permeabilityForm.kvKhMin.decimalInputValue" labelText="Kv/kh min"/>
  <@fdsTextInput.textInput path="form.permeabilityForm.kvKhMax.decimalInputValue" nestingPath="form.permeabilityForm.kvKhMax.decimalInputValue" labelText="Kv/kh max"/>
  <@fdsTextInput.textInput path="form.permeabilityForm.kvKhAverage.decimalInputValue" nestingPath="form.permeabilityForm.kvKhAverage.decimalInputValue" labelText="Kv/kh average"/>
  <@fdsRadio.radioGroup path="form.permeabilityForm.kvKhAverageMethodUsed" hiddenContent=true nestingPath="form.permeabilityForm.kvKhAverageMethodUsed" labelText="Kv/kh average method used">
    <#assign firstItem=true/>
    <#list source as key, value>
      <@fdsRadio.radioItem path="form.permeabilityForm.kvKhAverageMethodUsed" itemMap={key : value} isFirstItem=firstItem>
        <#if key == "OTHER">
          <@fdsTextarea.textarea path="form.permeabilityForm.kvKhAverageMethodUsedOther"
            labelText="Provide the other kv/kh average method" nestingPath="form.permeabilityForm.kvKhAverageMethodUsed"/>
        </#if>
      </@fdsRadio.radioItem>
      <#assign firstItem=false/>
    </#list>
  </@fdsRadio.radioGroup>
  <@fdsRadio.radioGroup path="form.permeabilityForm.kvKhSource" nestingPath="form.permeabilityForm.kvKhSource" labelText="Kv/kh source" hiddenContent=true>
    <#assign firstItem=true/>
    <#list source as key, value>
      <@fdsRadio.radioItem path="form.permeabilityForm.kvKhSource" itemMap={key : value} isFirstItem=firstItem>
        <#if key == "OTHER">
          <@fdsTextarea.textarea path="form.permeabilityForm.otherKvKhSource"
            labelText="Provide the other kv/kh source" nestingPath="form.permeabilityForm.kvKhSource"/>
        </#if>
      </@fdsRadio.radioItem>
      <#assign firstItem=false/>
    </#list>
  </@fdsRadio.radioGroup>
  <@fdsTextarea.textarea
    path="form.permeabilityForm.kvKhComments"
    nestingPath="form.permeabilityForm.kvKhComments"
    labelText="Kv/kh comments"
    optionalLabel=true/>
</#macro>
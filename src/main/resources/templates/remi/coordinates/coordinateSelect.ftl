<#import '/spring.ftl' as spring>
<#import '../../fds/utilities/utilities.ftl' as fdsUtil>

<#--Custom select component, removes the error messages bound to select.-->
<#macro select
  path
  options
  labelText
  hintText=""
  optionalInputDefault=""
  nestingPath=""
  optionalLabel=false
  pageHeading=false
  labelHeadingClass="govuk-label--l"
  formGroupClass=""
  labelClass=""
  inputClass=""
  autocompleteValue="">

    <@spring.bind path/>

    <#local id=fdsUtil.sanitiseId(spring.status.expression)>
    <#local name=fdsUtil.getSpringStatusExpression()>
    <#local value=fdsUtil.getSpringStatusValue()>
    <#local hasError=fdsUtil.hasSpringStatusErrors()>
    <#local ariaDescribedByValue=fdsUtil.getAriaDescribedByAttr(hintText?has_content, hasError, id)>

  <div class="govuk-form-group ${formGroupClass}">
      <#if pageHeading>
    <h1 class="govuk-label-wrapper">
        </#if>
      <label class="govuk-label ${labelClass}<#if pageHeading> ${labelHeadingClass}</#if>" for="${id}">
          ${labelText} <#if optionalLabel>(optional)</#if>
      </label>
        <#if pageHeading>
    </h1>
      </#if>
      <#if hintText?has_content>
        <div id="${id}-hint" class="govuk-hint">
            ${hintText}
        </div>
      </#if>
    <select
      class="govuk-select ${inputClass}<#if hasError> govuk-select--error</#if>"
      id="${id}"
      name="${name}"
      <#if ariaDescribedByValue?has_content>aria-describedby="${ariaDescribedByValue}"</#if>
      <#if autocompleteValue?has_content>autocomplete="${autocompleteValue}"</#if>>
        <#if optionalInputDefault?has_content>
          <option value="" selected>${optionalInputDefault}</option>
        <#else>
          <option value="" selected disabled>Select one...</option>
        </#if>
        <#list options?keys as option>
            <#assign isSelected = value == option>
          <option value="${option}" <#if isSelected>selected</#if>>${options[option]}</option>
        </#list>
    </select>
  </div>

<#--Rebind your form when a component is used inside show/hide radio groups-->
    <#if nestingPath?has_content>
        <@spring.bind nestingPath/>
    </#if>
</#macro>

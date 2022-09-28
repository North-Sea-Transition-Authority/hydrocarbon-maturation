<#include '../layout/layout.ftl'>
<#import '/spring.ftl' as springFtl>
<#import '../../fds/utilities/utilities.ftl' as fdsUtil>

<#macro probabilityDistributionFieldSet
    formId
    labelText
    nestingPath
    error
    probabilityDistributionType
    hintText=""
    optionalLabel=false
    fieldsetHeadingSize="h3"
    fieldsetHeadingClass="govuk-fieldset__legend--s"
    formGroupClass=""
    caption=""
    captionClass="govuk-caption-s"
    showLabelOnly=false
    noFieldsetHeadingSize="--s"
    moreNestedContent="">

    <#if error.hasError() && error.probabilityDistributionType().name()==probabilityDistributionType.name()>
        <@springFtl.bind error.fullErrorPath()/>
        <#local hasError=springFtl.status.errorMessages[0]?has_content/>
        <@springFtl.bind nestingPath/>
    <#else>
        <#local hasError=false/>
    </#if>

  <div class="govuk-form-group ${formGroupClass} <#if hasError>govuk-form-group--error</#if>">
      <@fdsFieldset.fieldset
      legendHeading=labelText
      legendHeadingSize=fieldsetHeadingSize
      legendHeadingClass=fieldsetHeadingClass
      caption=caption
      captionClass=captionClass
      optionalLabel=optionalLabel
      hintText=hintText
      formErrorId=formId
      formHasError=hasError
      showHeadingOnly=showLabelOnly
      noFieldsetHeadingSize=noFieldsetHeadingSize>

          ${moreNestedContent}

        <div class="govuk-date-input" id="${formId}-number-input">
            <#--probabilityDistributionInputList macro goes here-->
            <#nested>
        </div>
      </@fdsFieldset.fieldset>
  </div>
<#--Rebind your form when a component is used inside show/hide radio groups-->
    <#if nestingPath?has_content>
        <@springFtl.bind nestingPath/>
    </#if>
</#macro>

<#macro probabilityDistributionInputList
    probabilityDistributionInputs
    pathPrefix>

    <#list probabilityDistributionInputs as input>
          <@probabilityDistributionInputItem
                path=pathPrefix+"."+input.probabilityOfExceedanceType.path+".decimalInputValue"
                labelText=input.probabilityOfExceedanceType.displayName
                required=input.required
          />
    </#list>
</#macro>

<#macro probabilityDistributionInputItem
    path
    labelText
    required
    inputClass="govuk-input--width-3">

    <@springFtl.bind path/>

    <#local id=fdsUtil.sanitiseId(springFtl.status.expression)>
    <#local name=fdsUtil.getSpringStatusExpression()>
    <#local value=fdsUtil.getSpringStatusValue()>
    <#local hasError=fdsUtil.hasSpringStatusErrors()>

    <div class="govuk-date-input__item">
        <div class="govuk-form-group">
            <label class="govuk-label govuk-date-input__label" for="${id}">
                ${labelText}
                <#if !required>
                    <br/>
                    (optional)
                </#if>
            </label>
            <input class="govuk-input <#if hasError>govuk-input--error</#if> govuk-date-input__input ${inputClass}" id="${id}" name="${name}" type="text" value="${value}">
        </div>
    </div>
</#macro>



<#macro probabilityDistributionTemplate
    probabilityDistributionPath
    labelText
    formId
    template
    error>

    <@springFtl.formHiddenInput path=probabilityDistributionPath+".probabilityDistributionFieldInstance"/>
    <@fdsRadio.radioGroup path=probabilityDistributionPath+".probabilityDistributionType" labelText=labelText hiddenContent=true>
        <#assign firstItem=true/>
        <#list template.probabilityDistributionTypeWithInputsList as inputs>
            <@fdsRadio.radioItem
                path=probabilityDistributionPath+".probabilityDistributionType"
                itemMap={inputs.probabilityDistributionType.name() : inputs.probabilityDistributionType.displayName}
                isFirstItem=firstItem>
                <@probabilityDistributionSingle
                probabilityDistributionTypePath=probabilityDistributionPath+".probabilityDistributionType"
                probabilityDistributionFieldInstancePath=probabilityDistributionPath+".probabilityDistributionFieldInstance"
                    formId=formId
                    template=template
                    error=error
                    inputs=inputs/>
            </@fdsRadio.radioItem>
        </#list>
    </@fdsRadio.radioGroup>

</#macro>



<#macro probabilityDistributionSingle
    probabilityDistributionTypePath
    probabilityDistributionFieldInstancePath
    formId
    template
    error
    inputs>

    <@probabilityDistributionFieldSet
        formId=formId
        labelText="Probability of exceedance (${template.units})"
        nestingPath=probabilityDistributionTypePath
        error=error
        probabilityDistributionType=inputs.probabilityDistributionType>

        <#if error.hasError() && error.probabilityDistributionType().name()==inputs.probabilityDistributionType.name()>
            <@springFtl.bind error.fullErrorPath()/>
            <@fdsError.inputError inputId=error.errorId()/>
            <@springFtl.bind probabilityDistributionTypePath/>
        </#if>

        <@probabilityDistributionInputList
            probabilityDistributionInputs=inputs.probabilityDistributionTypeInputs
            pathPrefix=probabilityDistributionTypePath?replace("probabilityDistributionType", "")+inputs.probabilityDistributionType.path/>
        <#assign firstItem=false/>
    </@probabilityDistributionFieldSet>

</#macro>



<#macro probabilityDistributionCustomTemplate
    probabilityDistributionPath
    labelText
    formId
    template
    error>

    <@springFtl.formHiddenInput path=probabilityDistributionPath+".probabilityDistributionType"/>
    <@springFtl.formHiddenInput path=probabilityDistributionPath+".probabilityDistributionFieldInstance"/>
    <@probabilityDistributionSingle
        probabilityDistributionTypePath=probabilityDistributionPath+".probabilityDistributionType"
        probabilityDistributionFieldInstancePath=probabilityDistributionPath+".probabilityDistributionFieldInstance"
        formId=formId
        template=template
        error=error
        inputs=template.probabilityDistributionTypeWithInputsList[0]/>

</#macro>
<#include '../layout/layout.ftl'>
<#import '/spring.ftl' as springFtl>
<#import '../../fds/utilities/utilities.ftl' as fdsUtil>

<#macro probabilityDistributionFieldSet
    formId
    labelText
    pathMin=""
    pathP99=""
    pathP90=""
    pathMode=""
    pathP50=""
    pathMean=""
    pathP10=""
    pathP1=""
    pathMax=""
    pathConstant=""
    hintText=""
    optionalLabel=false
    nestingPath=""
    fieldsetHeadingSize="h2"
    fieldsetHeadingClass="govuk-fieldset__legend--s"
    formGroupClass=""
    caption=""
    captionClass="govuk-caption-s"
    showLabelOnly=false
    noFieldsetHeadingSize="--s"
    moreNestedContent="">

    <#if pathMin?has_content>
        <@springFtl.bind pathMin/>
        <#assign hasMinError=fdsUtil.hasSpringStatusErrors()>
    <#else>
        <#assign hasMinError=false>
    </#if>
    <#if pathP99?has_content>
        <@springFtl.bind pathMin/>
        <#assign hasP99Error=fdsUtil.hasSpringStatusErrors()>
    <#else>
        <#assign hasP99Error=false>
    </#if>
    <#if pathP90?has_content>
        <@springFtl.bind pathMin/>
        <#assign hasP90Error=fdsUtil.hasSpringStatusErrors()>
    <#else>
        <#assign hasP90Error=false>
    </#if>
    <#if pathMode?has_content>
        <@springFtl.bind pathMin/>
        <#assign hasModeError=fdsUtil.hasSpringStatusErrors()>
    <#else>
        <#assign hasModeError=false>
    </#if>
    <#if pathP50?has_content>
        <@springFtl.bind pathMin/>
        <#assign hasP50Error=fdsUtil.hasSpringStatusErrors()>
    <#else>
        <#assign hasP50Error=false>
    </#if>
    <#if pathMean?has_content>
        <@springFtl.bind pathMin/>
        <#assign hasMeanError=fdsUtil.hasSpringStatusErrors()>
    <#else>
        <#assign hasMeanError=false>
    </#if>
    <#if pathP10?has_content>
        <@springFtl.bind pathMin/>
        <#assign hasP10Error=fdsUtil.hasSpringStatusErrors()>
    <#else>
        <#assign hasP10Error=false>
    </#if>
    <#if pathP1?has_content>
        <@springFtl.bind pathMin/>
        <#assign hasP1Error=fdsUtil.hasSpringStatusErrors()>
    <#else>
        <#assign hasP1Error=false>
    </#if>
    <#if pathConstant?has_content>
        <@springFtl.bind pathMin/>
        <#assign hasConstantError=fdsUtil.hasSpringStatusErrors()>
    <#else>
        <#assign hasConstantError=false>
    </#if>
    <#local hasError =
        hasMinError ||
        hasP99Error ||
        hasP90Error ||
        hasModeError ||
        hasP50Error ||
        hasMeanError ||
        hasP10Error ||
        hasP1Error ||
        hasConstantError>

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
          <#if hasMinError>
              <@springFtl.bind pathMin/>
              <@fdsError.inputError inputId="${formId}"/>
          </#if>
          <#if hasP99Error>
              <@springFtl.bind pathP99/>
              <@fdsError.inputError inputId="${formId}"/>
          </#if>
          <#if hasP90Error>
              <@springFtl.bind pathP90/>
              <@fdsError.inputError inputId="${formId}"/>
          </#if>
          <#if hasModeError>
              <@springFtl.bind pathMode/>
              <@fdsError.inputError inputId="${formId}"/>
          </#if>
          <#if hasP50Error>
              <@springFtl.bind pathP50/>
              <@fdsError.inputError inputId="${formId}"/>
          </#if>
          <#if hasMeanError>
              <@springFtl.bind pathMean/>
              <@fdsError.inputError inputId="${formId}"/>
          </#if>
          <#if hasP10Error>
              <@springFtl.bind pathP10/>
              <@fdsError.inputError inputId="${formId}"/>
          </#if>
          <#if hasP1Error>
              <@springFtl.bind pathP1/>
              <@fdsError.inputError inputId="${formId}"/>
          </#if>
          <#if hasConstantError>
              <@springFtl.bind pathConstant/>
              <@fdsError.inputError inputId="${formId}"/>
          </#if>

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
                path=pathPrefix+"."+input.probabilityOfExceedanceType.path
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
    path
    labelText
    formId
    template>

    <@fdsRadio.radioGroup path=path labelText=labelText hiddenContent=true>
        <#assign firstItem=true/>
        <#list template.probabilityDistributionTypeWithInputsList as inputs>
            <@fdsRadio.radioItem
                path=path
                itemMap={inputs.probabilityDistributionType.name() : inputs.probabilityDistributionType.displayName}
                isFirstItem=firstItem>
                <@probabilityDistributionFieldSet formId=formId labelText="Probability of exceedance (${template.units})" nestingPath=path>
                    <@probabilityDistributionInputList
                        probabilityDistributionInputs=inputs.probabilityDistributionTypeInputs
                        pathPrefix=path?replace("probabilityDistributionType", "")+inputs.probabilityDistributionType.path/>
                    <#assign firstItem=false/>
                </@probabilityDistributionFieldSet>
            </@fdsRadio.radioItem>
        </#list>
    </@fdsRadio.radioGroup>

</#macro>
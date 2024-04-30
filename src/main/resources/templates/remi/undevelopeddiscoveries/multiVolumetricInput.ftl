<#include '../layout/layout.ftl'>
<#import '/spring.ftl' as springFtl>
<#import '../../fds/utilities/utilities.ftl' as fdsUtil>
<#import '../../fds/components/error/error.ftl' as error>

<#macro multiVolumetricInput
oneCPath
twoCPath
threeCPath
labelText
formId
fieldsetHeadingSize="h2"
fieldsetHeadingClass="govuk-fieldset__legend--s"
inputClass="govuk-input--width-3"
noFieldsetHeadingSize="--s">

    <@springFtl.bind oneCPath/>
    <#local oneCId=fdsUtil.sanitiseId(springFtl.status.expression)>
    <#local oneCName=fdsUtil.getSpringStatusExpression()>
    <#local oneCValue=fdsUtil.getSpringStatusValue()>
    <#local hasOneCError=fdsUtil.hasSpringStatusErrors()>

    <@springFtl.bind twoCPath/>
    <#local twoCId=fdsUtil.sanitiseId(springFtl.status.expression)>
    <#local twoCName=fdsUtil.getSpringStatusExpression()>
    <#local twoCValue=fdsUtil.getSpringStatusValue()>
    <#local hasTwoCError=fdsUtil.hasSpringStatusErrors()>

    <@springFtl.bind threeCPath/>
    <#local threeCId=fdsUtil.sanitiseId(springFtl.status.expression)>
    <#local threeCName=fdsUtil.getSpringStatusExpression()>
    <#local threeCValue=fdsUtil.getSpringStatusValue()>
    <#local hasThreeCError=fdsUtil.hasSpringStatusErrors()>

<#--Assign variable, left hand red border styling-->
    <#local hasError = hasOneCError || hasTwoCError || hasThreeCError>

    <@fdsFieldset.fieldset
    legendHeading=labelText
    legendHeadingSize=fieldsetHeadingSize
    legendHeadingClass=fieldsetHeadingClass
    optionalLabel=true
    formErrorId=formId
    formHasError=hasError
    noFieldsetHeadingSize=noFieldsetHeadingSize>

    <#--Re-bind for single error message-->
        <#if hasOneCError>
            <@springFtl.bind oneCPath/>
            <@error.inputError inputId="${formId}"/>
        <#elseif hasTwoCError>
            <@springFtl.bind twoCPath/>
            <@error.inputError inputId="${formId}"/>
        <#elseif hasThreeCError>
            <@springFtl.bind threeCPath/>
            <@error.inputError inputId="${formId}"/>
        </#if>

      <div class="govuk-date-input" id="${formId}-date-input">
        <div class="govuk-date-input__item">
          <div class="govuk-form-group">
            <label class="govuk-label govuk-date-input__label" for="${oneCId}">1C</label>
            <input class="govuk-input <#if hasOneCError>govuk-input--error</#if> govuk-date-input__input ${inputClass}"
                   id="${oneCId}" name="${oneCName}" type="text" value="${oneCValue}">
          </div>
        </div>

        <div class="govuk-date-input__item">
          <div class="govuk-form-group">
            <label class="govuk-label govuk-date-input__label" for="${twoCId}">2C</label>
            <input class="govuk-input <#if hasTwoCError>govuk-input--error</#if> govuk-date-input__input ${inputClass}"
                   id="${twoCId}" name="${twoCName}" type="text" value="${twoCValue}">
          </div>
        </div>

        <div class="govuk-date-input__item">
          <div class="govuk-form-group">
            <label class="govuk-label govuk-date-input__label" for="${threeCId}">3C</label>
            <input class="govuk-input <#if hasThreeCError>govuk-input--error</#if> govuk-date-input__input ${inputClass}"
                   id="${threeCId}" name="${threeCName}" type="text" value="${threeCValue}">
          </div>
        </div>
      </div>
    </@fdsFieldset.fieldset>
</#macro>
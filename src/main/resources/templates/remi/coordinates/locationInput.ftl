<#import '/spring.ftl' as spring>
<#include '../layout/layout.ftl'>
<#import '../../fds/components/numberInput/numberInput.ftl' as numberInput>
<#import '../../fds/utilities/utilities.ftl' as fdsUtil>
<#import 'coordinateSelect.ftl' as hmSelect>

<#macro locationInput
  degreesLocationPath
  minutesLocationPath
  secondsLocationPath
  formId
  directionList=[]
  direction="NS"
  directionPath=""
  labelText=""
  hintText=""
  optionalLabel="fromInterceptor"
  nestingPath=""
  wgs84Datum=true
  fieldsetHeadingSize="h3"
  fieldsetHeadingClass="govuk-fieldset__legend--s"
  formGroupClass=""
  caption=""
  captionClass="govuk-caption-s">

  <#if optionalLabel=="true">
    <#local optionalFlag=true>
  <#elseif optionalLabel=="false">
    <#local optionalFlag=false>
  <#elseif optionalLabel=="fromInterceptor">
    <#local optionalFlag=(!(validation[spring.status.path].mandatory)!true)>
  </#if>

  <@spring.bind degreesLocationPath/>
  <#local hasErrorDegrees=fdsUtil.hasSpringStatusErrors() && spring.status.errorMessages[0]?has_content>
  <#assign degreesError>
    <@fdsError.inputError inputId="${formId}-degrees"/>
  </#assign>

  <@spring.bind minutesLocationPath/>
  <#local hasErrorMinutes=fdsUtil.hasSpringStatusErrors() && spring.status.errorMessages[0]?has_content>
  <#assign minutesError>
    <@fdsError.inputError inputId="${formId}-minutes"/>
  </#assign>

  <@spring.bind secondsLocationPath/>
  <#local hasErrorSeconds=fdsUtil.hasSpringStatusErrors() && spring.status.errorMessages[0]?has_content>
  <#assign secondsError>
    <@fdsError.inputError inputId="${formId}-seconds"/>
  </#assign>

  <#if directionPath?has_content>
    <@spring.bind directionPath/>
    <#local hasErrorHemisphere=fdsUtil.hasSpringStatusErrors()>
    <#assign hemisphereError>
      <@fdsError.inputError inputId="${formId}-hemisphere"/>
    </#assign>
  <#else>
    <#local hasErrorHemisphere=false>
  </#if>

  <#local hasError=hasErrorDegrees || hasErrorMinutes || hasErrorSeconds || hasErrorHemisphere>
  <#local hasErrorNoHemisphere=hasErrorDegrees || hasErrorMinutes || hasErrorSeconds >

  <#if wgs84Datum>
    <#assign finalLabelText>
      ${labelText} in WGS 84
    </#assign>
  <#else>
    <#assign finalLabelText>
      ${labelText}
    </#assign>
  </#if>

  <div class="govuk-form-group ${formGroupClass}<#if hasError>govuk-form-group--error</#if>">
    <@fdsFieldset.fieldset legendHeading="${finalLabelText}" legendHeadingSize=fieldsetHeadingSize legendHeadingClass=fieldsetHeadingClass caption=caption captionClass=captionClass optionalLabel=optionalFlag hintText=hintText>
      <#if hasError>
        <#if hasErrorDegrees>
            ${degreesError}
        <#elseif hasErrorMinutes>
            ${minutesError}
        <#elseif hasErrorSeconds>
            ${secondsError}
        <#elseif hasErrorHemisphere>
            ${hemisphereError}
        </#if>
      </#if>
      <div class="govuk-date-input">
        <@numberInput.numberInputItem path=degreesLocationPath labelText="Degrees" inputClass="govuk-input--width-3"/>
        <@numberInput.numberInputItem path=minutesLocationPath labelText="Minutes" inputClass="govuk-input--width-3"/>
        <@numberInput.numberInputItem path=secondsLocationPath labelText="Seconds" inputClass="govuk-input--width-3"/>
        <div class="govuk-date-input__item">
          <div class="govuk-form-group">
            <#if direction=="NS">
              <div class="govuk-date-input__item">
                <div class="govuk-form-group">
                  <label class="govuk-label govuk-date-input__label" for="${formId}-hemisphere-north">
                    Hemisphere (north / south)
                  </label>
                  <input class="govuk-input <#if hasError>govuk-input--error</#if> govuk-date-input__input govuk-input--width-3 govuk-input--read-only"
                         id="${formId}-hemisphere-north" name="hemisphere-north" type="text" disabled value="North">
                </div>
              </div>
            <#elseif direction=="NS_MANUAL">
              <@hmSelect.select path=directionPath options=directionList labelText="Hemisphere (north / south)"/>
            <#else>
              <@hmSelect.select path=directionPath options=directionList labelText="Hemisphere (east / west)"/>
            </#if>
          </div>
        </div>
      </div>
    </@fdsFieldset.fieldset>
  </div>
<#--Rebind your form when a component is used inside show/hide radio groups-->
  <#if nestingPath?has_content>
    <@spring.bind nestingPath/>
  </#if>
</#macro>
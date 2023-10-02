<#include '../../layout/layout.ftl'>
<#import 'drillDetails.ftl' as drillDetails/>

<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="longitudeHemisphere" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="datumTypes" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="dryHoleTypes" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<#assign pageTitle = "Discovery well details" />

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
  <@fdsForm.htmlForm>
    <@drillDetails.drillDetails
      datumTypes=datumTypes
      longitudeHemisphere=longitudeHemisphere
      well=well!{}
      wellEndpoint=wellEndpoint
      formPath="form.wellDetailsForm"/>
    <h2 class="govuk-heading-l">Discovery details</h2>
    <@fdsRadio.radioGroup path="form.discoveryStatus" labelText="Discovery status">
      <#assign firstItem=true/>
      <#list discoveryStatus as key, value>
        <@fdsRadio.radioItem path="form.discoveryStatus" itemMap={key : value} isFirstItem=firstItem/>
        <#assign firstItem=false/>
      </#list>
    </@fdsRadio.radioGroup>
    <@fdsDetails.summaryDetails summaryTitle="What is the meaning of each discovery status?">
      <p class="govuk-body">
        A "technical discovery" is defined as exceeding the minimal recoverable volume used to define the Geological Chance of Success,
        but is not currently viewed as warranting further appraisal/development activity.
      </p>
      <p class="govuk-body">
        A "potentially commercial discovery" is defined as warranting further appraisal/development activity.
      </p>
    </@fdsDetails.summaryDetails>
    <@fdsTextarea.textarea
      path="form.chanceOfCommercialityComments"
      labelText="Comments regarding chance of commerciality"
      optionalLabel=true/>
    <@fdsTextInput.textInput path="form.dryHoleCost.decimalInputValue"
      labelText="Total well cost"
      inputClass="govuk-input--width-5"
      prefix="£"
      nestingPath="form.dryHoleCost"
      suffix="million"/>
    <@fdsTextInput.textInput path="form.successRelatedCosts.decimalInputValue"
      labelText="Success-related costs"
      inputClass="govuk-input--width-5"
      prefix="£"
      nestingPath="form.successRelatedCosts"
      suffix="million"/>
    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>
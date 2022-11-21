<#include '../../layout/layout.ftl'>
<#import 'drillDetails.ftl' as drillDetails/>

<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="longitudeHemisphere" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="datumTypes" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="dryHoleTypes" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<#assign pageTitle = "Dry well details" />

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
      dryWellEndpoint=dryWellEndpoint
      dryWell=dryWell!{}
      formPath="form.drillDetailsForm"/>

    <h2 class="govuk-heading-l">Dry hole details</h2>
    <@fdsRadio.radioGroup path="form.dryHoleType" labelText="Dry hole shows type">
      <#assign firstItem=true/>
      <#list dryHoleTypes as key, value>
        <@fdsRadio.radioItem path="form.dryHoleType" itemMap={key : value} isFirstItem=firstItem/>
        <#assign firstItem=false/>
      </#list>
    </@fdsRadio.radioGroup>
    <@fdsTextarea.textarea
      path="form.dryHoleDescription"
      labelText="Dry hole shows description"
      hintText="Indicate if shows were encountered in the well and if so, over what stratigraphic intervals"
      optionalLabel=true
    />
    <@fdsTextarea.textarea
      path="form.dryHoleScope"
      labelText="Dry hole scope for remaining potential"
      hintText="Based on the location of the well, is there still considered to be a valid prospect on the mapped structure. For example, up-dip potential"
      optionalLabel=true
    />
    <@fdsTextarea.textarea
      path="form.dryHoleComments"
      labelText="Dry hole comments"
      optionalLabel=true
    />
    <h2 class="govuk-heading-l">Well cost</h2>
    <@fdsTextInput.textInput path="form.dryHoleCost.decimalInputValue"
      labelText="Total well cost"
      inputClass="govuk-input--width-5"
      prefix="£"
      nestingPath="form.dryHoleCost"
      suffix="million"/>

    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>
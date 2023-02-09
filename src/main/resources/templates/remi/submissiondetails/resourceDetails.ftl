<#include '../layout/layout.ftl'>

<#assign pageTitle = "Resource details" />

<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  breadcrumbsMap=breadcrumbsMap
  errorItems=errorList>
    <#if isRegulator>
      <#assign isOptional = true>
    <#else>
      <#assign isOptional = false>
    </#if>
    <#if relinquishmentSubmission>
      <#assign guidanceTextOrganisation>
        The responsible party will be removed from this ${resourceType} when the relinquishment submission is approved.
      </#assign>
    <#else>
      <#assign guidanceTextOrganisation>
      </#assign>
    </#if>

    <@fdsForm.htmlForm>
      <@fdsTextInput.textInput
        path="form.resourceName"
        labelText="${resourceType?capitalize} name"
        inputClass="govuk-input--width-20"/>

      <@fdsSearchSelector.searchSelectorRest
        path="form.responsibleOrganisationId"
        restUrl=springUrl(organisationUnitEndpoint)
        labelText="What organisation is responsible for this ${resourceType}?"
        selectorMinInputLength=3
        nestingPath="form.responsibleOrganisationId"
        hintText=guidanceTextOrganisation
        preselectedItems=responsibleOrganisation!{}
        optionalLabel=isOptional/>
      <@fdsRadio.radio
        path="form.onshoreOffshore"
        radioItems=onshoreOffshore
        labelText="Is this ${resourceType} onshore or offshore?"/>

      <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
    </@fdsForm.htmlForm>
</@defaultPage>
<#include '../layout/layout.ftl'>

<#assign pageTitle = "Create a new ${resourceType}" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  backLinkUrl=springUrl(backLinkUrl)
  errorItems=errorList>
  <#if isRegulatorOrAreaTeamUser>
    <#assign isOptional = true>
  <#else>
    <#assign isOptional = false>
  </#if>
  <@fdsForm.htmlForm actionUrl=springUrl(actionUrl)>
    <@fdsTextInput.textInput path="form.resourceName"
      labelText="${resourceType?capitalize} name"
      inputClass="govuk-input--width-20"/>
    <@fdsSearchSelector.searchSelectorRest
      path="form.responsibleOrganisationId"
      restUrl=springUrl(organisationUnitEndpoint)
      labelText="What organisation is responsible for this ${resourceType}?"
      selectorMinInputLength=3
      nestingPath="form.responsibleOrganisationId"
      optionalLabel=isOptional
      preselectedItems=alreadySelectedOrganisation!{}/>
    <@fdsDetails.summaryDetails
      summaryTitle="The organisation I want to select is not shown in the list"
    >
      <p class="govuk-body">
        If the organisation you want to select is not shown in the list then you can <@requestNewCompany.requestCompanyLink/>
      </p>
    </@fdsDetails.summaryDetails>
    <@fdsRadio.radio
      path="form.onshoreOffshore"
      radioItems=onshoreOffshore
      labelText="Is this ${resourceType} onshore or offshore?"/>

    <@fdsAction.submitButtons
      primaryButtonText="Save and continue"
      secondaryLinkText="Cancel"
      linkSecondaryAction=true
      linkSecondaryActionUrl="${springUrl(backLinkUrl)}"/>
  </@fdsForm.htmlForm>
</@defaultPage>
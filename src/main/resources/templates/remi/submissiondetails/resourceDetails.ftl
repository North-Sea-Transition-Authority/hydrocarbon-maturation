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
    <#if isRegulatorOrAreaTeamUser>
      <#assign isOptional = true>
    <#else>
      <#assign isOptional = false>
    </#if>
    <#if relinquishmentSubmission>
      <#assign guidanceTextOrganisation>
        The responsible party will be removed from this ${resourceType} when the relinquishment submission is accepted.
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

        <#if showHistoricNameInfo>
          <#if lockedOrganisationName?has_content>
            <@fdsInsetText.insetText>
              The responsible organisation name at the time of approval was ${lockedOrganisationName}.
              If a new responsible organisation is to be added to this resource this should be completed via a full update to the resource.
            </@fdsInsetText.insetText>
            <#elseif noOrganisationAdded>
              <@fdsInsetText.insetText>
                No responsible organisation was added to this resource.
                If a new responsible organisation is to be added to this resource this should be completed via a full update to the resource.
              </@fdsInsetText.insetText>
            </#if>
        <#else>
          <@fdsSearchSelector.searchSelectorRest
          path="form.responsibleOrganisationId"
          restUrl=springUrl(organisationUnitEndpoint)
          labelText="What organisation is responsible for this ${resourceType}?"
          selectorMinInputLength=3
          nestingPath="form.responsibleOrganisationId"
          hintText=guidanceTextOrganisation
          preselectedItems=responsibleOrganisation!{}
          optionalLabel=isOptional/>
        </#if>

      <@fdsRadio.radio
        path="form.onshoreOffshore"
        radioItems=onshoreOffshore
        labelText="Is this ${resourceType} onshore or offshore?"/>

      <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
    </@fdsForm.htmlForm>
</@defaultPage>
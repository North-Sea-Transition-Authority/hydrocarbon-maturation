<#include '../layout/layout.ftl'>

<#assign pageTitle = "Post-drill discovery submission" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="prospectSelectedOption" type="String" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
backLinkUrl=springUrl(backLinkUrl)>
  <@fdsForm.htmlForm actionUrl=springUrl(actionUrl)>
    <@fdsSearchSelector.searchSelectorRest path="form.prospectSelectedOption" restUrl=springUrl(prospectApiEndpoint) labelText="What prospect is being updated?" selectorMinInputLength=3/>
    <@fdsDetails.summaryDetails summaryTitle="The prospect I want to update is not listed">
      <p class="govuk-body">
        Only prospects that exist on an extant licence and have not been relinquished are shown.
      </p>
      <p class="govuk-body">
        If the prospect you want to update is not shown in the list then you must contact the operator responsible for the prospect to provide you with access to their organisation.        </p>
      <p class="govuk-body">
        If you are already part of the organisation then contact the NSTA for advice.
      </p>
    </@fdsDetails.summaryDetails>
    <@fdsAction.submitButtons primaryButtonText="Save and continue" secondaryLinkText="Cancel" linkSecondaryAction=true linkSecondaryActionUrl="${springUrl(backLinkUrl)}"/>
  </@fdsForm.htmlForm>
</@defaultPage>
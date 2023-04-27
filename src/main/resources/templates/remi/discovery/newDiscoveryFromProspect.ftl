<#include '../layout/layout.ftl'>
<#import '../prospect/newSubmissionGuidance.ftl' as guidance>

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
      <@guidance.newSubmissionGuidanceText resourceTypeString="prospect" actionString="update"/>
    </@fdsDetails.summaryDetails>
    <@fdsAction.submitButtons primaryButtonText="Save and continue" secondaryLinkText="Cancel" linkSecondaryAction=true linkSecondaryActionUrl="${springUrl(backLinkUrl)}"/>
  </@fdsForm.htmlForm>
</@defaultPage>
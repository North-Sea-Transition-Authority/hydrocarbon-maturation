<#include '../layout/layout.ftl'>
<#import '../prospect/newSubmissionGuidance.ftl' as guidance>
<#assign pageTitle = "Prospect Relinquishment" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="prospectSelectedOption" type="String" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  backLinkUrl=springUrl(backLinkUrl)>
  <@fdsForm.htmlForm>
    <@fdsSearchSelector.searchSelectorRest
      path="form.prospectSelectedOption"
      restUrl=springUrl(prospectApiEndpoint)
      labelText="What prospect is being relinquished?"
      selectorMinInputLength=3/>
      <@fdsDetails.summaryDetails summaryTitle="The prospect I want to relinquish is not listed">
        <p class="govuk-body">
          Only prospects with your organisation are shown.
          If a prospect has already progressed to a discovery then you should relinquish the discovery instead.
        </p>
        <@guidance.newSubmissionGuidanceText resourceTypeString="prospect" actionString="relinquish"/>
      </@fdsDetails.summaryDetails>
    <@fdsAction.submitButtons primaryButtonText="Save and continue" secondaryLinkText="Cancel" linkSecondaryAction=true linkSecondaryActionUrl="${springUrl(backLinkUrl)}"/>
  </@fdsForm.htmlForm>
</@defaultPage>
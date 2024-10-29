<#include '../layout/layout.ftl'>
<#import '../prospect/newSubmissionGuidance.ftl' as guidance>
<#assign pageTitle = "Discovery Relinquishment" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="discoverySelectedOption" type="String" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  backLinkUrl=springUrl(backLinkUrl)>
  <@fdsForm.htmlForm>
    <@fdsSearchSelector.searchSelectorRest
      path="form.discoverySelectedOption"
      restUrl=springUrl(discoveryApiEndpoint)
      labelText="What discovery is being relinquished?"
      selectorMinInputLength=3/>
      <@fdsDetails.summaryDetails summaryTitle="The discovery I want to relinquish is not listed">
        <p>
          If the discovery you want to relinquish is not shown in the list then you must contact the operator responsible for the discovery
          to provide you with access to their organisation.
        </p>
        <@guidance.newSubmissionGuidanceText resourceTypeString="discovery" actionString="relinquish">
          <li>
            check if the discovery is associated with a field with a status of 300 or higher.
          </li>
        </@guidance.newSubmissionGuidanceText>
      </@fdsDetails.summaryDetails>
    <@fdsAction.submitButtons primaryButtonText="Save and continue" secondaryLinkText="Cancel" linkSecondaryAction=true linkSecondaryActionUrl="${springUrl(backLinkUrl)}"/>
  </@fdsForm.htmlForm>
</@defaultPage>
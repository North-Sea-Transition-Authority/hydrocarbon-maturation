<#include '../../layout/layout.ftl'>
<#import '../newFieldGuidance.ftl' as guidance>

<#assign pageTitle = "Field development plan (FDP)" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="fieldSelectedOption" type="String" -->
<#-- @ftlvariable name="fieldApiendpoint" type="String" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  backLinkUrl=springUrl(backLinkUrl)>
  <@fdsForm.htmlForm actionUrl=springUrl(actionUrl)>
    <@fdsSearchSelector.searchSelectorRest
      path="form.fieldSelectedOption"
      restUrl=springUrl(fieldApiEndpoint) labelText="What field are you providing a FDP for?"
      selectorMinInputLength=3/>
    <@fdsDetails.summaryDetails summaryTitle="The field I want to update is not listed">
      <@guidance.newFieldGuidanceText statusString="200, 300 or 400"/>
    </@fdsDetails.summaryDetails>
      <@fdsAction.submitButtons primaryButtonText="Save and continue" secondaryLinkText="Cancel" linkSecondaryAction=true linkSecondaryActionUrl="${springUrl(backLinkUrl)}"/>
  </@fdsForm.htmlForm>
</@defaultPage>
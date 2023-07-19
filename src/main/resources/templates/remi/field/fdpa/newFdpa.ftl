<#include '../../layout/layout.ftl'>
<#import '../newFieldGuidance.ftl' as guidance>

<#assign pageTitle = "Field development plan addendum (FDPA)" />
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
      restUrl=springUrl(fieldApiEndpoint) labelText="What field are you providing a FDPA for?"
      selectorMinInputLength=3/>
    <@fdsDetails.summaryDetails summaryTitle="The field I want to update is not listed">
      <@guidance.newFieldGuidanceText statusString="on or after status 600 and before status 900"/>
    </@fdsDetails.summaryDetails>
      <@fdsAction.submitButtons primaryButtonText="Save and continue" secondaryLinkText="Cancel" linkSecondaryAction=true linkSecondaryActionUrl="${springUrl(backLinkUrl)}"/>
  </@fdsForm.htmlForm>
</@defaultPage>
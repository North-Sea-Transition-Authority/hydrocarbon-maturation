<#include '../layout/layout.ftl'>

<#assign pageTitle = "What is the purpose of this submission?" />
<#-- @ftlvariable name="submissionType" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=""
pageSize=PageSize.TWO_THIRDS_COLUMN
errorItems=errorList
backLinkUrl=springUrl(backLinkUrl)>

  <@fdsForm.htmlForm
    actionUrl=springUrl(actionUrl)>

      <@fdsRadio.radio
      path="form.submissionType"
      radioItems=submissionType
      labelText=pageTitle
      fieldsetHeadingSize="h1"
      fieldsetHeadingClass="govuk-fieldset__legend--xl"/>

      <@fdsAction.submitButtons primaryButtonText="Continue" secondaryLinkText="Cancel" linkSecondaryAction=true linkSecondaryActionUrl="${springUrl(backLinkUrl)}"/>
  </@fdsForm.htmlForm>

</@defaultPage>
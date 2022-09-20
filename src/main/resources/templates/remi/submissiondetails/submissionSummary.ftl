<#include '../layout/layout.ftl'>

<#assign pageTitle = "Submission summary" />
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
errorItems=errorList
backLinkUrl=springUrl(backLinkUrl)>

  <@fdsForm.htmlForm>

      <@fdsAction.submitButtons primaryButtonText="Submit" secondaryLinkText="Cancel" linkSecondaryAction=true linkSecondaryActionUrl="${springUrl(backLinkUrl)}"/>
  </@fdsForm.htmlForm>

</@defaultPage>
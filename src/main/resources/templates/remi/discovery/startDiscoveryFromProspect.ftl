<#include '../layout/layout.ftl'>

<#assign pageTitle = "Advise of a post-drill discovery" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
backLinkUrl=springUrl(backLinkUrl)>
  <@fdsStartPage.startPage startActionText="Start now" startActionUrl=actionUrl>
    <p class="govuk-body">
      Use this submission to provide a post-drill update to a prospect that resulted in a discovery.
      You must provide a separate post-drill discovery submission for each discovery made.
    </p>
  </@fdsStartPage.startPage>
</@defaultPage>
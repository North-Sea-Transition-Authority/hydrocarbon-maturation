<#include '../layout/layout.ftl'>

<#assign pageTitle = "Advise of a post-drill dry hole" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
backLinkUrl=springUrl(backLinkUrl)>

  <@fdsStartPage.startPage startActionText="Start now" startActionUrl=actionUrl>
    <p class="govuk-body">
      Use this service to provide a post-drill update to a prospect that resulted in a dry hole.
      You will be required to provide general information about the well drilled and the dry hole.
    </p>
  </@fdsStartPage.startPage>
</@defaultPage>
<#include '../layout/layout.ftl'>

<#assign pageTitle = "Create a new prospect" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
backLinkUrl=springUrl(backLinkUrl)>

  <@fdsStartPage.startPage startActionText="Start now" startActionUrl=actionUrl>
    <p class="govuk-body">
      Use this service to create a new prospect.
    </p>
    <p class="govuk-body">
      You will be required to provide basic information about the prospect. You will optionally be able to provide full details of the prospect if they are available.
    </p>
  </@fdsStartPage.startPage>
</@defaultPage>
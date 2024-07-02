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
      Use this submission to create a new prospect.
    </p>
    <p class="govuk-body">
      You will be required to provide information on the trap, reservoir, hydrocarbon case, chance of success, and volumetrics.
    </p>
  </@fdsStartPage.startPage>
</@defaultPage>
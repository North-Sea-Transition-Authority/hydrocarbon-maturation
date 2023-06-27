<#include '../layout/layout.ftl'>

<#assign pageTitle = "Create a new field" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  backLinkUrl=springUrl(backLinkUrl)>
  <@fdsStartPage.startPage startActionText="Start now" startActionUrl=actionUrl>
    <p class="govuk-body">
      Use this submission to create a new field. A new field will automatically be set to status "200: Under appraisal – no FDP".
    </p>
  </@fdsStartPage.startPage>
</@defaultPage>
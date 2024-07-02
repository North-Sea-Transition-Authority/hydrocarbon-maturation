<#include '../layout/layout.ftl'>

<#assign pageTitle = "Update an existing discovery" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  backLinkUrl=springUrl(backLinkUrl)>
  <@fdsStartPage.startPage startActionText="Start now" startActionUrl=actionUrl>
    <p class="govuk-body">
      Use this submission to provide up to date information on an existing discovery following appraisal activities.
    </p>
  </@fdsStartPage.startPage>
</@defaultPage>
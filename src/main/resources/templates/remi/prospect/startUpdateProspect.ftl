<#include '../layout/layout.ftl'>

<#assign pageTitle = "Update an existing prospect" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
backLinkUrl=springUrl(backLinkUrl)>

  <@fdsStartPage.startPage startActionText="Start now" startActionUrl=actionUrl>
    <p class="govuk-body">
      Use this submission to update the information for an existing prospect.
      This should be completed at key points in the prospect work programme, such as when a well investment decision is made or pre-drill.
    </p>
  </@fdsStartPage.startPage>
</@defaultPage>
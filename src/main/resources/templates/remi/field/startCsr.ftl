<#include '../layout/layout.ftl'>

<#assign pageTitle = "Submit a concept select report (CSR)" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
backLinkUrl=springUrl(backLinkUrl)>
    <@fdsStartPage.startPage startActionText="Start now" startActionUrl=actionUrl>
      <p class="govuk-body">
        Use this submission to submit your CSR for a field. You will only be able to submit this report for fields that have not progressed past the concept select stage.
      </p>

      <p class="govuk-body">
        You will be required to provide information about the trap, field reservoirs and volumes.
      </p>
    </@fdsStartPage.startPage>
</@defaultPage>
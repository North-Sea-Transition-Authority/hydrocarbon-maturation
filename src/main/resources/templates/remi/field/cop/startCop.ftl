<#include '../../layout/layout.ftl'>

<#assign pageTitle = "Submit a cessation of production notification (CoP)" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
backLinkUrl=springUrl(backLinkUrl)>
    <@fdsStartPage.startPage startActionText="Start now" startActionUrl=actionUrl>
      <p class="govuk-body">
        Use this submission to submit your notification of CoP for a field. You will only be able to submit a CoP for fields that have progressed to production.
      </p>

      <p class="govuk-body">
        You will be required to provide information about the trap, field reservoirs and volumes at the time of CoP.
      </p>
    </@fdsStartPage.startPage>
</@defaultPage>
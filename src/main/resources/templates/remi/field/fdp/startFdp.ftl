<#include '../../layout/layout.ftl'>

<#assign pageTitle = "Submit a field development plan (FDP)" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
backLinkUrl=springUrl(backLinkUrl)>
    <@fdsStartPage.startPage startActionText="Start now" startActionUrl=actionUrl>
      <p class="govuk-body">
        Use this submission to submit your FDP for a field. You will only be able to submit an FDP for fields that have not progressed past the field development plan stage.
      </p>

      <p class="govuk-body">
        You will be required to provide information about the trap, field reservoirs and volumes.
      </p>
    </@fdsStartPage.startPage>
</@defaultPage>
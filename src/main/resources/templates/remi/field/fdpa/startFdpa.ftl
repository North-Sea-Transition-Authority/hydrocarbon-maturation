<#include '../../layout/layout.ftl'>

<#assign pageTitle = "Submit a field development plan addendum (FDPA)" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
backLinkUrl=springUrl(backLinkUrl)>
    <@fdsStartPage.startPage startActionText="Start now" startActionUrl=actionUrl>
      <p class="govuk-body">
        Use this submission to submit your FDPA for a field. You will only be able to submit an FDPA for fields that have progressed past the initial field development plan stage.
      </p>

      <p class="govuk-body">
        You will be required to provide information about the trap, field reservoirs, total volumes and volume changes as a result of the further development carried out on the field.
      </p>
    </@fdsStartPage.startPage>
</@defaultPage>
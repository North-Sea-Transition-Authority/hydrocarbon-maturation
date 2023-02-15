<#include '../layout/layout.ftl'>

<#assign pageTitle = "Create a discovery not related to a drill on a prospect" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
backLinkUrl=springUrl(backLinkUrl)>
  <@fdsStartPage.startPage startActionText="Start now" startActionUrl=actionUrl>
    <p class="govuk-body">
      Use this service to create a new discovery.
    </p>
    <p class="govuk-body">
      You will be required to provide information about what was discovered, the trap, reservoir, oil or gas volumes and fluid properties.
    </p>
    <p class="govuk-body">
      If this discovery is being created as a result of an active prospect being progressed, then you should use the post-drill discovery submission to progress that prospect.
    </p>
    <p class="govuk-body">
      This option should only be used when describing an existing discovery that is being relicensed.
    </p>
  </@fdsStartPage.startPage>
</@defaultPage>
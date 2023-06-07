<#include '../layout/layout.ftl'>

<#assign pageTitle = "Advise of a relinquishment" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  backLinkUrl=springUrl(backLinkUrl)>
    <@fdsStartPage.startPage startActionText="Start now" startActionUrl=actionUrl>
      <p class="govuk-body">
        Use this service to advise of a ${resourceType.getDisplayName()?lower_case} being relinquished.
        You should provide the latest information available for the ${resourceType.getDisplayName()?lower_case} as part of your submission.
      </p>
    </@fdsStartPage.startPage>
</@defaultPage>
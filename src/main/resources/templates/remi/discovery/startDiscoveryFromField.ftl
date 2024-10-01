<#include '../layout/layout.ftl'>
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="fieldStatus" type="FieldStatus" -->

<#if fieldStatus.enumName == "STATUS_000">
    <#assign pageTitle = "Create a new discovery from a shelved field" />
  <#else>
    <#assign pageTitle = "Create a new discovery from a ceased production field" />
</#if>

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
backLinkUrl=springUrl(backLinkUrl)>
  <@fdsStartPage.startPage startActionText="Start now" startActionUrl=actionUrl>
    <p class="govuk-body">
      [Insert guidance text here]
    </p>
  </@fdsStartPage.startPage>
</@defaultPage>
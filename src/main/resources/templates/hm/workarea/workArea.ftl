<#include '../layout/layout.ftl'>

<#assign pageTitle = "Work area" />
<#-- @ftlvariable name="startSubmissionUrl" type="String" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN>

    <@fdsAction.link linkText="Create submission" linkUrl=springUrl(startSubmissionUrl) linkClass="govuk-button"/>

</@defaultPage>
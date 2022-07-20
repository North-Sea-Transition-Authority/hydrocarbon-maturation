<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="pageTitle" type="String" -->
<#-- @ftlvariable name="startSubmissionUrl" type="String" -->

<#assign pageTitle = "${pageTitle}" />

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN>

    <@fdsAction.link linkText="Create submission" linkUrl=springUrl(startSubmissionUrl) linkClass="govuk-button"/>

</@defaultPage>
<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="projectStageHistory" type="uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.ProjectStageView" -->
<#-- @ftlvariable name="buttonText" type="String" -->
<#-- @ftlvariable name="buttonClass" type="String" -->
<#-- @ftlvariable name="caption" type="String" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  breadcrumbsMap=breadcrumbsMap
  caption=caption>
  <@fdsForm.htmlForm>
    <#if cannotBeArchivedReason?has_content>
      <@fdsWarning.warning>
        ${cannotBeArchivedReason}
      </@fdsWarning.warning>
      <@fdsBackLink.backLink backLinkText="Back" backLinkUrl=backLinkUrl/>
      <#elseif cannotBeUnarchivedReason?has_content>
        <@fdsWarning.warning>
            ${cannotBeUnarchivedReason}
        </@fdsWarning.warning>
        <@fdsBackLink.backLink backLinkText="Back" backLinkUrl=backLinkUrl/>
    <#else>
      <@fdsAction.submitButtons primaryButtonText="${buttonText}" secondaryLinkText="Cancel" linkSecondaryAction=true
      linkSecondaryActionUrl="${springUrl(cancelUrl)}" primaryButtonClass="govuk-button ${buttonClass}"/>
    </#if>
</@fdsForm.htmlForm>
</@defaultPage>
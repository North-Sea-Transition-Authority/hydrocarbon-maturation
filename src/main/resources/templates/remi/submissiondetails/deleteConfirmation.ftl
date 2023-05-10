<#include '../layout/layout.ftl'>
<#import './summary/_submissionSummary.ftl' as submissionSummary>

<#-- @ftlvariable name="pageName" type="String" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="resourceName" type="String" -->
<#-- @ftlvariable name="deleteUrl" type="String" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
<#-- @ftlvariable name="submissionType" type="String" -->
<#-- @ftlvariable name="isDraftUpdate" type="boolean" -->

<@defaultPage
  htmlTitle=pageName
  pageHeading=pageName
  pageSize=PageSize.FULL_WIDTH
  wrapperWidth=true
  breadcrumbsMap=breadcrumbsMap
  caption=resourceName
>
  <@submissionSummary.submissionSummary
    submissionType=submissionType
    />
  <#assign deleteButtonText>
    <#if isDraftUpdate>
      Delete draft update
    <#else>
      Delete
    </#if>
  </#assign>

  <@fdsForm.htmlForm actionUrl=springUrl(deleteUrl)>
    <@fdsAction.submitButtons
      primaryButtonText=deleteButtonText
      secondaryLinkText="Cancel"
      linkSecondaryAction=true
      linkSecondaryActionUrl=springUrl(cancelUrl)
      primaryButtonClass="govuk-button govuk-button--warning"/>
  </@fdsForm.htmlForm>
</@defaultPage>
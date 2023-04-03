<#include '../layout/layout.ftl'>
<#import '../submissiondetails/summary/_submissionSummary.ftl' as submissionSummary>

<#-- @ftlvariable name="pageName" type="String" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="resourceName" type="String" -->
<#-- @ftlvariable name="deleteUrl" type="String" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="submissionType" type="String" -->

<@defaultPage
  htmlTitle=pageName
  pageHeading=pageName
  pageSize=PageSize.FULL_WIDTH
  wrapperWidth=true
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap
  caption=resourceName>
  <@submissionSummary.submissionSummary
    submissionType=submissionType/>
  <@grid.twoThirdsColumn>
    <@fdsForm.htmlForm actionUrl="${springUrl(actionUrl)}">
      <@fdsTextarea.textarea
        path="form.withdrawReason"
        labelText="Reason for withdrawal"/>
      <@fdsAction.submitButtons
        primaryButtonText="Withdraw submission"
        secondaryLinkText="Cancel"
        linkSecondaryAction=true
        linkSecondaryActionUrl="${springUrl(cancelUrl)}"
        primaryButtonClass="govuk-button govuk-button--warning"/>
    </@fdsForm.htmlForm>
  </@grid.twoThirdsColumn>
</@defaultPage>
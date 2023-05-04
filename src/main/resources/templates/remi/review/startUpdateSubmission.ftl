<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="pageTitle" type="String" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  breadcrumbsMap=breadcrumbsMap>
    <@fdsStartPage.startPage startActionText="Start update" startActionUrl=startUpdateUrl>
      <p class="govuk-body">
        Update your submission with the information requested by ${CUSTOMER_MNEMONIC}.
      </p>
      <@fdsDetails.summaryDetails
        summaryTitle="What information have I been asked to update?">
        <p class="govuk-body govuk-body__preserve-whitespace">${updateRequestReason}</p>
      </@fdsDetails.summaryDetails>
    </@fdsStartPage.startPage>
</@defaultPage>
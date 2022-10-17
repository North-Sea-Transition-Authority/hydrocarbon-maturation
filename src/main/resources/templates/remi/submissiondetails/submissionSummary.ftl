<#include '../layout/layout.ftl'>
<#import '../summarylistcards/generaldetailsSummaryCard.ftl' as generalDetailsSummary>

<#assign pageTitle = "Check your answers before submitting" />
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
errorItems=errorList
breadcrumbsMap=breadcrumbsMap>
  <@fdsForm.htmlForm>
    <@generalDetailsSummary.generalDetailCard
      generalDetailValues=generalDetailsSummaryView/>

    <@fdsAction.submitButtons
      primaryButtonText="Submit"
      secondaryLinkText="Cancel"
      linkSecondaryAction=true
      linkSecondaryActionUrl="${springUrl(cancelUrl)}"/>
  </@fdsForm.htmlForm>

</@defaultPage>
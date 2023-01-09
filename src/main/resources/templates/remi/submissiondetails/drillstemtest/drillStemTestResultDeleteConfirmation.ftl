<#include '../../layout/layout.ftl'>
<#import '../../summarylistcards/drillstemtest/drillStemTestResultSummaryCard.ftl' as drillStemTestResultCard>

<#assign pageTitle = "Are you sure you want to delete this drill stem test result?" />
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
  <@fdsForm.htmlForm actionUrl=springUrl(actionUrl)>
    <@drillStemTestResultCard.drillStemTestResult
      drillStemTestResultValues=drillStemTestResultSummaryView
      withResultNumber=false/>

    <@fdsAction.submitButtons
      primaryButtonText="Delete"
      secondaryLinkText="Cancel"
      linkSecondaryAction=true
      linkSecondaryActionUrl=springUrl(backToSummaryUrl)
      primaryButtonClass="govuk-button govuk-button--warning"/>
  </@fdsForm.htmlForm>
</@defaultPage>
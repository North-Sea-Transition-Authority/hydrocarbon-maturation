<#include '../layout/layout.ftl'>
<#import '../summary/_undevelopedDiscoverySummaryCard.ftl' as undevelopedDiscoverySummary>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  caption=pageCaption
  pageSize=PageSize.FULL_WIDTH
  errorItems=errorList>
  <@fdsForm.htmlForm>
    <#list summaryCards as summaryCard>
      <@undevelopedDiscoverySummary.undevelopedDiscoverySummaryCard summaryCard=summaryCard/>
    </#list>

    <@fdsAction.submitButtons
      primaryButtonText="Delete"
      secondaryLinkText="Cancel"
      linkSecondaryAction=true
      linkSecondaryActionUrl="${springUrl(cancelUrl)}"
      primaryButtonClass="govuk-button govuk-button--warning"/>
  </@fdsForm.htmlForm>
</@defaultPage>
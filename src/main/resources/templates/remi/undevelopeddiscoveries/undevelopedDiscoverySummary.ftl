<#include '../layout/layout.ftl'>
<#import '../summary/_undevelopedDiscoverySummaryCard.ftl' as undevelopedDiscoverySummary>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="summaryCard" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
    <#list summarySections[0].summaryItems()![] as summaryItem>
      <#list summaryItem.summaryCards() as summaryCard>
        <@undevelopedDiscoverySummary.undevelopedDiscoverySummaryCard summaryCard=summaryCard/>
      </#list>
    </#list>
</@defaultPage>
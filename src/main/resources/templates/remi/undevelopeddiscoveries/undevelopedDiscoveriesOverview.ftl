<#include '../layout/layout.ftl'>
<#import '../summary/_tableSummary.ftl' as tableSummary>

<#-- @ftlvariable name="summaryTableView" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableView>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH>
  <@tableSummary.tableSummary summaryTableView=summaryTableView/>
</@defaultPage>
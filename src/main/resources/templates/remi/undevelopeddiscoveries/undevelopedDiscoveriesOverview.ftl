<#include '../layout/layout.ftl'>
<#import '../summary/_tableSummary.ftl' as tableSummary>

<#-- @ftlvariable name="summaryTableView" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableView>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH>
  <@fdsDataItems.dataItem>
    <@fdsDataItems.dataValues value="undeveloped discoveries" key=keyStats.undevelopedDiscoveries()/>
    <@fdsDataItems.dataValues value="licensed undeveloped discoveries" key=keyStats.licensedUndevelopedDiscoveries()/>
    <@fdsDataItems.dataValues value="" key=""/>
  </@fdsDataItems.dataItem>
  <@fdsDataItems.dataItem>
    <@fdsDataItems.dataValues value="mmboe (1C)" key=keyStats.mmboeOneC()/>
    <@fdsDataItems.dataValues value="mmboe (2C)" key=keyStats.mmboeTwoC()/>
    <@fdsDataItems.dataValues value="mmboe (3C)" key=keyStats.mmboeThreeC()/>
  </@fdsDataItems.dataItem>
  <@tableSummary.tableSummary summaryTableView=summaryTableView/>
</@defaultPage>
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
    <@fdsDataItems.dataValues value="group licensed undeveloped discoveries" key=keyStats.groupLicensedUndevelopedDiscoveries()/>
  </@fdsDataItems.dataItem>
  <@fdsDataItems.dataItem>
    <@fdsDataItems.dataValues value="total mmboe (1C)" key=keyStats.totalMmboeOneC()/>
    <@fdsDataItems.dataValues value="total mmboe (2C)" key=keyStats.totalMmboeTwoC()/>
    <@fdsDataItems.dataValues value="total mmboe (3C)" key=keyStats.totalMmboeThreeC()/>
  </@fdsDataItems.dataItem>
  <@fdsDataItems.dataItem>
    <@fdsDataItems.dataValues value="group licensed mmboe (1C)" key=keyStats.groupLicensedMmboeOneC()/>
    <@fdsDataItems.dataValues value="group licensed mmboe (2C)" key=keyStats.groupLicensedMmboeTwoC()/>
    <@fdsDataItems.dataValues value="group licensed mmboe (3C)" key=keyStats.groupLicensedMmboeThreeC()/>
  </@fdsDataItems.dataItem>
  <@fdsDataItems.dataItem>
    <@fdsDataItems.dataValues value="group unlicensed mmboe (1C)" key=keyStats.groupUnlicensedMmboeOneC()/>
    <@fdsDataItems.dataValues value="group unlicensed mmboe (2C)" key=keyStats.groupUnlicensedMmboeTwoC()/>
    <@fdsDataItems.dataValues value="group unlicensed mmboe (3C)" key=keyStats.groupUnlicensedMmboeThreeC()/>
  </@fdsDataItems.dataItem>
  <@fdsTable.sortableTable tableContents=summaryTableView tableId="undeveloped-discoveries" tableCaption="Summary of undeveloped discoveries"/>
</@defaultPage>
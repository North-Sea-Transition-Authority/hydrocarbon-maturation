<#include '../layout/layout.ftl'>
<#import '../summary/_probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="oilCaseVolumetricTotals" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals.DiscoveryOilCaseVolumetricTotalsSummaryView" -->

<#macro oilCaseVolumetricTotalsSummaryCard oilCaseVolumetricTotals>
    <@probabilityDistribution.probabilityDistributionTable
    heading="Total oil case volumetrics"
    summaryCardId="total-oil-case-volumetrics-table"
    withDistributionType=false
    withContingentUncertaintyCategories=true
    tableView=oilCaseVolumetricTotals.tableView()/>
</#macro>
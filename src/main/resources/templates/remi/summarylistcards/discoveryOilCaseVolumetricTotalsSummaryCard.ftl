<#include '../layout/layout.ftl'>
<#import '../summary/_probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="oilCaseVolumetricTotals" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals.DiscoveryOilCaseVolumetricTotalsSummaryView" -->

<#macro oilCaseVolumetricTotalsSummaryCard oilCaseVolumetricTotals>
    <@fdsSummaryList.summaryListCard headingText="Total oil case volumetrics" summaryListId="oilcasevolumetrictotals">
        <@probabilityDistribution.probabilityDistributionTable
        withDistributionType=false
        withContingentUncertaintyCategories=true
        tableView=oilCaseVolumetricTotals.tableView()/>
    </@fdsSummaryList.summaryListCard>
</#macro>
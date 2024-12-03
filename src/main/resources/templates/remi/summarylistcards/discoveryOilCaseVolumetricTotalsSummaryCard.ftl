<#include '../layout/layout.ftl'>
<#import '../summary/_probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="oilCaseVolumetricTotals" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoveryvolumetrics.discoveryoilcasevolumetrictotals.DiscoveryOilCaseVolumetricTotalsSummaryView" -->

<#macro oilCaseVolumetricTotalsSummaryCard oilCaseVolumetricTotals>
    <@fdsSummaryList.summaryListCard headingText="Oil case volumetric totals" summaryListId="oilcasevolumetrictotals">
        <@fdsSummaryList.summaryListRowNoAction keyText="Do you want to use the industry standard conversion for calculating the oil equivalent recoverable values?">
            ${oilCaseVolumetricTotals.useIndustryStandardConversion()}
        </@fdsSummaryList.summaryListRowNoAction>
    </@fdsSummaryList.summaryListCard>

    <@probabilityDistribution.probabilityDistributionTable
    heading="Total oil case volumetrics"
    summaryCardId="total-oil-case-volumetrics-table"
    withDistributionType=false
    withContingentUncertaintyCategories=true
    tableView=oilCaseVolumetricTotals.tableView()/>
</#macro>
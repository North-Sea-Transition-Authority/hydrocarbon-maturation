<#include '../layout/layout.ftl'>
<#import '../summary/_probabilityDistributionSummary.ftl' as probabilityDistribution>
<#import '../summary/tableSummaryCard.ftl' as tableSummaryCard>

<#macro combinedCaseVolumetricTotalsSummaryCard combinedCaseVolumetricTotals>
    <@fdsSummaryList.summaryListCard headingText="Oil and gas case volumetric totals" summaryListId="combinedcasevolumetrictotals">
        <@fdsSummaryList.summaryListRowNoAction keyText="Do you expect to find associated condensate recoverable resources?">
            ${combinedCaseVolumetricTotals.hasAssociatedCondensateRecoverableResources()}
        </@fdsSummaryList.summaryListRowNoAction>
    </@fdsSummaryList.summaryListCard>

    <@probabilityDistribution.probabilityDistributionTable
    heading="Oil and gas case volumetric totals"
    summaryCardId="combinedcasevolumetrictotalsprobabilities"
    withDistributionType=false
    withContingentUncertaintyCategories=true
    tableView=combinedCaseVolumetricTotals.tableView()/>
</#macro>
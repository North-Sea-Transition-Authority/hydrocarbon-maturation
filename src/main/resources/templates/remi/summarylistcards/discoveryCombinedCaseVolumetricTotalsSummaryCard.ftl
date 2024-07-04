<#include '../layout/layout.ftl'>
<#import '../summary/_probabilityDistributionSummary.ftl' as probabilityDistribution>

<#macro combinedCaseVolumetricTotalsSummaryCard combinedCaseVolumetricTotals>
    <@fdsSummaryList.summaryListCard headingText="Oil and gas case volumetric totals" summaryListId="combinedcasevolumetrictotals">
        <@fdsSummaryList.summaryListRowNoAction keyText="Do you expect to find associated condensate recoverable resources?">
            ${combinedCaseVolumetricTotals.hasAssociatedCondensateRecoverableResources()}
        </@fdsSummaryList.summaryListRowNoAction>
    </@fdsSummaryList.summaryListCard>

    <@fdsSummaryList.summaryListCard headingText="Oil and gas case volumetric totals" summaryListId="combinedcasevolumetrictotalsprobabilities">
        <@probabilityDistribution.probabilityDistributionTable
        withDistributionType=false
        withContingentUncertaintyCategories=true
        tableView=combinedCaseVolumetricTotals.tableView()/>
    </@fdsSummaryList.summaryListCard>
</#macro>
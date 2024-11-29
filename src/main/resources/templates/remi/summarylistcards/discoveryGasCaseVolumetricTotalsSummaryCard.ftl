<#include '../layout/layout.ftl'>
<#import '../summary/_probabilityDistributionSummary.ftl' as probabilityDistribution>

<#macro gasCaseVolumetricTotalsSummaryCard gasCaseVolumetricTotals>
    <@fdsSummaryList.summaryListCard headingText="Gas case volumetric totals" summaryListId="gascasevolumetrictotals">
        <@fdsSummaryList.summaryListRowNoAction keyText="Do you expect to find associated condensate recoverable resources?">
            ${gasCaseVolumetricTotals.hasAssociatedCondensateRecoverableResources()}
        </@fdsSummaryList.summaryListRowNoAction>
        <@fdsSummaryList.summaryListRowNoAction keyText="Do you want to use the industry standard conversion for calculating the oil equivalent recoverable values?">
            ${gasCaseVolumetricTotals.useIndustryStandardConversion()}
        </@fdsSummaryList.summaryListRowNoAction>
    </@fdsSummaryList.summaryListCard>

    <@probabilityDistribution.probabilityDistributionTable
    heading="Gas case volumetric totals"
    withDistributionType=false
    withContingentUncertaintyCategories=true
    tableView=gasCaseVolumetricTotals.tableView()
    summaryCardId="gascasevolumetrictotalsprobabilities"/>
</#macro>
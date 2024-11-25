<#include '../layout/layout.ftl'>
<#import '../summary/_probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="oilCaseOutputsValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryView" -->

<#macro oilCaseOutputsSummaryCard oilCaseOutputsValues withContingentUncertaintyCategories=false>
  <@fdsSummaryList.summaryListCard headingText="Oil case outputs (resources)" summaryListId="oilcaseoutputresources">
    <@fdsSummaryList.summaryListRowNoAction keyText="Do you expect to find associated gas recoverable resources?">
      ${oilCaseOutputsValues.hasAssociatedGasRecoverableResources()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Do you want to use the industry standard conversion for calculating the oil equivalent recoverable values?">
      ${oilCaseOutputsValues.useIndustryStandardConversion()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>

  <@probabilityDistribution.probabilityDistributionTable
    heading="Oil case outputs"
    summaryCardId="oil-case-outputs-table"
    withDistributionType=false
    withContingentUncertaintyCategories=withContingentUncertaintyCategories
    tableView=oilCaseOutputsValues.tableView()/>
</#macro>
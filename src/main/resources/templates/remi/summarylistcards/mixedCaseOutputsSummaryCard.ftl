<#include '../layout/layout.ftl'>
<#import '../summary/_probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="mixedCaseOutputsValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryView" -->

<#macro mixedCaseOutputsSummaryCard mixedCaseOutputsValues withContingentUncertaintyCategories=false>
  <@fdsSummaryList.summaryListCard headingText="Mixed case outputs (resources)" summaryListId="mixedcaseoutputresources">
    <@fdsSummaryList.summaryListRowNoAction keyText="Do you expect to find associated gas recoverable resources?">
      ${mixedCaseOutputsValues.hasAssociatedGasRecoverableResources()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Do you expect to find associated condensate recoverable resources?">
        ${mixedCaseOutputsValues.hasAssociatedCondensateRecoverableResources()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>

  <@probabilityDistribution.probabilityDistributionTable
    heading="Mixed case outputs"
    summaryCardId="mixed-case-outputs-table"
    withDistributionType=false
    withContingentUncertaintyCategories=withContingentUncertaintyCategories
    tableView=mixedCaseOutputsValues.tableView()/>
</#macro>
<#include '../layout/layout.ftl'>
<#import '../summary/_probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="gasCaseOutputsValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryView" -->

<#macro gasCaseOutputsSummaryCard gasCaseOutputsValues withContingentUncertaintyCategories=false>
  <@fdsSummaryList.summaryListCard headingText="Gas case outputs (resources)" summaryListId="gascaseoutputresources">
    <@fdsSummaryList.summaryListRowNoAction keyText="Do you expect to find associated condensate recoverable resources?">
      ${gasCaseOutputsValues.hasAssociatedCondensateRecoverableResources()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>

  <@probabilityDistribution.probabilityDistributionTable
    heading="Gas case outputs"
    summaryCardId="gas-case-outputs-table"
    withDistributionType=false
    withContingentUncertaintyCategories=withContingentUncertaintyCategories
    tableView=gasCaseOutputsValues.tableView()/>
</#macro>
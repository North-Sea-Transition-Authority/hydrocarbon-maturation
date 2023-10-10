<#include '../layout/layout.ftl'>
<#import '../summary/_probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="gasCaseOutputsValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryView" -->

<#macro gasCaseOutputsSummaryCard gasCaseOutputsValues withContingentUncertaintyCategories=false>
  <@fdsSummaryList.summaryListCard headingText="Gas case outputs (resources)" summaryListId="gascaseoutputresources">
    <@fdsSummaryList.summaryListRowNoAction keyText="Do you expect to find associated condensate recoverable resources?">
      ${gasCaseOutputsValues.hasAssociatedCondensateRecoverableResources()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>

    <@fdsSummaryList.summaryListCard headingText="Gas case outputs" summaryListId="gascaseoutputs">
      <@probabilityDistribution.probabilityDistributionTable
        withDistributionType=false
        withContingentUncertaintyCategories=withContingentUncertaintyCategories
        probabilityDistributions=gasCaseOutputsValues.probabilityDistributionSummaryViews()/>
    </@fdsSummaryList.summaryListCard>
</#macro>
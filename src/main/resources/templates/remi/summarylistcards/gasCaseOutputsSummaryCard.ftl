<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="gasCaseOutputsValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryView" -->

<#macro gasCaseOutputsSummaryCard gasCaseOutputsValues>
  <@fdsSummaryList.summaryListCard headingText="Gas case outputs (resources)" summaryListId="gascaseoutputs(resources)">
    <@fdsSummaryList.summaryListRowNoAction keyText="Do you expect to find associated condensate recoverable resources?">
      ${gasCaseOutputsValues.hasAssociatedCondensateRecoverableResources()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>

  <@probabilityDistribution.probabilityDistributionTable
    withDistributionType=false
    probabilityDistributions=gasCaseOutputsValues.probabilityDistributionSummaryViews()/>
</#macro>
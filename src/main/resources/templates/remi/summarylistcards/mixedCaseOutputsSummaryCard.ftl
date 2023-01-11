<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="mixedCaseOutputsValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryView" -->

<#macro mixedCaseOutputsSummaryCard mixedCaseOutputsValues>
  <@fdsSummaryList.summaryListCard headingText="Mixed case outputs (resources)" summaryListId="mixedcaseoutputresources">
    <@fdsSummaryList.summaryListRowNoAction keyText="Do you expect to find associated gas recoverable resources?">
      ${mixedCaseOutputsValues.hasAssociatedGasRecoverableResources()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Risked oil equivalent recoverable mean">
      ${mixedCaseOutputsValues.riskedOilEquivalentRecoverableMean()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>

    <@fdsSummaryList.summaryListCard headingText="Mixed case outputs (resources)" summaryListId="mixedcaseoutputs">
        <@probabilityDistribution.probabilityDistributionTable
          withDistributionType=false
          probabilityDistributions=mixedCaseOutputsValues.probabilityDistributionSummaryViews()/>
    </@fdsSummaryList.summaryListCard>
</#macro>
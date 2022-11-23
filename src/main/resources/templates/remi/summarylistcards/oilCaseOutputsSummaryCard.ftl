<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="oilCaseOutputsValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryView" -->

<#macro oilCaseOutputsSummaryCard oilCaseOutputsValues>
  <@fdsSummaryList.summaryListCard headingText="Oil case outputs (resources)" summaryListId="oilcaseoutputresources">
    <@fdsSummaryList.summaryListRowNoAction keyText="Do you expect to find associated gas recoverable resources?">
      ${oilCaseOutputsValues.hasAssociatedGasRecoverableResources()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Risked oil equivalent recoverable mean">
      ${oilCaseOutputsValues.riskedOilEquivalentRecoverableMean()}
    </@fdsSummaryList.summaryListRowNoAction>
  </@fdsSummaryList.summaryListCard>

    <@fdsSummaryList.summaryListCard headingText="Oil case outputs" summaryListId="oilcaseoutputs">
        <@probabilityDistribution.probabilityDistributionTable
          withDistributionType=false
          probabilityDistributions=oilCaseOutputsValues.probabilityDistributionSummaryViews()/>
    </@fdsSummaryList.summaryListCard>
</#macro>
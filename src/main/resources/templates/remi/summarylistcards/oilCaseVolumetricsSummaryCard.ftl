<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="oilCaseVolumetricsValues" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView" -->

<#macro oilCaseVolumetricsSummary oilCaseVolumetricsValues>
    <@fdsSummaryList.summaryListCard headingText="Oil case volumetric parameters" summaryListId="oilcasevolumetricparams">
        <@probabilityDistribution.probabilityDistributionTable probabilityDistributions=oilCaseVolumetricsValues.probabilityDistributionSummaryViews()/>
    </@fdsSummaryList.summaryListCard>
</#macro>
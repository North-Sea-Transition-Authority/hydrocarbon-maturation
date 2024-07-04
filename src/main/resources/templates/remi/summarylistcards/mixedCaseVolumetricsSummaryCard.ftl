<#include '../layout/layout.ftl'>
<#import '../summary/_probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="mixedCaseVolumetricsValues" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView" -->

<#macro mixedCaseVolumetricsSummary mixedCaseVolumetricsValues>
    <@fdsSummaryList.summaryListCard headingText="Mixed case volumetric parameters" summaryListId="mixedcasevolumetricparams">
        <@probabilityDistribution.probabilityDistributionTable
        tableView=mixedCaseVolumetricsValues/>
    </@fdsSummaryList.summaryListCard>
</#macro>
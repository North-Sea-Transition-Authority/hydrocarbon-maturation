<#include '../layout/layout.ftl'>
<#import '../summary/_probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="oilCaseVolumetricsValues" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView" -->

<#macro oilCaseVolumetricsSummary oilCaseVolumetricsValues>
    <@fdsSummaryList.summaryListCard headingText="Oil case volumetric parameters" summaryListId="oilcasevolumetricparams">
        <@probabilityDistribution.probabilityDistributionTable
        tableView=oilCaseVolumetricsValues/>
    </@fdsSummaryList.summaryListCard>
</#macro>
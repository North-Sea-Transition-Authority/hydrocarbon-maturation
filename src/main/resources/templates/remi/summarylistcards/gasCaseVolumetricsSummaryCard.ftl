<#include '../layout/layout.ftl'>
<#import '../summary/_probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="gasCaseVolumetricsValues" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView" -->

<#macro gasCaseVolumetricsSummary gasCaseVolumetricsValues>
    <@fdsSummaryList.summaryListCard headingText="Gas case volumetric parameters" summaryListId="gascasevolumetricparams">
        <@probabilityDistribution.probabilityDistributionTable
        tableView=gasCaseVolumetricsValues/>
    </@fdsSummaryList.summaryListCard>
</#macro>
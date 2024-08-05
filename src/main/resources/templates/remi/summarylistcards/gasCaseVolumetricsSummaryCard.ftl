<#include '../layout/layout.ftl'>
<#import '../summary/_probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="gasCaseVolumetricsValues" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView" -->

<#macro gasCaseVolumetricsSummary gasCaseVolumetricsValues>
    <@probabilityDistribution.probabilityDistributionTable
    heading="Gas case volumetric parameters"
    summaryCardId="gas-case-volumetric-parameters-table"
    tableView=gasCaseVolumetricsValues/>
</#macro>
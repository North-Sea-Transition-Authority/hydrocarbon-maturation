<#include '../layout/layout.ftl'>
<#import '../summary/_probabilityDistributionSummary.ftl' as probabilityDistribution>
<#import '../summary/tableSummaryCard.ftl' as tableSummaryCard>

<#-- @ftlvariable name="oilCaseVolumetricsValues" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTableView" -->

<#macro oilCaseVolumetricsSummary oilCaseVolumetricsValues>
    <@probabilityDistribution.probabilityDistributionTable
    heading="Oil case volumetric parameters"
    summaryCardId="oil-case-volumetric-parameters"
    tableView=oilCaseVolumetricsValues/>
</#macro>
<#include '../layout/layout.ftl'>
<#import '../summary/_probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="mixedCaseVolumetricsValues" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView" -->

<#macro mixedCaseVolumetricsSummary mixedCaseVolumetricsValues>
    <@probabilityDistribution.probabilityDistributionTable
    heading="Mixed case volumetric parameters"
    summaryCardId="mixed-case-volumetric-parameters-table"
    tableView=mixedCaseVolumetricsValues/>
</#macro>
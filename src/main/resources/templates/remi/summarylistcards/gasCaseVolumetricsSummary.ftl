<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="gasCaseVolumetricsValues" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView" -->

<#macro gasCaseVolumetricsSummary gasCaseVolumetricsValues>
  <@fdsSummaryList.summaryListWrapper headingClass="fds-summary-list-card__heading" headingSize="h2" headingText="Gas case volumetrics parameters" summaryListId="gascasevolumetricsparameters"/>
    <@probabilityDistribution.probabilityDistributionTable
    probabilityDistributions=gasCaseVolumetricsValues.probabilityDistributionSummaryViews()/>
</#macro>
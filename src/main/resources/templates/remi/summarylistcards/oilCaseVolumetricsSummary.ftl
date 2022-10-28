<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="oilCaseVolumetricsValues" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView" -->

<#macro oilCaseVolumetricsSummary oilCaseVolumetricsValues>
  <@fdsSummaryList.summaryListWrapper headingClass="fds-summary-list-card__heading" headingSize="h2" headingText="Oil case volumetrics parameters" summaryListId="oilcasevolumetricsparameters"/>
  <@probabilityDistribution.probabilityDistributionTable
    probabilityDistributions=oilCaseVolumetricsValues.probabilityDistributionSummaryViews()/>
</#macro>
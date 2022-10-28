<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilityDistributionSummary.ftl' as probabilityDistribution>

<#-- @ftlvariable name="mixedCaseVolumetricsValues" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView" -->

<#macro mixedCaseVolumetricsSummary mixedCaseVolumetricsValues>
  <@fdsSummaryList.summaryListWrapper headingClass="fds-summary-list-card__heading" headingSize="h2" headingText="Mixed case volumetrics parameters" summaryListId="mixedcasevolumetricsparameters"/>
  <@probabilityDistribution.probabilityDistributionTable
    probabilityDistributions=mixedCaseVolumetricsValues.probabilityDistributionSummaryViews()/>
</#macro>
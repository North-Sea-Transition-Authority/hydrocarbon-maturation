<#include '../layout/layout.ftl'>
<#import '../probabilitydistribution/probabilityDistributionSummary.ftl' as probabilityDistribution>
<#import 'fileUploadSummaryCard.ftl' as fileUploadSummary>

<#-- @ftlvariable name="trapPropertiesValues" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryView" -->

<#macro trapPropertiesSummaryCard trapPropertiesValues>
  <@fdsSummaryList.summaryListCard headingText="Trap properties" summaryListId="trapproperties">
    <@fdsSummaryList.summaryListRowNoAction keyText="Trap description">
      ${trapPropertiesValues.trapDescription()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Trap type">
      ${trapPropertiesValues.trapType()}
    </@fdsSummaryList.summaryListRowNoAction>
    <#if trapPropertiesValues.trapType() == "Other">
      <@fdsSummaryList.summaryListRowNoAction keyText="Other trap type">
        ${trapPropertiesValues.otherTrapType()}
      </@fdsSummaryList.summaryListRowNoAction>
    </#if>
    <@fdsSummaryList.summaryListRowNoAction keyText="Centre of interest latitude ">
      ${trapPropertiesValues.latitudeDegrees()}
      ${trapPropertiesValues.latitudeMinutes()}
      ${trapPropertiesValues.latitudeSeconds()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Centre of interest longitude">
      ${trapPropertiesValues.longitudeDegrees()}
      ${trapPropertiesValues.longitudeMinutes()}
      ${trapPropertiesValues.longitudeSeconds()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Longitude hemisphere">
      ${trapPropertiesValues.longitudeHemisphere()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Centre of interest datum">
      ${trapPropertiesValues.datum()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fdsSummaryList.summaryListRowNoAction keyText="Quadrant and block the centre of interest is within">
      ${trapPropertiesValues.quadAndBlockCentreOfInterest()}
    </@fdsSummaryList.summaryListRowNoAction>
    <@fileUploadSummary.fileUploadSummary
      filesUploaded=trapPropertiesValues.prospectDepthMap()
      fieldName="Prospect depth map"/>
  </@fdsSummaryList.summaryListCard>

    <@fdsSummaryList.summaryListCard headingText="Trap property distributions" summaryListId="trappropertydistributions">
        <@probabilityDistribution.probabilityDistributionTable probabilityDistributions=trapPropertiesValues.probabilityDistributionSummaryViews()/>
    </@fdsSummaryList.summaryListCard>
</#macro>
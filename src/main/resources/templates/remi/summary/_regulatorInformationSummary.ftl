<#import '../summarylistcards/shapeMapSummaryCard.ftl' as shapeMapSummaryCard>
<#import '_simpleSummary.ftl' as simpleSummary>
<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="fieldMapView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.FieldMapView" -->
<#-- @ftlvariable name="fieldMapView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.FieldMapView" -->

<#macro regulatorInformationSummary fieldRegulatorInformationCard fieldMapView canViewRegulatorManagementInfo associatedDiscoveriesMap linkedPostFieldDiscoveriesMap canSeePostFieldLink>
    <@simpleSummary.simpleSummaryCard
      summaryDataView=fieldRegulatorInformationCard.summaryData()
      summaryHeading=fieldRegulatorInformationCard.displayName()!"">
      <#if canViewRegulatorManagementInfo>
        <@fdsSummaryList.summaryListRowNoAction keyText="Associated discoveries">
          <#list associatedDiscoveriesMap as link, name>
            <div>
              <@fdsAction.link linkText=name linkUrl=springUrl(link)/>
            </div>
          </#list>
        </@fdsSummaryList.summaryListRowNoAction>
        <#if canSeePostFieldLink && linkedPostFieldDiscoveriesMap?has_content>
          <@fdsSummaryList.summaryListRowNoAction keyText="Linked post field discoveries">
            <#list linkedPostFieldDiscoveriesMap as link, name>
              <div>
                <@fdsAction.link linkText=name linkUrl=springUrl(link)/>
              </div>
            </#list>
          </@fdsSummaryList.summaryListRowNoAction>
        </#if>
      </#if>
      <#if fieldMapView?has_content>
        <@shapeMapSummaryCard.shapeMapSummary
          geoJsonDataUrl=fieldMapView.fieldGeoJsonUrl()
          dataMapAttributions=""
          shapeName=fieldMapView.fieldName()
          keyText="Field map"
          showShapeNameKey=false/>
      <#else>
        <@fdsSummaryList.summaryListRowNoAction keyText="Field map">
          Field shape information is not currently available.
        </@fdsSummaryList.summaryListRowNoAction>
      </#if>
    </@simpleSummary.simpleSummaryCard>
</#macro>
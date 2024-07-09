<#import '../summarylistcards/shapeMapSummaryCard.ftl' as shapeMapSummaryCard>
<#import '_simpleSummary.ftl' as simpleSummary>

<#-- @ftlvariable name="fieldMapView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.FieldMapView" -->
<#-- @ftlvariable name="fieldMapView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.FieldMapView" -->

<#macro regulatorInformationSummary fieldRegulatorInformationCard fieldMapView>
    <@simpleSummary.simpleSummaryCard
      summaryDataView=fieldRegulatorInformationCard.summaryData()
      summaryHeading=fieldRegulatorInformationCard.displayName()!"">
        <@shapeMapSummaryCard.shapeMapSummary
          geoJsonDataUrl=fieldMapView.fieldGeoJsonUrl()
          dataMapAttributions=""
          shapeName=fieldMapView.fieldName()
          keyText="Field map"/>
    </@simpleSummary.simpleSummaryCard>
</#macro>
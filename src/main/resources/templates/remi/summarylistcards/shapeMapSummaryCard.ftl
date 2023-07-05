<#include '../layout/layout.ftl'>
<#import '../maps/shapeMap.ftl' as maps/>
<#-- @ftlvariable name="geoJsonDataUrl" type="String" -->
<#-- @ftlvariable name="dataMapAttributions" type="String" -->
<#-- @ftlvariable name="shapeName" type="String" -->

<#macro shapeMapSummary geoJsonDataUrl dataMapAttributions shapeName>
    <@fdsSummaryList.summaryListRowNoAction keyText="Shapefile map" rowClass="govuk-summary-list__row--align-key-top">
      <@maps.shapeMap
        geoJsonDataUrl=geoJsonDataUrl
        dataMapAttributions=dataMapAttributions
        shapeName=shapeName
        showFieldDeterminationBoundariesLayer=true
        showQuadBlocksLayer=true
        showLicenceBlocksLayer=true
        showFieldBlobsLayer=true
        />
    </@fdsSummaryList.summaryListRowNoAction>
</#macro>
<#include '../layout/layout.ftl'>
<#import '../maps/shapeMap.ftl' as maps/>
<#-- @ftlvariable name="geoJsonDataUrl" type="String" -->
<#-- @ftlvariable name="dataMapAttributions" type="String" -->

<#macro shapeMapSummary geoJsonDataUrl dataMapAttributions>
    <@fdsSummaryList.summaryListRowNoAction keyText="Shapefile map">
      <@maps.shapeMap
        geoJsonDataUrl=geoJsonDataUrl
        dataMapAttributions=dataMapAttributions
        showFieldDeterminationBoundariesLayer=true
        showQuadBlocksLayer=true
        showLicenceBlocksLayer=true
        showFieldBlobsLayer=true
        />
    </@fdsSummaryList.summaryListRowNoAction>
</#macro>
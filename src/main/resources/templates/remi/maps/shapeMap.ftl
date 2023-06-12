<#include '../layout/layout.ftl'>
<#import "../ifie/ifIe.ftl" as ifIe>

<#macro shapeMap
  geoJsonDataUrl <#-- The url to fetch this shape geojson data from. -->
  dataMapAttributions <#-- Map attributions to show with map -->
  showFieldDeterminationBoundariesLayer=false <#-- Show field determination boundaries on map. -->
  showQuadBlocksLayer=false <#-- Show quad/blocks on map. -->
  showLicenceBlocksLayer=false <#-- Show licence blocks on map. -->
  showFieldBlobsLayer=false <#-- Show field blobs on map. -->
>

  <@ifIe.showIfIe>
    <@fdsInsetText.insetText>
      Maps can't be shown in Internet Explorer. Use a different web browser to view the map.
    </@fdsInsetText.insetText>
  </@ifIe.showIfIe>

  <@ifIe.hideIfIe>
    <div
      class="shape-map__container"
      data-module="shape-map"
      data-geojson-url="${geoJsonDataUrl}"
      data-map-attributions="${dataMapAttributions}"
      data-show-field-determination-boundaries-layer="${showFieldDeterminationBoundariesLayer?string}"
      data-show-quad-blocks-layer="${showQuadBlocksLayer?string}"
      data-show-licence-blocks-layer="${showLicenceBlocksLayer?string}"
      data-show-field-blobs-layer="${showFieldBlobsLayer?string}"
    >
      <div class="shape-map__legend">
        <#if showQuadBlocksLayer>
          <div class="shape-map__legend-item"><span class="shape-map__legend-box" id="quad-blocks-legend"></span>Quadrant Blocks</div>
        </#if>
        <#if showLicenceBlocksLayer>
          <div class="shape-map__legend-item"><span class="shape-map__legend-box" id="license-blocks-legend"></span>Licence Blocks</div>
        </#if>
        <#if showFieldDeterminationBoundariesLayer>
          <div class="shape-map__legend-item"><span class="shape-map__legend-box" id="field-determination-boundaries-legend"></span>Field Determination Boundaries</div>
        </#if>
        <#if showFieldBlobsLayer>
          <div class="shape-map__legend-item"><span class="shape-map__legend-box" id="fields-legend"></span>Fields</div>
        </#if>
      </div>
    </div>
  </@ifIe.hideIfIe>

</#macro>
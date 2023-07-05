<#include '../layout/layout.ftl'>
<#import "../ifie/ifIe.ftl" as ifIe>

<#macro shapeMap
  geoJsonDataUrl <#-- The url to fetch this shape geojson data from. -->
  dataMapAttributions <#-- Map attributions to show with map -->
  shapeName <#-- Name of shape being displayed -->
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
          <div class="shape-map__legend-item"><span class="shape-map__legend-box" id="quad-blocks-legend"></span>Quadrant blocks</div>
        </#if>
        <#if showLicenceBlocksLayer>
          <div class="shape-map__legend-item"><span class="shape-map__legend-box" id="license-blocks-legend"></span>Licence blocks</div>
        </#if>
        <#if showFieldDeterminationBoundariesLayer>
          <div class="shape-map__legend-item"><span class="shape-map__legend-box" id="field-determination-boundaries-legend"></span>Field determination boundaries</div>
        </#if>
        <#if showFieldBlobsLayer>
          <div class="shape-map__legend-item"><span class="shape-map__legend-box" id="gas-fields-legend"></span>Gas fields</div>
          <div class="shape-map__legend-item"><span class="shape-map__legend-box" id="oil-fields-legend"></span>Oil fields</div>
          <div class="shape-map__legend-item"><span class="shape-map__legend-box" id="cond-fields-legend"></span>Condensate fields</div>
        </#if>
        <div class="shape-map__legend-item"><span class="shape-map__legend-box" id="shape-legend"></span>${shapeName}</div>
      </div>
    </div>
  </@ifIe.hideIfIe>

</#macro>
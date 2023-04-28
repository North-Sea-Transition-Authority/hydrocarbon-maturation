<#include '../layout/layout.ftl'>
<#import "../ifie/ifIe.ftl" as ifIe>

<#macro shapeMap
  geoJsonDataUrl <#-- The url to fetch this shape geojson data from. -->
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
    ></div>
  </@ifIe.hideIfIe>

</#macro>
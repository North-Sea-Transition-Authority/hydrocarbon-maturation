<#include '../layout/layout.ftl'>

<#macro shapeMap
    geoJsonDataUrl <#-- The url to fetch this shape geojson data from. -->
>
  <#-- TODO: HM-493 - Port IfIE Macro from EIP to disable map on IE -->

  <div
    class="shape-map__container"
    data-module="shape-map"
    data-geojson-url="${geoJsonDataUrl}"
  ></div>

</#macro>
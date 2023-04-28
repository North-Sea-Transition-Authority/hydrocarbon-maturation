<#include '../layout/layout.ftl'>
<#import "../ifie/ifIe.ftl" as ifIe>

<#macro shapeMap
  geoJsonDataUrl <#-- The url to fetch this shape geojson data from. -->
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
    ></div>
  </@ifIe.hideIfIe>

</#macro>
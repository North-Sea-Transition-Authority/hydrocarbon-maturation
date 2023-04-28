<#macro showIfIe>
<#--  hidden by default -->
  <div class="if-ie--hidden" data-module="show-if-ie">
      <#nested>
  </div>
</#macro>

<#macro hideIfIe>
  <div data-module="hide-if-ie">
      <#nested>
  </div>
</#macro>
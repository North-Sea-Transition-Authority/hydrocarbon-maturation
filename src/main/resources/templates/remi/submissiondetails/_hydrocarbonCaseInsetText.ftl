<#include '../layout/layout.ftl'>

<#macro hydrocarbonCaseInsetText hydrocarbonCase>
  <@fdsInsetText.insetText>
    This is a ${hydrocarbonCase.getInlineDisplayText()} field. If this is incorrect you must update the hydrocarbons discovered in the field reservoirs section.
  </@fdsInsetText.insetText>
</#macro>
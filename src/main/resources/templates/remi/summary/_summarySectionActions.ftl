<#include '../layout/layout.ftl'>

<#macro summarySectionActions actions>
  <#if actions?has_content>
    <@fdsAction.buttonGroup>
      <#list actions as summarySectionAction>
          <@fdsAction.link
          linkClass="govuk-link govuk-!-font-size-19"
          linkText=summarySectionAction.label()
          linkScreenReaderText=summarySectionAction.screenReaderText()
          linkUrl=springUrl(summarySectionAction.url())/>
      </#list>
    </@fdsAction.buttonGroup>
  </#if>
</#macro>
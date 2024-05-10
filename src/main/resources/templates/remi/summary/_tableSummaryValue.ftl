<#include '../layout/layout.ftl'>
<#import '_tag.ftl' as tag>

<#-- @ftlvariable name="tableValue" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTableValue>" -->

<#macro tableSummaryValue tableValue>
    <#assign value=tableValue.value()!""/>
    <#if tableValue.link()?has_content>
      <td class="govuk-table__cell">
        <@fdsAction.link linkText="${value}" linkUrl=springUrl(tableValue.link())
          linkScreenReaderText="View summary for ${value}"/>
        <@tag.tagList tags=tableValue.tags()/>
      </td>
    <#else>
      <td class="govuk-table__cell">
        ${value}
        <@tag.tagList tags=tableValue.tags()/>
      </td>
    </#if>
</#macro>
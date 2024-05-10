<#-- @ftlvariable name="tagRecord" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.summary.Tag>" -->
<#-- https://design-system.service.gov.uk/components/tag/ / https://design-system.fivium.co.uk/components/tag -->

<#macro tag tagRecord>
  <#assign tagColour=tagRecord.tagColour()?c_lower_case/>
  <strong class="govuk-tag govuk-tag--${tagColour}">${tagRecord.tagText()}</strong>
</#macro>

<#macro tagList tags>
  <#if tags?has_content>
    <#list tags as tagRecord>
      <@tag tagRecord=tagRecord/>
    </#list>
  </#if>
</#macro>
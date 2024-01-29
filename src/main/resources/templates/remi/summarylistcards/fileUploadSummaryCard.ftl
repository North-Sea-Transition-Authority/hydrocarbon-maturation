<#include '../layout/layout.ftl'>
<#-- @ftlvariable name="filesUploaded" type="java.util.HashMap<uk.co.fivium.fileuploadlibrary.core.UploadedFile, String>" -->

<#macro fileUploadSummary filesUploaded={} fieldName="">
  <@fdsSummaryList.summaryListRowNoAction keyText="Supporting document type">
    ${fieldName}
  </@fdsSummaryList.summaryListRowNoAction>
  <#if filesUploaded?has_content>
    <#list filesUploaded as file, url>
      <@fdsSummaryList.summaryListRowNoAction keyText="File name">
      <#if file.getName()?has_content>
        <@fdsAction.link linkText="${file.getName()}" linkUrl=springUrl("${url}")/>
      </#if>
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="File description">
      <#if file.getDescription()?has_content>
        <p class="govuk-body govuk-body__preserve-whitespace">${file.getDescription()}</p>
      </#if>
      </@fdsSummaryList.summaryListRowNoAction>
    </#list>
  <#else>
    <@fdsSummaryList.summaryListRowNoAction keyText="File name"/>
    <@fdsSummaryList.summaryListRowNoAction keyText="File description"/>
  </#if>
</#macro>
<#include '../layout/layout.ftl'>
<#-- @ftlvariable name="filesUploaded" type="java.util.HashMap<uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile, String>" -->

<#macro fileUploadSummary filesUploaded={} fieldName="">
  <@fdsSummaryList.summaryListRowNoAction keyText="Supporting document type">
    ${fieldName}
  </@fdsSummaryList.summaryListRowNoAction>
  <#if filesUploaded?has_content>
    <#list filesUploaded as file, url>
      <@fdsSummaryList.summaryListRowNoAction keyText="File name">
      <#if file.getFilename()?has_content>
        <@fdsAction.link linkText="${file.getFilename()}" linkUrl=springUrl("${url}")/>
      </#if>
      </@fdsSummaryList.summaryListRowNoAction>
      <@fdsSummaryList.summaryListRowNoAction keyText="File description">
      <#if file.getDescription()?has_content>
        ${file.getDescription()}
      </#if>
      </@fdsSummaryList.summaryListRowNoAction>
    </#list>
  <#else>
    <@fdsSummaryList.summaryListRowNoAction keyText="File name"/>
    <@fdsSummaryList.summaryListRowNoAction keyText="File description"/>
  </#if>
</#macro>
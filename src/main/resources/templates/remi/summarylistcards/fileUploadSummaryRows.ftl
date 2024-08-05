<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="filesUploaded" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryFileViewRow.java>" -->

<#macro fileUploadSummary filesUploaded>
  <#list filesUploaded as file>
    <@fileUploadSummaryRow fileUploaded=file.fileView() key=file.key()/>
  </#list>
</#macro>

<#macro fileUploadSummaryRow fileUploaded key>
  <@fdsSummaryList.summaryListRowNoAction keyText=key>
    <#if fileUploaded?has_content>
      <#if fileUploaded.filename()?has_content>
        <@fdsAction.link linkText="${fileUploaded.filename()}" linkUrl=springUrl("${fileUploaded.downloadUrl()}")/>
      </#if>
      <#if fileUploaded.description()?has_content>
        <p class="govuk-body govuk-body__preserve-whitespace">${fileUploaded.description()}</p>
      </#if>
    </#if>
  </@fdsSummaryList.summaryListRowNoAction>
</#macro>
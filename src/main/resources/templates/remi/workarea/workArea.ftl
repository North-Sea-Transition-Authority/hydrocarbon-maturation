<#include '../layout/layout.ftl'>
<#import 'submissionWorkAreaItem.ftl' as submissionWorkAreaItem>
<#-- @ftlvariable name="pageTitle" type="String" -->
<#-- @ftlvariable name="startSubmissionUrl" type="String" -->
<#-- @ftlvariable name="workAreaItems" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaItem>" -->

<#assign pageTitle = "${pageTitle}" />

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  noIndex=false>
  <#if canCreateSubmissions>
    <@fdsAction.link linkText="Create submission" linkUrl=springUrl(startSubmissionUrl) linkClass="govuk-button"/>
  </#if>
  <#if workAreaItems?has_content>
    <@fdsResultList.resultList resultCountSuffix="submission" resultCount=workAreaItems?size>
      <#list workAreaItems as workAreaItem>
        <#if workAreaItem.type() == "SUBMISSION">
          <@submissionWorkAreaItem.submissionWorkAreaItem workAreaItem=workAreaItem/>
        </#if>
      </#list>
    </@fdsResultList.resultList>
  <#else>
    <@fdsInsetText.insetText>
      You have no in-progress submissions assigned to your team.
    </@fdsInsetText.insetText>
  </#if>
</@defaultPage>
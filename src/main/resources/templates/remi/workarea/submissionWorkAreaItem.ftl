<#include '../layout/layout.ftl'>
<#-- @ftlvariable name="workAreaItem" type="uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaItem" -->



<#macro submissionWorkAreaItem workAreaItem>
  <#assign modelProperties = workAreaItem.modelProperties().properties />
    <#assign updateRequestedTag>
      <#if modelProperties["updateRequested"]>
        <@fdsResultList.resultListTag
          tagText="Update requested"
          tagClass="govuk-tag--blue govuk-tag--float-right"/>
      </#if>
    </#assign>
    <@fdsResultList.resultListItem
      captionHeadingText=workAreaItem.captionText()!""
      linkHeadingUrl=springUrl(workAreaItem.actionUrl())
      linkHeadingText=workAreaItem.headingText()
      itemTag=updateRequestedTag>

    <@fdsResultList.resultListDataItem>
      <@fdsResultList.resultListDataValue
        key="Status"
        value=modelProperties["submissionStatus"]/>
      <@fdsResultList.resultListDataValue
        key="Responsible organisation"
        value=modelProperties["organisationResponsible"]/>
      <#assign licencesDisplay>
        <#if modelProperties["licences"]?has_content>
          <#list modelProperties["licences"] as licence>
            ${licence}<#if licence?has_next>,</#if>
          </#list>
        </#if>
      </#assign>
      <@fdsResultList.resultListDataValue
        key="Licences"
        value=licencesDisplay/>
    </@fdsResultList.resultListDataItem>
  </@fdsResultList.resultListItem>
</#macro>
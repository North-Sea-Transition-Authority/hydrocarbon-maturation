<#include '../layout/layout.ftl'>
<#-- @ftlvariable name="workAreaItem" type="uk.co.nstauthority.hydrocarbonmaturation.workarea.WorkAreaItem" -->

<#macro submissionWorkAreaItem workAreaItem>
  <#assign modelProperties = workAreaItem.modelProperties().properties />
    <@fdsResultList.resultListItem
      captionHeadingText=workAreaItem.captionText()!""
      linkHeadingUrl=springUrl(workAreaItem.actionUrl())
      linkHeadingText=workAreaItem.headingText()>

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
            ${licence}
          </#list>
        <#else>
          ${modelProperties["defaultText"]}
        </#if>
      </#assign>
      <@fdsResultList.resultListDataValue
        key="Licences"
        value=licencesDisplay/>
    </@fdsResultList.resultListDataItem>

  </@fdsResultList.resultListItem>
</#macro>
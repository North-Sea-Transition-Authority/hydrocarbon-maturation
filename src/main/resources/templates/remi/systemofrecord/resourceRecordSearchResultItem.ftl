<#include '../layout/layout.ftl'>

<#macro prospectAndDiscoveryRecordSearchResultItem resultItem>
  <#assign modelProperties = resultItem.modelProperties().properties />
    <@fdsResultList.resultListItem
      captionHeadingText=resultItem.captionText()!""
      linkHeadingUrl=springUrl(resultItem.actionUrl())
      linkHeadingText=resultItem.headingText()>

        <@fdsResultList.resultListDataItem>
            <@fdsResultList.resultListDataValue
              key="Responsible team"
              value=modelProperties["teamResponsible"]/>

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

<#macro fieldRecordSearchResultItem resultItem>
    <#assign modelProperties = resultItem.modelProperties().properties />
    <@fdsResultList.resultListItem
    captionHeadingText=resultItem.captionText()!""
    linkHeadingUrl=springUrl(resultItem.actionUrl())
    linkHeadingText=resultItem.headingText()>

        <@fdsResultList.resultListDataItem>
            <@fdsResultList.resultListDataValue
            key="Responsible team"
            value=modelProperties["teamResponsible"]/>

            <@fdsResultList.resultListDataValue
            key="Responsible organisation"
            value=modelProperties["organisationResponsible"]/>

            <@fdsResultList.resultListDataValue
            key="Field status"
            value=modelProperties["fieldStatus"]/>

            <@fdsResultList.resultListDataValue
            key="Parent field"
            value=modelProperties["parentField"]/>
        </@fdsResultList.resultListDataItem>

    </@fdsResultList.resultListItem>
</#macro>
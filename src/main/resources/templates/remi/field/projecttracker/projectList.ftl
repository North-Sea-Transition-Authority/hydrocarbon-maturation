<#include '../../layout/layout.ftl'>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
caption=pageCaption
pageSize=PageSize.TWO_THIRDS_COLUMN
errorItems=errorList
breadcrumbsMap=breadcrumbsMap>
    <#if createProjectUrl?hasContent>
        <@fdsAction.link linkText="Create project" linkUrl=springUrl(createProjectUrl) linkClass="govuk-button"/>
    </#if>

    <@fdsLinkList.linkList>
        <#list projectViews as projectView>
            <@fdsLinkList.linkListItem
            linkName=projectView.projectName()
            hintText=projectView.projectCaption()
            linkUrl=projectView.linkUrl()!""/>
        </#list>
    </@fdsLinkList.linkList>
</@defaultPage>

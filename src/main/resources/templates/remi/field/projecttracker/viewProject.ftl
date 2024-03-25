<#include '../../layout/layout.ftl'>
<#import '../../summary/_projectSummary.ftl' as sectionsSummary>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
caption=pageCaption
pageSize=PageSize.TWO_THIRDS_COLUMN
errorItems=errorList
breadcrumbsMap=breadcrumbsMap>
     <#if deleteUrl?has_content>
         <@fdsAction.link linkText="Delete project" linkUrl="${springUrl(deleteUrl)}" linkClass="govuk-button govuk-button--secondary"/>
     </#if>

     <@sectionsSummary.projectSummary accordionId />
</@defaultPage>
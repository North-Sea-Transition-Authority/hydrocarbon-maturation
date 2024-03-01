<#include '../../layout/layout.ftl'>
<#import '../../summary/_submissionSummary.ftl' as sectionsSubmissionSummary>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
caption=pageCaption
pageSize=PageSize.TWO_THIRDS_COLUMN
errorItems=errorList
breadcrumbsMap=breadcrumbsMap>
     <@sectionsSubmissionSummary.submissionSummary accordionId />
</@defaultPage>
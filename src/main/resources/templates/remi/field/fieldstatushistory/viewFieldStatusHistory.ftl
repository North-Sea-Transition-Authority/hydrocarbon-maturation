<#include '../../layout/layout.ftl'>

<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
caption=pageCaption
pageSize=PageSize.TWO_THIRDS_COLUMN
errorItems=errorList
breadcrumbsMap=breadcrumbsMap>
    <@fdsTimeline.timeline>
        <@fdsTimeline.timelineSection sectionHeading="Field status history">
            <#list fieldStatusHistoryViews as fieldStatusHistory>
                <#assign timelineActions>
                    <#if canRemoveFieldStatus>
                        <@fdsAction.link linkText="Remove"
                        linkScreenReaderText=fieldStatusHistory.fieldStatus()
                        linkUrl=springUrl(fieldStatusHistory.removeUrl())
                        linkClass="govuk-link"/>
                    </#if>
                </#assign>
                <@fdsTimeline.timelineTimeStamp
                timeStampHeading=fieldStatusHistory.fieldStatus()
                timeStampHeadingHint=fieldStatusHistory.fieldStatusDate()
                nodeNumber=(fieldStatusHistory?index+1)?c
                timelineActionContent=timelineActions>
                </@fdsTimeline.timelineTimeStamp>
            </#list>
        </@fdsTimeline.timelineSection>
    </@fdsTimeline.timeline>

    <#if canAddFieldStatus>
        <@fdsAction.link linkText="Add field status" linkUrl=springUrl(addFieldStatusUrl) linkClass="govuk-button govuk-button--primary"/>
    </#if>
</@defaultPage>
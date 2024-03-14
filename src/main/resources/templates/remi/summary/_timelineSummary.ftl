<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="summaryHeading" type="String" -->
<#-- @ftlvariable name="summaryTimelineView" type="uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryTimelineView" -->

<#macro timelineSummary summaryTimelineView summaryHeading>
    <#assign content>
        <@fdsSummaryList.summaryListCardActionList>
            <#list summaryTimelineView.cardActionsMap() as actionText, actionUrl>
                <@fdsSummaryList.summaryListCardActionItem itemUrl=springUrl(actionUrl) itemText=actionText itemScreenReaderText=""/>
            </#list>
        </@fdsSummaryList.summaryListCardActionList>
    </#assign>

    <@fdsSummaryList.summaryListCard
    headingText=summaryHeading
    headingSize="h3"
    summaryListId="table-summary-card-list"
    cardActionsContent=content>
        <@fdsTimeline.timeline>
            <@fdsTimeline.timelineSection>
                <#list summaryTimelineView.timelineEvents() as event>
                    <#assign eventActions>
                        <#list event.eventActionsMap() as actionName, actionUrl>
                            <@fdsAction.link linkText=actionName
                            linkScreenReaderText=event.heading()
                            linkUrl=springUrl(actionUrl)
                            linkClass="govuk-link"/>
                        </#list>
                    </#assign>

                    <@fdsTimeline.timelineTimeStamp
                    timeStampHeading=event.heading()
                    timeStampHeadingHint=event.hint()
                    timelineActionContent=eventActions
                    timeStampClass=event?has_next?then("", "govuk-!-padding-bottom-0")
                    nodeNumber=(event?index+1)?c>
                    </@fdsTimeline.timelineTimeStamp>
                </#list>
            </@fdsTimeline.timelineSection>
        </@fdsTimeline.timeline>
    </@fdsSummaryList.summaryListCard>
</#macro>
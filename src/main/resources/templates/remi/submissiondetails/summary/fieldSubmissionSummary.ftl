<#include '../../layout/layout.ftl'>

<#import '_discoverySubmissionSummary.ftl' as discoverySubmissionSummary>
<#import '_summarySubmissionNotificationBanner.ftl' as banner>
<#import '../../review/updateSummaryDetails.ftl' as updateSummaryDetails>
<#import '../../summary/_simpleSummary.ftl' as simpleSummary>
<#import '../../summary/_submissionSummary.ftl' as submissionSummary>
<#import '../../summary/_reservoirSummary.ftl' as reservoirSummary>
<#import '../../summary/_regulatorInformationSummary.ftl' as regulatorSummary>

<#-- @ftlvariable name="pageName" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
<#-- @ftlvariable name="submittable" type="Boolean" -->
<#-- @ftlvariable name="isRegulatorOrAreaTeamUser" type="Boolean" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="isSubmissionUpdate" type="Boolean" -->
<#-- @ftlvariable name="isOffshore" type="Boolean" -->
<#-- @ftlvariable name="isRegulator" type="Boolean" -->
<#-- @ftlvariable name="submissionUpdateReason" type="String" -->
<#-- @ftlvariable name="resourceName" type="String" -->
<#-- @ftlvariable name="accordionId" type="String" -->
<#-- @ftlvariable name="reservoirSummaries" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ReservoirSummary>" -->

<#if !submittable>
    <#assign notificationBanner>
        <@banner.submissionSummaryNotificationBanner isRegulator=isRegulatorOrAreaTeamUser/>
    </#assign>
<#else>
    <#assign notificationBanner = ""/>
</#if>

<@defaultPage
htmlTitle=pageName
pageHeading=pageName
pageSize=PageSize.FULL_WIDTH
wrapperWidth=true
errorItems=errorList
breadcrumbsMap=breadcrumbsMap
notificationBanner=notificationBanner
caption=resourceName>
    <#if isSubmissionUpdate>
        <@updateSummaryDetails.updateDetails submissionUpdateReason=submissionUpdateReason/>
    </#if>
    <@fdsForm.htmlForm>
        <#if isRegulatorOrAreaTeamUser>
            <@regulatorSummary.regulatorInformationSummary
              fieldRegulatorInformationCard=fieldRegulatorManagedInformationSummaryCard
              fieldMapView=fieldMapView!""/>
        </#if>
        <@submissionSummary.submissionSummary accordionId>
            <@reservoirSummary.reservoirSummary reservoirSummaries=reservoirSummaries/>
        </@submissionSummary.submissionSummary>
        <#if !submittable && isRegulatorOrAreaTeamUser>
            <div class="govuk-form-group <#if errorList?has_content>govuk-form-group--error</#if>">
                <@fdsCheckbox.checkbox
                labelText="I want to complete this submission without providing all the required information"
                path="form.completeWithoutMandatoryInformation"/>
            </div>
        </#if>
        <#if isRegulatorOrAreaTeamUser || submittable>
            <@fdsAction.submitButtons
            primaryButtonText="Submit"
            secondaryLinkText="Back to task list"
            linkSecondaryAction=true
            linkSecondaryActionUrl="${springUrl(cancelUrl)}"/>
        <#else>
            <@fdsAction.link linkText="Back to task list" linkUrl="${springUrl(cancelUrl)}"/>
        </#if>
    </@fdsForm.htmlForm>
</@defaultPage>
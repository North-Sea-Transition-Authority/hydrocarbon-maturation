<#include '../../layout/layout.ftl'>
<#import '../../summary/_reservoirSummary.ftl' as reservoirSummary>

<#-- @ftlvariable name="pageName" type="String" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="reservoirName" type="String" -->
<#-- @ftlvariable name="canDeleteReservoir" type="boolean" -->
<#-- @ftlvariable name="deleteUrl" type="String" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
<#-- @ftlvariable name="submissionType" type="String" -->
<#-- @ftlvariable name="deleteButtonText" type="String" -->
<#-- @ftlvariable name="summary" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.ReservoirSummary" -->

<@defaultPage
    htmlTitle=pageName
    pageHeading=pageName
    pageSize=PageSize.FULL_WIDTH
    wrapperWidth=true
    breadcrumbsMap=breadcrumbsMap
    caption=reservoirName
>
    <#if !canDeleteReservoir>
        <@fdsNotificationBanner.notificationBannerInfo fullWidth=true bannerTitleText="Cannot delete reservoir">
            <@fdsNotificationBanner.notificationBannerContent>
                You can't delete this reservoir as it has been associated with the following fields:
                <ul class="govuk-list govuk-list--bullet">
                    <#list associatedFields as associatedField>
                        <li>${associatedField}</li>
                    </#list>
                </ul>
            </@fdsNotificationBanner.notificationBannerContent>
        </@fdsNotificationBanner.notificationBannerInfo>
    </#if>
    <@reservoirSummary.reservoirSummarySections reservoirSummary=summary/>

    <#if canDeleteReservoir>
        <@fdsForm.htmlForm actionUrl=springUrl(deleteUrl)>
            <@fdsAction.submitButtons
            primaryButtonText="Delete reservoir"
            secondaryLinkText="Cancel"
            linkSecondaryAction=true
            linkSecondaryActionUrl=springUrl(cancelUrl)
            primaryButtonClass="govuk-button govuk-button--warning"/>
        </@fdsForm.htmlForm>
    <#else>
        <@fdsAction.link linkText="Back to reservoir task list" linkUrl="${springUrl(cancelUrl)}"/>
    </#if>
</@defaultPage>
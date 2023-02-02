<#include '../../layout/layout.ftl'>

<#import '_dryWellSubmissionSummary.ftl' as dryWellSubmissionSummary>

<#assign pageTitle = "Check your answers before submitting" />
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
<#-- @ftlvariable name="submittable" type="Boolean" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="generalDetailsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView" -->
<#-- @ftlvariable name="dryWellDetailsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails.DryWellDetailsSummaryView" -->
<#-- @ftlvariable name="reservoirPropertiesSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView" -->
<#-- @ftlvariable name="permeabilitySummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.permeability.PermeabilityDetailSummaryView" -->
<#-- @ftlvariable name="opportunityDetailsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryView" -->
<#-- @ftlvariable name="sourceAndSealSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView" -->

<#if !submittable>
    <#assign notificationBanner>
        <@fdsNotificationBanner.notificationBannerInfo fullWidth=true bannerTitleText="Missing information">
            <@fdsNotificationBanner.notificationBannerContent headingText="Not all sections shown on the task list have been completed.">
              You will be allowed to submit without this information but this will mean the information will not be available in
              the approved view of the resource.
            </@fdsNotificationBanner.notificationBannerContent>
        </@fdsNotificationBanner.notificationBannerInfo>
    </#assign>
<#else>
    <#assign notificationBanner = ""/>
</#if>

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH
  wrapperWidth=true
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap
  notificationBanner=notificationBanner>

    <@fdsForm.htmlForm>
        <@dryWellSubmissionSummary.dryWellSubmissionSummary
          generalDetailsSummaryView=generalDetailsSummaryView
          dryWellDetailsSummaryView=dryWellDetailsSummaryView
          reservoirPropertiesSummaryView=reservoirPropertiesSummaryView
          permeabilitySummaryView=permeabilitySummaryView
          opportunityDetailsSummaryView=opportunityDetailsSummaryView
          sourceAndSealSummaryView=sourceAndSealSummaryView/>
        <#if !submittable>
            <@fdsCheckbox.checkbox
            labelText="I want to complete this submission without providing all the required information"
            path="form.completeWithoutMandatoryInformation"/>
        </#if>
      <@fdsAction.submitButtons
        primaryButtonText="Submit"
        secondaryLinkText="Cancel"
        linkSecondaryAction=true
        linkSecondaryActionUrl="${springUrl(cancelUrl)}"/>
    </@fdsForm.htmlForm>
</@defaultPage>
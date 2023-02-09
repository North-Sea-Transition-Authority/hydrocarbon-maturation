<#include '../../layout/layout.ftl'>

<#import '_prospectSubmissionSummary.ftl' as prospectSubmissionSummary>

<#assign pageTitle = "Check your answers before submitting" />
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="subNavigation" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubNavigationSectionItem>" -->
<#-- @ftlvariable name="submittable" type="Boolean" -->
<#-- @ftlvariable name="generalDetailsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryView" -->
<#-- @ftlvariable name="opportunityDetailsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryView" -->
<#-- @ftlvariable name="trapPropertiesSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryView" -->
<#-- @ftlvariable name="sourceAndSealSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryView" -->
<#-- @ftlvariable name="hydrocarbonCaseSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseSummaryView" -->
<#-- @ftlvariable name="chanceOfSuccessSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess.ChanceOfSuccessSummaryView" -->
<#-- @ftlvariable name="reservoirPropertiesSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView" -->
<#-- @ftlvariable name="oilCaseVolumetricsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView" -->
<#-- @ftlvariable name="oilCaseOutputsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilcaseoutputs.OilCaseOutputsSummaryView" -->
<#-- @ftlvariable name="gasCaseVolumetricsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView" -->
<#-- @ftlvariable name="gasCaseOutputsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gascaseoutputs.GasCaseOutputsSummaryView" -->
<#-- @ftlvariable name="mixedCaseVolumetricsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionsFormSummaryView" -->
<#-- @ftlvariable name="mixedCaseOutputsSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.mixedcaseoutputs.MixedCaseOutputsSummaryView" -->
<#-- @ftlvariable name="resourceDetailSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailSummaryView" -->
<#-- @ftlvariable name="gasCase" type="Boolean" -->
<#-- @ftlvariable name="oilCase" type="Boolean" -->
<#-- @ftlvariable name="mixedCase" type="Boolean" -->

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
        <@prospectSubmissionSummary.prospectSubmissionSummary
          generalDetailsSummaryView=generalDetailsSummaryView
          opportunityDetailsSummaryView=opportunityDetailsSummaryView
          trapPropertiesSummaryView=trapPropertiesSummaryView
          sourceAndSealSummaryView=sourceAndSealSummaryView
          hydrocarbonCaseSummaryView=hydrocarbonCaseSummaryView
          chanceOfSuccessSummaryView=chanceOfSuccessSummaryView
          reservoirPropertiesSummaryView=reservoirPropertiesSummaryView
          oilCase=oilCase
          oilCaseVolumetricsSummaryView=oilCaseVolumetricsSummaryView
          oilCaseOutputsSummaryView=oilCaseOutputsSummaryView
          gasCase=gasCase
          gasCaseVolumetricsSummaryView=gasCaseVolumetricsSummaryView
          gasCaseOutputsSummaryView=gasCaseOutputsSummaryView
          mixedCase=mixedCase
          mixedCaseVolumetricsSummaryView=mixedCaseVolumetricsSummaryView
          mixedCaseOutputsSummaryView=mixedCaseOutputsSummaryView
          resourceDetailSummaryView=resourceDetailSummaryView
          />
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
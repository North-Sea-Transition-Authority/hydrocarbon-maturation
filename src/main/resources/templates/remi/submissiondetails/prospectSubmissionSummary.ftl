<#include '../layout/layout.ftl'>

<#import '../summarylistcards/generaldetailsSummaryCard.ftl' as generalDetailsSummary>
<#import '../summarylistcards/sourceAndSealSummaryCard.ftl' as sourceAndSealSummary>
<#import '../summarylistcards/hydrocarbonCaseSummaryCard.ftl' as hydrocarbonCaseSummary>
<#import '../summarylistcards/reservoirPropertiesSummaryCard.ftl' as reservoirPropertiesSummary>
<#import '../summarylistcards/chanceOfSuccessSummaryCard.ftl' as chanceOfSuccessSummary>
<#import '../summarylistcards/trapPropertiesSummaryCard.ftl' as trapPropertiesSummary>
<#import '../summarylistcards/oilCaseVolumetricsSummaryCard.ftl' as oilCaseVolumetricsSummary>
<#import '../summarylistcards/oilCaseOutputsSummaryCard.ftl' as oilCaseOutputsSummary>
<#import '../summarylistcards/gasCaseVolumetricsSummaryCard.ftl' as gasCaseVolumetricsSummary>
<#import '../summarylistcards/gasCaseOutputsSummaryCard.ftl' as gasCaseOutputsSummary>
<#import '../summarylistcards/mixedCaseVolumetricsSummaryCard.ftl' as mixedCaseVolumetricsSummary>
<#import '../summarylistcards/mixedCaseOutputsSummaryCard.ftl' as mixedCaseOutputsSummary>
<#import '../summarylistcards/opportunityDetailsSummaryCard.ftl' as opportunityDetailsSummary>

<#assign pageTitle = "Check your answers before submitting" />
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="subNavigation" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubNavigationSectionItem>" -->
<#-- @ftlvariable name="reservoirPropertiesSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView" -->
<#-- @ftlvariable name="trapPropertiesSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.TrapPropertiesSummaryView" -->
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
        <@fdsAccordion.accordion accordionId="summaryaccordian">
            <@fdsAccordion.accordionSection sectionHeading="General details">
                <@generalDetailsSummary.generalDetailCard generalDetailValues=generalDetailsSummaryView/>
            </@fdsAccordion.accordionSection>
            <@fdsAccordion.accordionSection sectionHeading="Opportunity" summaryText="Opportunity details, trap properties, source and seal, hydrocarbon case, chance of success">
                <@opportunityDetailsSummary.opportunityDetailsSummaryCard opportunityDetailsValues=opportunityDetailsSummaryView/>
                <@trapPropertiesSummary.trapPropertiesSummaryCard trapPropertiesValues=trapPropertiesSummaryView/>
                <@sourceAndSealSummary.sourceAndSealCard sourceAndSealValues=sourceAndSealSummaryView/>
                <@hydrocarbonCaseSummary.hydrocarbonCaseCard hydrocarbonCaseValues=hydrocarbonCaseSummaryView/>
                <@chanceOfSuccessSummary.chanceOfSuccessCards chanceOfSuccessValues=chanceOfSuccessSummaryView/>
            </@fdsAccordion.accordionSection>
            <@fdsAccordion.accordionSection sectionHeading="Reservoir" summaryText="Reservoir properties">
                <@reservoirPropertiesSummary.reservoirPropertiesSummaryCard reservoirPropertiesValues=reservoirPropertiesSummaryView/>
            </@fdsAccordion.accordionSection>
            <@fdsAccordion.accordionSection sectionHeading="Volumetrics" summaryText="Oil case, gas case, and mixed case volumetric parameters and outputs">
                <#if oilCase>
                    <@oilCaseVolumetricsSummary.oilCaseVolumetricsSummary oilCaseVolumetricsValues=oilCaseVolumetricsSummaryView/>
                    <@oilCaseOutputsSummary.oilCaseOutputsSummaryCard oilCaseOutputsValues=oilCaseOutputsSummaryView/>
                </#if>
                <#if gasCase>
                    <@gasCaseVolumetricsSummary.gasCaseVolumetricsSummary gasCaseVolumetricsValues=gasCaseVolumetricsSummaryView/>
                    <@gasCaseOutputsSummary.gasCaseOutputsSummaryCard gasCaseOutputsValues=gasCaseOutputsSummaryView/>
                </#if>
                <#if mixedCase>
                    <@mixedCaseVolumetricsSummary.mixedCaseVolumetricsSummary mixedCaseVolumetricsValues=mixedCaseVolumetricsSummaryView/>
                    <@mixedCaseOutputsSummary.mixedCaseOutputsSummaryCard mixedCaseOutputsValues=mixedCaseOutputsSummaryView/>
                </#if>
            </@fdsAccordion.accordionSection>
        </@fdsAccordion.accordion>
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
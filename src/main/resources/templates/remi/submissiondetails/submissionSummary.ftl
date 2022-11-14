<#include '../layout/leftSubNavLayout.ftl'>

<#import '../summarylistcards/generaldetailsSummaryCard.ftl' as generalDetailsSummary>
<#import '../summarylistcards/sourceAndSealSummaryCard.ftl' as sourceAndSealSummary>
<#import '../summarylistcards/hydrocarbonCaseSummaryCard.ftl' as hydrocarbonCaseSummary>
<#import '../summarylistcards/reservoirPropertiesSummaryCard.ftl' as reservoirPropertiesSummary>
<#import '../summarylistcards/chanceOfSuccessSummaryCard.ftl' as chanceOfSuccessSummary>
<#import '../summarylistcards/trapPropertiesSummaryCard.ftl' as trapPropertiesSummary>
<#import '../summarylistcards/oilCaseVolumetricsSummary.ftl' as oilCaseVolumetricsSummary>
<#import '../summarylistcards/oilCaseOutputsSummaryCard.ftl' as oilCaseOutputsSummary>
<#import '../summarylistcards/gasCaseVolumetricsSummary.ftl' as gasCaseVolumetricsSummary>
<#import '../summarylistcards/gasCaseOutputsSummaryCard.ftl' as gasCaseOutputsSummary>
<#import '../summarylistcards/mixedCaseVolumetricsSummary.ftl' as mixedCaseVolumetricsSummary>
<#import '../summarylistcards/mixedCaseOutputsSummaryCard.ftl' as mixedCaseOutputsSummary>
<#import '../summarylistcards/additionalInformationSummaryCard.ftl' as additionalInformationSummary>
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

<@defaultLeftSubNavPage
  htmlTitle=pageTitle
  htmlAppTitle=pageTitle
  wrapperWidth=true>
  <@fdsLeftSubNavPageTemplateSubNav smallSubnav=true>
    <@fdsSubNavigation.subNavigation sticky=true>
      <@fdsSubNavigation.subNavigationSection themeHeading="Sections">
        <#list subNavigation as item>
          <@fdsSubNavigation.subNavigationSectionItem
            linkName="${item.displayName()}"
            linkAction="${item.actionUrl()}"
            currentItemHref="#"/>
        </#list>
      </@fdsSubNavigation.subNavigationSection>
    </@fdsSubNavigation.subNavigation>
  </@fdsLeftSubNavPageTemplateSubNav>

  <@defaultLeftSubNavPageContent pageHeading=pageTitle breadcrumbsMap=breadcrumbsMap>
    <@fdsForm.htmlForm>
      <@generalDetailsSummary.generalDetailCard
        generalDetailValues=generalDetailsSummaryView/>
      <@opportunityDetailsSummary.opportunityDetailsSummaryCard
        opportunityDetailsValues=opportunityDetailsSummaryView/>
      <@trapPropertiesSummary.trapPropertiesSummaryCard
        trapPropertiesValues=trapPropertiesSummaryView/>
      <@sourceAndSealSummary.sourceAndSealCard
        sourceAndSealValues=sourceAndSealSummaryView/>
      <@hydrocarbonCaseSummary.hydrocarbonCaseCard
        hydrocarbonCaseValues=hydrocarbonCaseSummaryView/>
      <@chanceOfSuccessSummary.chanceOfSuccessCards
        chanceOfSuccessValues=chanceOfSuccessSummaryView/>
      <@reservoirPropertiesSummary.reservoirPropertiesSummaryCard
        reservoirPropertiesValues=reservoirPropertiesSummaryView/>
      <#if oilCase>
        <@oilCaseVolumetricsSummary.oilCaseVolumetricsSummary
          oilCaseVolumetricsValues=oilCaseVolumetricsSummaryView/>
        <@oilCaseOutputsSummary.oilCaseOutputsSummaryCard
          oilCaseOutputsValues=oilCaseOutputsSummaryView/>
      </#if>
      <#if gasCase>
        <@gasCaseVolumetricsSummary.gasCaseVolumetricsSummary
          gasCaseVolumetricsValues=gasCaseVolumetricsSummaryView/>
        <@gasCaseOutputsSummary.gasCaseOutputsSummaryCard
          gasCaseOutputsValues=gasCaseOutputsSummaryView/>
      </#if>
      <#if mixedCase>
        <@mixedCaseVolumetricsSummary.mixedCaseVolumetricsSummary
          mixedCaseVolumetricsValues=mixedCaseVolumetricsSummaryView/>
        <@mixedCaseOutputsSummary.mixedCaseOutputsSummaryCard
          mixedCaseOutputsValues=mixedCaseOutputsSummaryView/>
      </#if>
      <@additionalInformationSummary.additionalInformationSummaryCard
          additionalInformationValues=additionalInformationSummaryView/>

      <@fdsAction.submitButtons
        primaryButtonText="Submit"
        secondaryLinkText="Cancel"
        linkSecondaryAction=true
        linkSecondaryActionUrl="${springUrl(cancelUrl)}"/>
    </@fdsForm.htmlForm>
  </@defaultLeftSubNavPageContent>
</@defaultLeftSubNavPage>
<#include '../layout/leftSubNavLayout.ftl'>

<#import '../summarylistcards/generaldetailsSummaryCard.ftl' as generalDetailsSummary>
<#import '../summarylistcards/sourceAndSealSummaryCard.ftl' as sourceAndSealSummary>
<#import '../summarylistcards/hydrocarbonCaseSummaryCard.ftl' as hydrocarbonCaseSummary>
<#import '../summarylistcards/reservoirPropertiesSummaryCard.ftl' as reservoirPropertiesSummary>
<#import '../summarylistcards/chanceOfSuccessSummaryCard.ftl' as chanceOfSuccessSummary>
<#import '../summarylistcards/gasCaseVolumetricsSummary.ftl' as gasCaseVolumetricsSummary>
<#import '../summarylistcards/gasCaseOutputsSummaryCard.ftl' as gasCaseOutputsSummary>

<#assign pageTitle = "Check your answers before submitting" />
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="subNavigation" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubNavigationSectionItem>" -->
<#-- @ftlvariable name="reservoirPropertiesSummaryView" type="uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties.ReservoirPropertiesSummaryView" -->
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
        <#list subNavigation as section>
          <#list section.getProspectTaskListItems() as item>
            <@fdsSubNavigation.subNavigationSectionItem
              linkName="${item.getItemDisplayText()}"
              linkAction="${item.getAction()}"
              currentItemHref="#"/>
          </#list>
        </#list>
      </@fdsSubNavigation.subNavigationSection>
    </@fdsSubNavigation.subNavigation>
  </@fdsLeftSubNavPageTemplateSubNav>

  <@defaultLeftSubNavPageContent pageHeading=pageTitle breadcrumbsMap=breadcrumbsMap>
    <@fdsForm.htmlForm>
      <@generalDetailsSummary.generalDetailCard
        generalDetailValues=generalDetailsSummaryView/>
      <@sourceAndSealSummary.sourceAndSealCard
        sourceAndSealValues=sourceAndSealSummaryView/>
      <@hydrocarbonCaseSummary.hydrocarbonCaseCard
        hydrocarbonCaseValues=hydrocarbonCaseSummaryView/>
      <@chanceOfSuccessSummary.chanceOfSuccessCards
        chanceOfSuccessValues=chanceOfSuccessSummaryView/>
      <@reservoirPropertiesSummary.reservoirPropertiesSummaryCard
        reservoirPropertiesValues=reservoirPropertiesSummaryView/>
      <#if gasCase>
        <@gasCaseVolumetricsSummary.gasCaseVolumetricsSummary
          gasCaseVolumetricsValues=gasCaseVolumetricsSummaryView/>
        <@gasCaseOutputsSummary.gasCaseOutputsSummaryCard
          gasCaseOutputsValues=gasCaseOutputsSummaryView/>
      </#if>

      <@fdsAction.submitButtons
        primaryButtonText="Submit"
        secondaryLinkText="Cancel"
        linkSecondaryAction=true
        linkSecondaryActionUrl="${springUrl(cancelUrl)}"/>
    </@fdsForm.htmlForm>
  </@defaultLeftSubNavPageContent>
</@defaultLeftSubNavPage>
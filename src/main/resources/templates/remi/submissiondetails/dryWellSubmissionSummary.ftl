<#include '../layout/leftSubNavLayout.ftl'>

<#import '../summarylistcards/dryWellSummaryCard.ftl' as dryWellSummary>

<#assign pageTitle = "Check your answers before submitting" />
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="subNavigation" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.SubNavigationPostDrillSectionItem>" -->
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
      <@dryWellSummary.dryWellDetailCard dryWellValues=dryWellDetailsSummaryView/>

      <@fdsAction.submitButtons
        primaryButtonText="Submit"
        secondaryLinkText="Cancel"
        linkSecondaryAction=true
        linkSecondaryActionUrl="${springUrl(cancelUrl)}"/>
    </@fdsForm.htmlForm>
  </@defaultLeftSubNavPageContent>
</@defaultLeftSubNavPage>
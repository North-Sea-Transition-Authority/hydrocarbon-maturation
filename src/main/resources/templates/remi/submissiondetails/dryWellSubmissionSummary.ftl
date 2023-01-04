<#include '../layout/layout.ftl'>

<#import '../summarylistcards/dryWellSummaryCard.ftl' as dryWellSummary>
<#import '../summarylistcards/permeabilitySummaryCard.ftl' as permeabilitySummary>
<#import '../summarylistcards/reservoirPropertiesSummaryCard.ftl' as reservoirPropertiesSummary>
<#import '../summarylistcards/generaldetailsSummaryCard.ftl' as generalDetailsSummary>

<#assign pageTitle = "Check your answers before submitting" />
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="gasCase" type="Boolean" -->
<#-- @ftlvariable name="oilCase" type="Boolean" -->
<#-- @ftlvariable name="mixedCase" type="Boolean" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH
  errorItems=errorList
  wrapperWidth=true
  breadcrumbsMap=breadcrumbsMap>

    <@fdsForm.htmlForm>
        <@fdsAccordion.accordion accordionId="summaryaccordian">
            <@fdsAccordion.accordionSection sectionHeading="General details">
                <@generalDetailsSummary.generalDetailCard generalDetailValues=generalDetailsSummaryView/>
            </@fdsAccordion.accordionSection>
            <@fdsAccordion.accordionSection sectionHeading="Post well drill" summaryText="Dry well details">
                <@dryWellSummary.dryWellDetailCard dryWellValues=dryWellDetailsSummaryView/>
            </@fdsAccordion.accordionSection>
            <@fdsAccordion.accordionSection sectionHeading="Reservoir details" summaryText="Reservoir properties, permeability">
                <@reservoirPropertiesSummary.reservoirPropertiesSummaryCard
                  reservoirPropertiesValues=reservoirPropertiesSummaryView />
                <@permeabilitySummary.permeabilityDetailsSummaryCard
                  permeabilityDetailsValues=permeabiltySummaryView
                  optionalPermeability=true/>
            </@fdsAccordion.accordionSection>
        </@fdsAccordion.accordion>
      <@fdsAction.submitButtons
        primaryButtonText="Submit"
        secondaryLinkText="Cancel"
        linkSecondaryAction=true
        linkSecondaryActionUrl="${springUrl(cancelUrl)}"/>
    </@fdsForm.htmlForm>
</@defaultPage>
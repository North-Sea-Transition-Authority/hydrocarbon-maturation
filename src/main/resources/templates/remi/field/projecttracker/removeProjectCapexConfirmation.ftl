<#include '../../layout/layout.ftl'>

<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="projectCapex" type="uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectcapex.ProjectCapexView" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
caption=pageCaption
pageSize=PageSize.TWO_THIRDS_COLUMN>
    <@fdsForm.htmlForm>
        <@fdsSummaryList.summaryListWrapper
        summaryListId="summary-list-one">
            <@fdsSummaryList.summaryList>
                <@fdsSummaryList.summaryListRowNoAction keyText="Date project CAPEX provided">
                    ${projectCapex.dateProvided()}
                </@fdsSummaryList.summaryListRowNoAction>
                <@fdsSummaryList.summaryListRowNoAction keyText="Total project CAPEX">
                    ${projectCapex.totalCapex()}
                </@fdsSummaryList.summaryListRowNoAction>
                <@fdsSummaryList.summaryListRowNoAction keyText="Source of information">
                    ${projectCapex.sourceOfInformation()}
                </@fdsSummaryList.summaryListRowNoAction>
            </@fdsSummaryList.summaryList>
        </@fdsSummaryList.summaryListWrapper>

        <@fdsAction.submitButtons
        primaryButtonText="Remove"
        secondaryLinkText="Cancel"
        linkSecondaryAction=true
        linkSecondaryActionUrl="${springUrl(cancelUrl)}"
        primaryButtonClass="govuk-button govuk-button--warning"/>
    </@fdsForm.htmlForm>
</@defaultPage>
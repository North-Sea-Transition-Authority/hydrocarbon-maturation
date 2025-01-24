<#include '../../../layout/layout.ftl'>

<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="projectStageHistory" type="uk.co.nstauthority.hydrocarbonmaturation.field.projecttracker.projectstage.ProjectStageView" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
caption=pageCaption
pageSize=PageSize.TWO_THIRDS_COLUMN>
    <@fdsForm.htmlForm>
        <@fdsSummaryList.summaryListWrapper
        summaryListId="summary-list-one">
            <@fdsSummaryList.summaryList>
                <@fdsSummaryList.summaryListRowNoAction keyText="Date">
                    ${projectDate}
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
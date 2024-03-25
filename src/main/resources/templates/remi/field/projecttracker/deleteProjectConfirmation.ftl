<#include '../../layout/layout.ftl'>
<#import '../../summary/_simpleSummary.ftl' as simpleSummary>

<#-- @ftlvariable name="backLinkUrl" type="String" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
caption=pageCaption
pageSize=PageSize.TWO_THIRDS_COLUMN>
    <@fdsForm.htmlForm>

        <@fdsSummaryList.summaryListWrapper
        summaryListId="summary-list-one">
            <@simpleSummary.simpleSummaryWithActions
            summaryDataViewWithActions=summaryCard.summaryData()
            summaryHeading=summaryCard.displayName()!""/>
        </@fdsSummaryList.summaryListWrapper>

        <@fdsAction.submitButtons
        primaryButtonText="Delete"
        secondaryLinkText="Cancel"
        linkSecondaryAction=true
        linkSecondaryActionUrl="${springUrl(cancelUrl)}"
        primaryButtonClass="govuk-button govuk-button--warning"/>

    </@fdsForm.htmlForm>
</@defaultPage>
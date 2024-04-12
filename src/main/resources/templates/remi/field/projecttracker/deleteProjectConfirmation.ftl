<#include '../../layout/layout.ftl'>
<#import '../../summary/_simpleSummary.ftl' as simpleSummary>

<#-- @ftlvariable name="backLinkUrl" type="String" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
caption=pageCaption
pageSize=PageSize.TWO_THIRDS_COLUMN>
    <@fdsForm.htmlForm>

        <@simpleSummary.simpleSummaryCard
        summaryDataView=summaryCard.summaryData()
        summaryHeading=summaryCard.displayName()!""/>

        <@fdsAction.submitButtons
        primaryButtonText="Delete"
        secondaryLinkText="Cancel"
        linkSecondaryAction=true
        linkSecondaryActionUrl="${springUrl(cancelUrl)}"
        primaryButtonClass="govuk-button govuk-button--warning"/>

    </@fdsForm.htmlForm>
</@defaultPage>
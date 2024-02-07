<#include '../../layout/layout.ftl'>

<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN>
    <@fdsForm.htmlForm>
        <@fdsSummaryList.summaryListWrapper
        
        summaryListId="summary-list-one">
            <@fdsSummaryList.summaryList>
                <@fdsSummaryList.summaryListRowNoAction keyText="Field status">
                    ${fieldStatusHistory.fieldStatus()}
                </@fdsSummaryList.summaryListRowNoAction>
                <@fdsSummaryList.summaryListRowNoAction keyText="Date field entered status">
                    ${fieldStatusHistory.fieldStatusDate()}
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
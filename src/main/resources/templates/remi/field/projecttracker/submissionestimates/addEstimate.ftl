<#include '../../../layout/layout.ftl'>
<#import '../../../systemofrecord/submissionHistorySearchSelector.ftl' as submissionSelector>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
caption=pageCaption
pageSize=PageSize.TWO_THIRDS_COLUMN
errorItems=errorList>
    <@fdsForm.htmlForm>
        <@fdsDateInput.dateInput
        formId="earliestDate-date-input"
        dayPath="form.earliestDateDay"
        monthPath="form.earliestDateMonth"
        yearPath="form.earliestDateYear"
        labelText="Earliest submission date"
        optionalLabel=true/>

        <@fdsDateInput.dateInput
        formId="mostLikelyDate-date-input"
        dayPath="form.mostLikelyDateDay"
        monthPath="form.mostLikelyDateMonth"
        yearPath="form.mostLikelyDateYear"
        labelText="Most likely submission"/>

        <@fdsDateInput.dateInput
        formId="latestDate-date-input"
        dayPath="form.latestDateDay"
        monthPath="form.latestDateMonth"
        yearPath="form.latestDateYear"
        labelText="Latest submission date"
        optionalLabel=true/>

        <@fdsDateInput.dateInput
        formId="estimatedDate-date-input"
        dayPath="form.estimatedDateDay"
        monthPath="form.estimatedDateMonth"
        yearPath="form.estimatedDateYear"
        labelText="On what date were these the estimated submission dates?"/>

        <@fdsAction.submitButtons
        primaryButtonText="Save"
        secondaryLinkText="Cancel"
        linkSecondaryAction=true
        linkSecondaryActionUrl=springUrl(cancelUrl)/>
    </@fdsForm.htmlForm>
</@defaultPage>
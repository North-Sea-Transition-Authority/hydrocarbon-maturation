<#include '../../layout/layout.ftl'>
<#import '../../systemofrecord/submissionHistorySearchSelector.ftl' as submissionSelector>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
caption=pageCaption
pageSize=PageSize.TWO_THIRDS_COLUMN
errorItems=errorList>
    <@fdsForm.htmlForm>
        <@fdsDateInput.dateInput
        formId="submissionDate-date-input"
        dayPath="form.submissionDateDay"
        monthPath="form.submissionDateMonth"
        yearPath="form.submissionDateYear"
        labelText="Environment statement submission date"
        optionalLabel=true/>

        <@fdsDateInput.dateInput
        formId="consultationStartDate-date-input"
        dayPath="form.consultationStartDateDay"
        monthPath="form.consultationStartDateMonth"
        yearPath="form.consultationStartDateYear"
        labelText="Environmental statement consultation start date"
        optionalLabel=true/>

        <@fdsDateInput.dateInput
        formId="consultationClosingDate-date-input"
        dayPath="form.consultationClosingDateDay"
        monthPath="form.consultationClosingDateMonth"
        yearPath="form.consultationClosingDateYear"
        labelText="Environmental statement consultation closing date"
        optionalLabel=true/>

        <@fdsAction.submitButtons
        primaryButtonText="Save"
        secondaryLinkText="Cancel"
        linkSecondaryAction=true
        linkSecondaryActionUrl=springUrl(cancelUrl)/>
    </@fdsForm.htmlForm>
</@defaultPage>
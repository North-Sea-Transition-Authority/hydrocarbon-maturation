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
        formId="projectDate-date-input"
        dayPath="form.dateDay"
        monthPath="form.dateMonth"
        yearPath="form.dateYear"
        labelText="On what date did this event occur?"/>

        <@fdsAction.submitButtons
        primaryButtonText="Save"
        secondaryLinkText="Cancel"
        linkSecondaryAction=true
        linkSecondaryActionUrl=springUrl(cancelUrl)/>
    </@fdsForm.htmlForm>
</@defaultPage>
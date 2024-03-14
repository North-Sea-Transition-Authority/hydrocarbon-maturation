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
        <@fdsRadio.radio
        path="form.stage"
        labelText="Project stage"
        radioItems=projectStageOptions/>

        <@fdsDateInput.dateInput
        formId="stageDate-date-input"
        dayPath="form.stageDateDay"
        monthPath="form.stageDateMonth"
        yearPath="form.stageDateYear"
        labelText="Date project entered stage"/>

        <@fdsAction.submitButtons
        primaryButtonText="Save project stage"
        secondaryLinkText="Cancel"
        linkSecondaryAction=true
        linkSecondaryActionUrl="${springUrl(cancelUrl)}"/>
    </@fdsForm.htmlForm>
</@defaultPage>
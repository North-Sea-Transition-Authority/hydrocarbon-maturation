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
        <@fdsTextInput.textInput
        path="form.projectName"
        labelText="Project name"
        inputClass="govuk-input--width-20"/>

        <#if !isUpdate>
            <@fdsRadio.radio
            path="form.projectType"
            labelText="Project type"
            radioItems=projectTypeOptions/>
        </#if>

        <@fdsRadio.radio
        path="form.projectStatus"
        labelText="Current project status"
        radioItems=projectStatusOptions/>

        <@fdsRadio.radioGroup
        path="form.isPartOfPipeline"
        labelText="Is this project part of the project pipeline?">
            <@fdsRadio.radioYes path="form.isPartOfPipeline"/>
            <@fdsRadio.radioNo path="form.isPartOfPipeline"/>
        </@fdsRadio.radioGroup>

        <@fdsSelect.select
        path="form.selectedSubmissionId"
        labelText="Which submission is associated with this project?"
        optionalLabel=true
        options=acceptedSubmissionOptions!{}/>

        <#if isUpdate && ((projectType!"") == "FIELD_DEVELOPMENT_PLAN_ADDENDUM")>
            <@fdsDateInput.dateInput
            formId="fdpaStartUpDate-date-input"
            dayPath="form.fdpaStartUpDateDay"
            monthPath="form.fdpaStartUpDateMonth"
            yearPath="form.fdpaStartUpDateYear"
            labelText="FDPA start up date"/>
        </#if>

        <@fdsAction.submitButtons
        primaryButtonText="Save and complete"
        secondaryLinkText="Cancel"
        linkSecondaryAction=true
        linkSecondaryActionUrl="${springUrl(cancelUrl)}"/>
    </@fdsForm.htmlForm>
</@defaultPage>
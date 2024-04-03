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
        formId="dateProvided-date-input"
        dayPath="form.dateProvidedDay"
        monthPath="form.dateProvidedMonth"
        yearPath="form.dateProvidedYear"
        labelText="Date CAPEX was provided"
        defaultHint=true/>

        <@fdsTextInput.textInput
        path="form.totalCapex.decimalInputValue"
        labelText="Total project CAPEX"
        prefix="£"
        suffix="million"
        inputClass="govuk-input--width-5"/>

        <@fdsRadio.radio
        path="form.sourceOfInformation"
        labelText="Source of information"
        radioItems=projectCapexSourceOptions/>

        <@fdsAction.submitButtons
        primaryButtonText="Save and complete"
        secondaryLinkText="Cancel"
        linkSecondaryAction=true
        linkSecondaryActionUrl="${springUrl(cancelUrl)}"/>
    </@fdsForm.htmlForm>
</@defaultPage>
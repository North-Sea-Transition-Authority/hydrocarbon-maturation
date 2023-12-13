<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
errorItems=errorList
breadcrumbsMap=breadcrumbsMap>
    <#if submissionType == "CREATE_FDPA">
        <@fdsInsetText.insetText>
            Only provide information on the planned unsanctioned wells related to this FDPA
        </@fdsInsetText.insetText>
    </#if>
    <@fdsForm.htmlForm>
        <@fdsTextInput.textInput
        path="form.totalCapex.decimalInputValue"
        labelText="Total CAPEX for all planned unsanctioned wells"
        inputClass="govuk-input--width-3"
        prefix="£"
        suffix="million"
        />

        <@fdsTextInput.textInput
        path="form.totalNumberOfPlannedUnsanctionedWells.decimalInputValue"
        labelText="Total number of planned unsanctioned wells"
        inputClass="govuk-input--width-3"/>

        <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
    </@fdsForm.htmlForm>
</@defaultPage>
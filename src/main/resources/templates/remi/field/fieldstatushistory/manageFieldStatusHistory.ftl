<#include '../../layout/layout.ftl'>

<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
caption=pageCaption
pageSize=PageSize.TWO_THIRDS_COLUMN
errorItems=errorList
breadcrumbsMap=breadcrumbsMap>
    <@fdsForm.htmlForm>
        <@fdsRadio.radio
        path="form.fieldStatus"
        labelText="Field status"
        radioItems=fieldStatusOptions/>

        <@fdsDateInput.dateInput
        formId="fieldStatusDate-date-input"
        dayPath="form.fieldStatusDateDay"
        monthPath="form.fieldStatusDateMonth"
        yearPath="form.fieldStatusDateYear"
        labelText="Date field entered status"/>

        <@fdsAction.submitButtons
        primaryButtonText="Save field status"
        secondaryLinkText="Cancel"
        linkSecondaryAction=true
        linkSecondaryActionUrl="${springUrl(cancelUrl)}"/>
    </@fdsForm.htmlForm>
</@defaultPage>
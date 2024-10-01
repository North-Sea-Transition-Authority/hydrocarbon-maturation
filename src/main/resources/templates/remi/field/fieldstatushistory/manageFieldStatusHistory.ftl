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
        <@fdsRadio.radioGroup
        path="form.fieldStatus"
        labelText="Field status"
        hiddenContent=true>
            <#assign firstItem=true/>
                <#list fieldStatusOptions as key, value>
                    <@fdsRadio.radioItem path="form.fieldStatus" itemMap={key : value} isFirstItem=firstItem>
                        <#if key == "STATUS_000" || key == "STATUS_900">
                            <@fdsWarning.warning>
                                Check with the relevant area team if a new discovery is required to track the remaining field volumetrics.
                            </@fdsWarning.warning>
                        </#if>
                        <#assign firstItem = false/>
                    </@fdsRadio.radioItem>
                </#list>

        </@fdsRadio.radioGroup>

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
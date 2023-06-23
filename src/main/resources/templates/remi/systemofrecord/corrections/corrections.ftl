<#include '../../layout/layout.ftl'>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="submissionsOnResource" type="java.util.Map<String, String>"-->

<@defaultPage
htmlTitle="What do you want to correct?"
errorItems=errorList
backLinkEnabled=true>

    <@fdsForm.htmlForm>
        <@fdsRadio.radioGroup path="form.correctionType" labelText="What do you want to correct?" fieldsetHeadingSize="h1" fieldsetHeadingClass="govuk-fieldset__legend--xl"  hiddenContent=true>
            <#assign firstItem=true/>
            <#list correctionTypes as key, value>
                <@fdsRadio.radioItem path="form.correctionType" itemMap={key : value} isFirstItem=firstItem>
                    <#if key == "SUBMISSION_CORRECTION">
                        <@fdsSelect.select
                        path = "form.submissionIdForCorrectingInformation"
                        labelText = "On which submission do you want to correct the information provided?"
                        options = submissionsOnResource
                        nestingPath="form.correctionType"/>
                    </#if>
                    <#assign firstItem=false/>
                </@fdsRadio.radioItem>
            </#list>
        </@fdsRadio.radioGroup>
        <@fdsAction.submitButtons primaryButtonText="Start correction" secondaryLinkText="Cancel" linkSecondaryAction=true linkSecondaryActionUrl="${springUrl(cancelUrl)}"/>
    </@fdsForm.htmlForm>
</@defaultPage>
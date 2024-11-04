<#include '../../layout/layout.ftl'>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="submissionsOnResource" type="java.util.Map<String, String>"-->
<#-- @ftlvariable name="removableSubmissions" type="java.util.Map<String, String>"-->

<@defaultPage
htmlTitle="What do you want to correct?"
errorItems=errorList
backLinkUrl=springUrl(cancelUrl)>

    <@fdsForm.htmlForm>
        <@fdsRadio.radioGroup path="form.correctionType" labelText="What do you want to correct?" fieldsetHeadingSize="h1" fieldsetHeadingClass="govuk-fieldset__legend--xl"  hiddenContent=true>
            <#assign firstItem=true/>
            <#list correctionTypes as key, value>
                <@fdsRadio.radioItem path="form.correctionType" itemMap={key : value} isFirstItem=firstItem>
                    <#if key == "HISTORIC_UPDATE">
                        <@fdsSelect.select
                        path = "form.submissionDetailIdForMissingUpdate"
                        labelText = "After which submission do you want to add a missing update?"
                        options = submissionsOnResource
                        nestingPath="form.correctionType"/>

                        <@fdsDateInput.dateInput
                            formId="missing-update-date-input"
                            dayPath="form.missingUpdateDay"
                            monthPath="form.missingUpdateMonth"
                            yearPath="form.missingUpdateYear"
                            labelText="Date of missing update"
                            hintText="For example, 31 1 2023"
                            nestingPath="form.correctionType"/>
                    </#if>
                    <#if key == "FIELD_HISTORIC_UPDATE" && submissionTypes?has_content>
                        <@fdsRadio.radioGroup
                        path="form.submissionTypeForMissingUpdate"
                        labelText="Which missing submission type do you want to add?">
                            <#assign firstItem=true/>
                            <#list submissionTypes as submissionType>
                                <@fdsRadio.radioItem
                                path="form.submissionTypeForMissingUpdate"
                                itemMap={submissionType.enumName: submissionType.displayName}
                                itemHintText=submissionType.hintText isFirstItem=firstItem/>
                                <#assign firstItem=false/>
                            </#list>
                        </@fdsRadio.radioGroup>

                        <@fdsDateInput.dateInput
                        formId="missing-update-date-input"
                        dayPath="form.missingUpdateDay"
                        monthPath="form.missingUpdateMonth"
                        yearPath="form.missingUpdateYear"
                        labelText="Date of missing update"
                        hintText="For example, 31 1 2023"
                        nestingPath="form.correctionType"/>
                    </#if>
                    <#if key == "SUBMISSION_CORRECTION">
                        <@fdsSelect.select
                            path = "form.submissionDetailIdForCorrectingInformation"
                            labelText = "On which submission do you want to correct the information provided?"
                            options = submissionsOnResource
                            nestingPath="form.correctionType"/>
                    </#if>
                    <#if key == "ACCEPTED_DATE_CHANGE">
                        <@fdsSelect.select
                            path = "form.submissionDetailIdForChangingApprovedDate"
                            labelText = "On which submission do you want to change the accepted date?"
                            options = submissionsOnResource
                            nestingPath="form.correctionType"/>
                        <@fdsDateInput.dateInput
                            formId="approval-date-change-input"
                            dayPath="form.approvalDateChangeDay"
                            monthPath="form.approvalDateChangeMonth"
                            yearPath="form.approvalDateChangeYear"
                            labelText="What is the new accepted date of the submission?"
                            hintText="For example, 31 1 2023"
                            nestingPath="form.correctionType"/>
                    </#if>
                    <#if key == "REMOVE_SUBMISSION">
                        <@fdsSelect.select
                        path = "form.submissionDetailIdForRemoval"
                        labelText = "Which submission do you want to remove?"
                        options = removableSubmissions
                        nestingPath="form.correctionType"/>
                    </#if>
                    <#assign firstItem=false/>
                </@fdsRadio.radioItem>
            </#list>
        </@fdsRadio.radioGroup>
        <@fdsAction.submitButtons primaryButtonText="Start correction" secondaryLinkText="Cancel" linkSecondaryAction=true linkSecondaryActionUrl="${springUrl(cancelUrl)}"/>
    </@fdsForm.htmlForm>
</@defaultPage>
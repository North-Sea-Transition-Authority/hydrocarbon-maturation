<#include '../layout/layout.ftl'>

<#assign pageTitle = "What is the purpose of this submission?" />
<#-- @ftlvariable name="submissionTypes" type="java.util.List<SubmissionType>" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=""
pageSize=PageSize.TWO_THIRDS_COLUMN
errorItems=errorList
backLinkUrl=springUrl(backLinkUrl)>

  <@fdsForm.htmlForm
    actionUrl=springUrl(actionUrl)>

      <@fdsRadio.radioGroup path="form.submissionType" labelText=pageTitle
      fieldsetHeadingSize="h1" fieldsetHeadingClass="govuk-fieldset__legend--xl">
          <#assign firstItem=true/>
          <#list submissionTypes as submissionType>
            <@fdsRadio.radioItem path="form.submissionType" itemMap={submissionType.enumName: submissionType.displayName}
            itemHintText=submissionType.hintText isFirstItem=firstItem/>
            <#assign firstItem=false/>
          </#list>
      </@fdsRadio.radioGroup>

      <@fdsAction.submitButtons primaryButtonText="Continue" secondaryLinkText="Cancel" linkSecondaryAction=true linkSecondaryActionUrl="${springUrl(backLinkUrl)}"/>
  </@fdsForm.htmlForm>

</@defaultPage>
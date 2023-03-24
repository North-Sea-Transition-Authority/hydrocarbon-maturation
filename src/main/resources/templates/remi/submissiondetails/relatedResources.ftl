<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="pageTitle" type="String" -->
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="resourceGroupsEndpoint" type="String" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>

    <@fdsForm.htmlForm>

      <@fdsRadio.radioGroup
        path="form.relatedToAnotherResource"
        fieldsetHeadingClass="govuk-fieldset__legend--m"
        labelText="Is this ${resourceType} a different view of another ${resourceType} in the service?"
        hintText="Different organisations may provide their own views of a ${resourceType}. Each is captured in the service and associated with each other so that it is clear they are different views of the same area."
        hiddenContent=true>
          <@fdsRadio.radioYes path="form.relatedToAnotherResource">
              <@fdsSearchSelector.searchSelectorRest
                path="form.submissionDetailIdOfRelatedResource"
                restUrl=springUrl(resourceGroupsEndpoint)
                labelText="What ${resourceType} or ${resourceType} group is this the same as?"
                selectorMinInputLength=3
                nestingPath="form.submissionDetailIdOfRelatedResource"
                preselectedItems=relatedSubmissionDetail!{}/>
                <@fdsRadio.radioGroup
                  path="form.preferredView"
                  labelText="Is this the NSTA preferred view of the ${resourceType}?"
                  hiddenContent=true>
                    <@fdsRadio.radioYes path="form.preferredView"/>
                    <@fdsRadio.radioNo path="form.preferredView"/>
                </@fdsRadio.radioGroup>
          </@fdsRadio.radioYes>
          <@fdsRadio.radioNo path="form.relatedToAnotherResource"/>
      </@fdsRadio.radioGroup>

      <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>

    </@fdsForm.htmlForm>
</@defaultPage>
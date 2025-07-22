<#include '../layout/layout.ftl'>

<#macro relatedResources resourceType resourceGroupsEndpoint relatedSubmissionDetail>
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
    </@fdsRadio.radioYes>
    <@fdsRadio.radioNo path="form.relatedToAnotherResource"/>
  </@fdsRadio.radioGroup>

</#macro>
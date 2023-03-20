<#include '../layout/layout.ftl'>

<#import '../layout/inlineInputAction.ftl' as inlineInputAction>

<#macro submissionHistorySearchSelector resourceType submissionsOnResource>
  <@inlineInputAction.inlineInputAction>
    <@fdsSelect.select
      path = "form.selectedSubmissionId"
      labelText = "${resourceType} submissions"
      options = submissionsOnResource/>
    <@fdsAction.button
      buttonText="View"
      buttonClass="govuk-button govuk-button--secondary"/>
  </@inlineInputAction.inlineInputAction>
</#macro>
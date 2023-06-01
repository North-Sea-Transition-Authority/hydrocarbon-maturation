<#include '../layout/layout.ftl'>

<#macro updateDetails submissionUpdateReason>
  <@fdsDetails.summaryDetails
    summaryTitle="What information requires an update?">
    <p class="govuk-body govuk-body__preserve-whitespace">${submissionUpdateReason}</p>
  </@fdsDetails.summaryDetails>
</#macro>
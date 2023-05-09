<#include '../layout/layout.ftl'>

<#macro updateDetails submissionUpdateReason>
  <@fdsDetails.summaryDetails
    summaryTitle="What information have I been asked to update?">
    <p class="govuk-body govuk-body__preserve-whitespace">${submissionUpdateReason}</p>
  </@fdsDetails.summaryDetails>
</#macro>
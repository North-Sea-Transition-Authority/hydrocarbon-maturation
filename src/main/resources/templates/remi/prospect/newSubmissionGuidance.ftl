<#include '../layout/layout.ftl'>
<#-- @ftlvariable name="customerBranding" type="uk.co.nstauthority.hydrocarbonmaturation.branding.CustomerConfigurationProperties" -->

<#macro newSubmissionGuidanceText resourceTypeString actionString="update">
  <p class="govuk-body">
    If the ${resourceTypeString} you want to ${actionString} is not shown in the list then
  </p>
  <ol type="1">
    <li>
      check with the operator responsible for the ${resourceTypeString} that you have permissions to submit updates on their behalf
    </li>
    <li>
      check if there is an open submission for the ${resourceTypeString} in draft or awaiting acceptance as only one open submission is allowed at a time
    </li>
    <#nested>
  </ol>
  <p class="govuk-body">
    If you have confirmed you have access to submit updates for the ${resourceTypeString} and there are no open submissions then contact the ${customerBranding.mnemonic()} for advice.
  </p>
</#macro>
<#include '../layout/layout.ftl'>

<#macro newFieldGuidanceText statusString actionString="update">
  <p class="govuk-body">
    If the field you want to ${actionString} is not shown in the list then
  </p>
  <ol type="1">
    <li>
      check with the operator responsible for the field that you have permissions to submit updates on their behalf
    </li>
    <li>
      check if there is an open submission for the field in draft or awaiting acceptance as only one open submission is allowed at a time
    </li>
    <li>
      check the status of the field is ${statusString}
    </li>
  </ol>
  <p class="govuk-body">
    If you have checked the above then contact the NSTA for advice.
  </p>
</#macro>
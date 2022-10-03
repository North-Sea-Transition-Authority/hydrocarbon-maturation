<#include '../layout/layout.ftl'>

<#assign pageTitle = "Submission complete" />
<#-- @ftlvariable name="workAreaUrl" type="String" -->
<#-- @ftlvariable name="assetName" type="String" -->
<#-- @ftlvariable name="typeOfUpdate" type="String" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=""
pageSize=PageSize.TWO_THIRDS_COLUMN>

  <@fdsPanel.panel
    panelTitle=pageTitle
    panelText="${assetName} - ${typeOfUpdate}"/>


  <h2 class="govuk-heading-m">What happens next</h2>

  <p class="govuk-body">
    The submission has been automatically approved. The information provided is now available in the system of record.
  </p>

  <p class="govuk-body">
    <a href="#" class="govuk-link">What did you think of this service?</a> (takes 30 seconds)
  </p>

  <p class="govuk-body">
    <a href="${springUrl(workAreaUrl)}" class="govuk-link">Back to work area</a>
  </p>

</@defaultPage>
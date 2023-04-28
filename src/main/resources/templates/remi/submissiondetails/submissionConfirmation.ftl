<#include '../layout/layout.ftl'>

<#assign pageTitle = "Submission complete" />
<#-- @ftlvariable name="workAreaUrl" type="String" -->
<#-- @ftlvariable name="assetName" type="String" -->
<#-- @ftlvariable name="typeOfUpdate" type="String" -->
<#-- @ftlvariable name="customerBranding" type="uk.co.nstauthority.hydrocarbonmaturation.branding.CustomerConfigurationProperties" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=""
pageSize=PageSize.TWO_THIRDS_COLUMN>

  <@fdsPanel.panel
    panelTitle=pageTitle
    panelText="${assetName} - ${typeOfUpdate}"/>

  <h2 class="govuk-heading-m">What happens next</h2>

  <#if hasBeenAutoApproved>
    <p class="govuk-body">
      The submission has been automatically approved. The information provided is now available in the system of record.
    </p>
  <#else>
    <p class="govuk-body">
      We've sent your submission to the ${customerBranding.name()} (${customerBranding.mnemonic()}).
    </p>
    <p class="govuk-body">
      They will contact you either to confirm if approved, or to ask for more information.
    </p>
  </#if>

  <p class="govuk-body">
    <@fdsAction.link
      linkClass="govuk-link"
      linkText="What did you think of this service?"
      linkUrl=springUrl(feedbackUrl)/>
    (takes 30 seconds)
  </p>

  <p class="govuk-body">
    <a href="${springUrl(workAreaUrl)}" class="govuk-link">Back to work area</a>
  </p>

</@defaultPage>
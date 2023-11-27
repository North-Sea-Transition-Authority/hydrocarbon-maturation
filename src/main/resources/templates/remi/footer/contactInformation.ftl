<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="businessSupport" type="uk.co.nstauthority.hydrocarbonmaturation.businesssupport.BusinessSupportConfigurationProperties" -->
<#-- @ftlvariable name="technicalSupport" type="uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.TechnicalSupportConfigurationProperties" -->

<@defaultPage
  htmlTitle="Contact"
  pageHeading="Contact"
  pageSize=PageSize.TWO_THIRDS_COLUMN
  backLinkEnabled=true
  topNavigation=false>
  <div class="govuk-body">

    <div>
      <h2 class="govuk-heading-m">Business support</h2>
      <p>For example, questions about filling in your submission,
        the information you need to provide or to provide feedback on the service.</p>
      <ul class="govuk-list">
        <li>${businessSupport.name()}</li>
        <li>Email: <@fdsAction.link linkText=businessSupport.emailAddress()linkUrl="mailto:${businessSupport.emailAddress()}"/></li>
      </ul>
    </div>

    <div>
      <h2 class="govuk-heading-m">Technical support</h2>
      <p>For example, unexpected problems using the service or system errors being received.</p>
      <ul class="govuk-list">
        <li>${technicalSupport.name()}</li>
        <li>Telephone: ${technicalSupport.phoneNumber()}</li>
        <li>Email: <@fdsAction.link linkText=technicalSupport.emailAddress()linkUrl="mailto:${technicalSupport.emailAddress()}"/></li>
      </ul>
    </div>

  </div>
</@defaultPage>
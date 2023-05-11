<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="technicalSupport" type="uk.co.nstauthority.hydrocarbonmaturation.technicalsupport.TechnicalSupportConfigurationProperties" -->

<@defaultPage
  htmlTitle="Contact"
  pageHeading="Contact"
  pageSize=PageSize.TWO_THIRDS_COLUMN
  backLinkEnabled=true
  topNavigation=false>
  <div class="govuk-body">
    <h2 class="govuk-heading-m">${technicalSupport.name()}</h2>
    <p>For example, unexpected problems using the service or system errors being received</p>
    <ul class="govuk-list">
      <li>${technicalSupport.name()}</li>
      <li>Telephone: ${technicalSupport.phoneNumber()}</li>
      <li>Email: <@fdsAction.link linkText=technicalSupport.emailAddress()linkUrl="mailto:${technicalSupport.emailAddress()}"/></li>
    </ul>

  </div>
</@defaultPage>
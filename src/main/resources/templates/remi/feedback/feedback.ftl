<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="serviceBranding" type="uk.co.nstauthority.hydrocarbonmaturation.branding.ServiceConfigurationProperties" -->
<#-- @ftlvariable name="serviceRatings" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
<#-- @ftlvariable name="showBackLink" type="String" -->

<#assign pageTitle = "Give feedback on ${serviceBranding.mnemonic()}"/>

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  phaseBanner=false
  backLinkEnabled=true
  errorItems=errorList>
  <@fdsForm.htmlForm actionUrl=springUrl(actionUrl)>
    <@fdsRadio.radio
      path="form.serviceRating"
      labelText="Overall, how did you feel about using this service?"
      radioItems=serviceRatings/>
    <@fdsTextarea.textarea
      path="form.feedback"
      labelText="How could we improve this service?"
      hintText="Do not include any personal or financial information, for example your National Insurance or credit card numbers"
      optionalLabel=true
      maxCharacterLength="2000"
      characterCount=true
      rows="10"/>

    <@fdsAction.submitButtons
      primaryButtonText="Send feedback"
      linkSecondaryAction=true
      secondaryLinkText="Cancel"
      linkSecondaryActionUrl=springUrl(cancelUrl)/>
  </@fdsForm.htmlForm>
</@defaultPage>
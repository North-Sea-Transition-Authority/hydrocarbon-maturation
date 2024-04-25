<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList>
  <@fdsForm.htmlForm>
    <@fdsRadio.radioGroup path="form.isUndevelopedDiscovery"
      labelText="Should this discovery be included in the undeveloped discoveries tracker?">
      <@fdsRadio.radioYes path="form.isUndevelopedDiscovery"/>
      <@fdsRadio.radioNo path="form.isUndevelopedDiscovery"/>
    </@fdsRadio.radioGroup>

    <@fdsAction.submitButtons
      primaryButtonText="Save and complete"
      secondaryLinkText="Cancel"
      linkSecondaryAction=true
      linkSecondaryActionUrl="${springUrl(cancelUrl)}"/>
  </@fdsForm.htmlForm>
</@defaultPage>
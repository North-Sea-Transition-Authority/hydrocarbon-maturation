<#include '../../layout/layout.ftl'>

<#assign pageTitle = "Reservoir details" />
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
  <@fdsForm.htmlForm>
      <@fdsTextInput.textInput path="form.reservoirName" labelText="Reservoir name"/>
      <@fdsRadio.radioGroup path="form.hydrocarbonsDiscovered" labelText="Hydrocarbons discovered">
          <#assign firstItem=true/>
          <#list hydrocarbonsDiscovered as key, value>
              <@fdsRadio.radioItem path="form.hydrocarbonsDiscovered" itemMap={key : value} isFirstItem=firstItem/>
              <#assign firstItem=false/>
          </#list>
      </@fdsRadio.radioGroup>

      <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>
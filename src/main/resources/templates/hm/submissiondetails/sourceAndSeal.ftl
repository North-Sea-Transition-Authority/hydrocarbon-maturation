<#include '../layout/layout.ftl'>

<#assign pageTitle = "Source and seal" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>

  <@fdsForm.htmlForm actionUrl=springUrl(actionUrl)>
    <@fdsFieldset.fieldset legendHeading="Source">
      <@fdsTextInput.textInput path="form.sourceFormationName" labelText="Formation name"/>
      <@fdsTextInput.textInput path="form.sourceAgeStage" labelText="Age stage or series" optionalLabel=true/>
      <@fdsTextInput.textInput path="form.sourceLithology" labelText="Lithology" optionalLabel=true/>
    </@fdsFieldset.fieldset>

    <@fdsFieldset.fieldset legendHeading="Top seal">
        <@fdsTextInput.textInput path="form.topSealFormationName" labelText="Formation name"/>
        <@fdsTextInput.textInput path="form.topSealAgeStage" labelText="Age stage or series" optionalLabel=true/>
        <@fdsTextInput.textInput path="form.topSealLithology" labelText="Lithology" optionalLabel=true/>
    </@fdsFieldset.fieldset>

    <@fdsAction.submitButtons primaryButtonText="Save and complete" secondaryButtonText="Save and complete later"/>
  </@fdsForm.htmlForm>
</@defaultPage>
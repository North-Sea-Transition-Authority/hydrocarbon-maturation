<#include '../layout/layout.ftl'>

<#assign pageTitle = "Reservoir properties" />
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
htmlTitle=pageTitle
pageHeading=pageTitle
pageSize=PageSize.TWO_THIRDS_COLUMN
errorItems=errorList
breadcrumbsMap=breadcrumbsMap>

  <@fdsForm.htmlForm>
      <@fdsFieldset.fieldset>
        <@fdsTextInput.textInput path="form.formationName" labelText="Formation name"/>
        <@fdsTextInput.textInput path="form.ageStageOrSeries" labelText="Age stage or series"/>
      <@fdsTextInput.textInput path="form.lithology" labelText="Lithology"/>
    </@fdsFieldset.fieldset>

    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>
<#include '../layout/layout.ftl'>
<#import '../reusableforms/permeability.ftl' as permeability>
<#assign pageTitle = "Permeability" />
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.FULL_WIDTH
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
  <@fdsForm.htmlForm>
    <@fdsRadio.radioGroup
      path="form.hasPermeability"
      labelText="Do you have permeability details?"
      hiddenContent=true>
      <@fdsRadio.radioYes path="form.hasPermeability">
        <@permeability.permeabilityForm
          form=form
          methodUsed=methodUsed
          source=source/>
      </@fdsRadio.radioYes>
      <@fdsRadio.radioNo path="form.hasPermeability"/>
    </@fdsRadio.radioGroup>
    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>
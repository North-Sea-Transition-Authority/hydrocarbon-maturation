<#include '../../layout/layout.ftl'>

<#assign pageTitle = "Drill stem test results" />
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
  <@fdsForm.htmlForm>
    <@fdsRadio.radioGroup
      path="form.hasDrillStemTestResult"
      fieldsetHeadingClass="govuk-fieldset__legend--l"
      labelText="Do you have any drill stem test results?"
      hiddenContent=true>
      <@fdsRadio.radioYes path="form.hasDrillStemTestResult"/>
      <@fdsRadio.radioNo path="form.hasDrillStemTestResult"/>
    </@fdsRadio.radioGroup>

    <@fdsAction.button buttonText="Save and continue" buttonValue=springUrl(actionUrl)/>

</@fdsForm.htmlForm>
</@defaultPage>
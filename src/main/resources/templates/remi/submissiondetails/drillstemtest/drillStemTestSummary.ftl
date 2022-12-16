<#include '../../layout/layout.ftl'>

<#assign pageTitle = "Drill stem test results" />
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
    <@fdsForm.htmlForm>
      <@fdsRadio.radioGroup
        path="form.drillStemTestSummaryOption"
        fieldsetHeadingClass="govuk-fieldset__legend--l"
        labelText="Do you want to add another drill stem test result?"
        hiddenContent=true>
        <#assign firstItem=true/>
        <#list drillStemTestSummaryOptions as key, value>
          <@fdsRadio.radioItem path="form.drillStemTestSummaryOption" itemMap={key : value} isFirstItem=firstItem/>
          <#assign firstItem=false/>
        </#list>
      </@fdsRadio.radioGroup>

      <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
    </@fdsForm.htmlForm>
</@defaultPage>
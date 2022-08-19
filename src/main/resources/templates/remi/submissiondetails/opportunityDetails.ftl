<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="pageTitle" type="String" -->
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="opportunityTypes" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>

    <@fdsForm.htmlForm actionUrl=springUrl(actionUrl)>

      <@fdsRadio.radioGroup path="form.opportunityType" labelText="Opportunity type" hiddenContent=true>
          <#assign firstItem=true/>
          <#list opportunityTypes as key, value>
              <@fdsRadio.radioItem path="form.opportunityType" itemMap={key : value} isFirstItem=firstItem>
                <#if key == "OTHER">
                  <@fdsTextInput.textInput path="form.otherTypeOfOpportunity"
                  labelText="Enter the type of opportunity" nestingPath="form.opportunityType"/>
                </#if>
                <#assign firstItem=false/>
              </@fdsRadio.radioItem>
          </#list>
      </@fdsRadio.radioGroup>

      <@fdsTextarea.textarea path="form.opportunityComments" labelText="Opportunity comments" optionalLabel=true/>

      <@fdsTextarea.textarea path="form.keyEvaluationWorkCarriedOut" labelText="Key evaluation work carried out"/>

      <@fdsTextarea.textarea path="form.keyWorkRequiredForFurtherMaturation" labelText="Key work required for further maturation" optionalLabel=true/>

      <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
    </@fdsForm.htmlForm>
</@defaultPage>
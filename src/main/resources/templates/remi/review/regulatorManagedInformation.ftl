<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="cancelurl" type="String" -->
<#-- @ftlvariable name="geographicalAreas" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="canUpdateResponsibleTeam" type="boolean"-->
<#-- @ftlvariable name="responsibleTeamTypes" type="java.util.Map<String, String>" -->

<@defaultPage
  htmlTitle=pageName
  pageHeading=pageName
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap
  caption=resourceDisplay>
  <@fdsForm.htmlForm>
    <@fdsRadio.radioGroup path="form.geographicalArea" labelText="Geographical area">
      <#assign firstItem=true/>
      <#list geographicalAreas as key, value>
        <@fdsRadio.radioItem path="form.geographicalArea" itemMap={key : value} isFirstItem=firstItem>
          <#assign firstItem=false/>
        </@fdsRadio.radioItem>
      </#list>
    </@fdsRadio.radioGroup>
    <#if canUpdateResponsibleTeam>
      <@fdsRadio.radioGroup path="form.responsibleTeamType" labelText="Responsible team">
          <#assign firstItem=true/>
          <#list responsibleTeamTypes as key, value>
              <@fdsRadio.radioItem path="form.responsibleTeamType" itemMap={key : value} isFirstItem=firstItem>
                  <#assign firstItem=false/>
              </@fdsRadio.radioItem>
          </#list>
      </@fdsRadio.radioGroup>
    </#if>
    <#if isResourceBeingRelinquished>
      <@fdsFieldset.fieldset
        legendHeading="Relinquishment report"
        legendHeadingSize="h2"
        legendHeadingClass="govuk-fieldset__legend--m"
        optionalLabel=true>
          <@fdsFileUpload.fileUpload
            id="relinquishmentReport"
            path="form.relinquishmentReport"
            downloadUrl=relinquishmentReportTemplate.downloadUrl()
            uploadUrl=relinquishmentReportTemplate.uploadUrl()
            deleteUrl=relinquishmentReportTemplate.deleteUrl()
            maxAllowedSize=relinquishmentReportTemplate.maxAllowedSize()
            allowedExtensions=relinquishmentReportTemplate.allowedExtensions()
            existingFiles=relinquishmentReportTemplate.existingFiles()
            multiFile=false/>
        </@fdsFieldset.fieldset>
    </#if>
    <#if shelvedCopFieldApiEndpoint??>
      <@fdsRadio.radioGroup
      path="form.linkedToShelvedCopField"
      labelText="Was this discovery created to track remaining volumes in a shelved field or a field that has ceased production?"
      hiddenContent=true>
        <@fdsRadio.radioYes path="form.linkedToShelvedCopField">
          <@fdsSearchSelector.searchSelectorRest path="form.fieldSelectedOption"
          restUrl=springUrl(shelvedCopFieldApiEndpoint)
          labelText="Provide the associated field"
          selectorMinInputLength=3
          preselectedItems=preSelectedFieldOption!{}/>
        </@fdsRadio.radioYes>
        <@fdsRadio.radioNo path="form.linkedToShelvedCopField" />
      </@fdsRadio.radioGroup>
    </#if>

      <@fdsAction.submitButtons primaryButtonText="Save and complete"
      secondaryLinkText="Cancel"
      linkSecondaryAction=true
      linkSecondaryActionUrl=springUrl(cancelUrl)/>
  </@fdsForm.htmlForm>
</@defaultPage>
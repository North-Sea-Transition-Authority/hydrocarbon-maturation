<#include '../layout/layout.ftl'>
<#import '../coordinates/coordinateInput.ftl' as coordinateInput/>
<#import '../probabilitydistribution/probabilitydistribution.ftl' as probabilityDistribution>

<#assign pageTitle = "Regulator managed information" />
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="cancelurl" type="String" -->
<#-- @ftlvariable name="geographicalAreas" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="canUpdateResponsibleTeam" type="boolean"-->
<#-- @ftlvariable name="responsibleTeamTypes" type="java.util.Map<String, String>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
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
      <@fdsAction.submitButtons primaryButtonText="Save and complete"
      secondaryLinkText="Cancel"
      linkSecondaryAction=true
      linkSecondaryActionUrl=springUrl(cancelUrl)/>
  </@fdsForm.htmlForm>
</@defaultPage>
<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="pageTitle" type="String" -->
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="opportunityTypes" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="mapOfOpportunityTemplate" type="uk.co.fivium.fileuploadlibrary.fds.FileUploadComponentAttributes" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>

    <@fdsForm.htmlForm>
      <#if prospectSubmission>
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
      </#if>
      <@fdsTextarea.textarea path="form.opportunityComments" labelText="Opportunity comments" optionalLabel=true/>

      <@fdsTextarea.textarea path="form.keyEvaluationWorkCarriedOut" labelText="Key evaluation work carried out"/>

      <@fdsTextarea.textarea path="form.keyWorkRequiredForFurtherMaturation" labelText="Key work required for further maturation" optionalLabel=true/>

      <@fdsFieldset.fieldset legendHeading=mapName legendHeadingSize="h2" legendHeadingClass="govuk-fieldset__legend--m">
        <@fdsFileUpload.fileUpload
          id="mapOfOpportunity"
          path="form.mapOfOpportunity"
          downloadUrl=mapOfOpportunityTemplate.downloadUrl()
          uploadUrl=mapOfOpportunityTemplate.uploadUrl()
          deleteUrl=mapOfOpportunityTemplate.deleteUrl()
          maxAllowedSize=mapOfOpportunityTemplate.maxAllowedSize()
          allowedExtensions=mapOfOpportunityTemplate.allowedExtensions()
          existingFiles=mapOfOpportunityTemplate.existingFiles()
          multiFile=false
        />
      </@fdsFieldset.fieldset>

      <@fdsFieldset.fieldset
        legendHeading="Representative seismic section"
        legendHeadingSize="h2"
        legendHeadingClass="govuk-fieldset__legend--m">
          <@fdsFileUpload.fileUpload
            id="representativeSeismicSection"
            path="form.representativeSeismicSection"
            downloadUrl=representativeSeismicSectionTemplate.downloadUrl()
            uploadUrl=representativeSeismicSectionTemplate.uploadUrl()
            deleteUrl=representativeSeismicSectionTemplate.deleteUrl()
            maxAllowedSize=representativeSeismicSectionTemplate.maxAllowedSize()
            allowedExtensions=representativeSeismicSectionTemplate.allowedExtensions()
            existingFiles=representativeSeismicSectionTemplate.existingFiles()
            multiFile=false/>
        </@fdsFieldset.fieldset>

      <@fdsFieldset.fieldset
        legendHeading="Geological cross section"
        legendHeadingSize="h2"
        legendHeadingClass="govuk-fieldset__legend--m"
        optionalLabel=true>
        <@fdsFileUpload.fileUpload
          id="geologicalCrossSection"
          path="form.geologicalCrossSection"
          downloadUrl=geologicalCrossSectionTemplate.downloadUrl()
          uploadUrl=geologicalCrossSectionTemplate.uploadUrl()
          deleteUrl=geologicalCrossSectionTemplate.deleteUrl()
          maxAllowedSize=geologicalCrossSectionTemplate.maxAllowedSize()
          allowedExtensions=geologicalCrossSectionTemplate.allowedExtensions()
          existingFiles=geologicalCrossSectionTemplate.existingFiles()
          multiFile=false/>
      </@fdsFieldset.fieldset>

      <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
    </@fdsForm.htmlForm>
</@defaultPage>
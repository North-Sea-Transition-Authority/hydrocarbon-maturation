<#include '../../layout/layout.ftl'>

<#-- @ftlvariable name="pageTitle" type="String" -->
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="outlineForMaximumCaseTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadTemplate" -->
<#-- @ftlvariable name="outlineForMaximumCaseUploads" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFileView>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>

    <@fdsForm.htmlForm>

      <@fdsInsetText.insetText>
        <p class="govuk-body">Shapefile must be a zipped archive (zip) containing the following files:</p>
        <ul class="govuk-list">
          <li>.shp</li>
          <li>.shx</li>
          <li>.dbf</li>
          <li>.prj</li>
        </ul>

        <p class="govuk-body">Ensure zipped archive only contains one shapefile, all files have the same name, and the files are not in folders.</p>
      </@fdsInsetText.insetText>
      <@fdsFileUpload.fileUpload
        id="outlineForMaximumCase"
        path="form.outlineForMaximumCase"
        downloadUrl=outlineForMaximumCaseTemplate.downloadUrl()
        uploadUrl=outlineForMaximumCaseTemplate.uploadUrl()
        deleteUrl=outlineForMaximumCaseTemplate.deleteUrl()
        maxAllowedSize=outlineForMaximumCaseTemplate.maxAllowedSize()
        allowedExtensions=outlineForMaximumCaseTemplate.allowedExtensions()
        existingFiles=outlineForMaximumCaseUploads
        multiFile=false
        />

        <@fdsAction.submitButtons
          primaryButtonText="Save and continue"
          secondaryLinkText="Cancel"
          linkSecondaryAction=true
          linkSecondaryActionUrl="${springUrl(cancelUrl)}"
          />

    </@fdsForm.htmlForm>
</@defaultPage>
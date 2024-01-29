<#include '../../layout/layout.ftl'>

<#-- @ftlvariable name="pageTitle" type="String" -->
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="cancelUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="outlineForMaximumCaseTemplate" type="uk.co.fivium.fileuploadlibrary.fds.FileUploadComponentAttributes" -->
<#-- @ftlvariable name="isOffshore" type="Boolean" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>

    <@fdsForm.htmlForm>

      <@fdsInsetText.insetText>
        <p class="govuk-body">Shapefile must be a zipped archive (.zip) containing the following files:</p>
        <ul class="govuk-list">
          <li>.shp</li>
          <li>.shx</li>
          <li>.dbf</li>
          <li>.prj</li>
        </ul>

        <p class="govuk-body">The shapefile must use one of the following Coordinate Reference Systems (CRS):</p>
        <ul class="govuk-list govuk-list--bullet">
          <#if isOffshore>
            <li>European Datum 1950 (ED50)</li>
            <li>World Geodetic System 1984 (WGS84)</li>
            <li>European Terrestrial Reference System 1989 (ETRS89)</li>
          <#else>
            <li>British National Grid (OSGB36)</li>
          </#if>
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
        existingFiles=outlineForMaximumCaseTemplate.existingFiles()
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
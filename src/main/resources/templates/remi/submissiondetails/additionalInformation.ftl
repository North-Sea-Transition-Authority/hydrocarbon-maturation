<#include '../layout/layout.ftl'>

<#-- @ftlvariable name="pageTitle" type="String" -->
<#-- @ftlvariable name="backLinkUrl" type="String" -->
<#-- @ftlvariable name="actionUrl" type="String" -->
<#-- @ftlvariable name="errorList" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.fds.ErrorItem>" -->
<#-- @ftlvariable name="breadcrumbsMap" type="java.util.Map<String, String>" -->
<#-- @ftlvariable name="additionalDocumentTemplate" type="uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadTemplate" -->
<#-- @ftlvariable name="additionalDocumentUploads" type="java.util.List<uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFileView>" -->

<@defaultPage
  htmlTitle=pageTitle
  pageHeading=pageTitle
  pageSize=PageSize.TWO_THIRDS_COLUMN
  errorItems=errorList
  breadcrumbsMap=breadcrumbsMap>
  <@fdsForm.htmlForm>
    <@fdsRadio.radioGroup path="form.providingAdditionalComments" labelText="Do you have any additional comments?" hiddenContent=true>
      <@fdsRadio.radioYes path="form.providingAdditionalComments">
        <@fdsTextarea.textarea path="form.additionalComments"
          labelText="Additional comments" nestingPath="form.additionalComments"/>
      </@fdsRadio.radioYes>
      <@fdsRadio.radioNo path="form.providingAdditionalComments"/>
    </@fdsRadio.radioGroup>

    <@fdsRadio.radioGroup path="form.providingAdditionalDocuments" labelText="Do you have any additional documents?" hiddenContent=true>
      <@fdsRadio.radioYes path="form.providingAdditionalDocuments">
        <@fdsFieldset.fieldset legendHeading="Additional documents" legendHeadingSize="h2" legendHeadingClass="govuk-fieldset__legend--m">
          <@fdsFileUpload.fileUpload
            id="additionalDocuments"
            path="form.additionalDocuments"
            downloadUrl=additionalDocumentTemplate.downloadUrl()
            uploadUrl=additionalDocumentTemplate.uploadUrl()
            deleteUrl=additionalDocumentTemplate.deleteUrl()
            maxAllowedSize=additionalDocumentTemplate.maxAllowedSize()
            allowedExtensions=additionalDocumentTemplate.allowedExtensions()
            existingFiles=additionalDocumentUploads
            multiFile=true/>
        </@fdsFieldset.fieldset>
      </@fdsRadio.radioYes>
      <@fdsRadio.radioNo path="form.providingAdditionalDocuments"/>
    </@fdsRadio.radioGroup>

    <@fdsAction.submitButtons primaryButtonText=submitPrimaryButtonText secondaryButtonText=submitSecondaryButtonText/>
  </@fdsForm.htmlForm>
</@defaultPage>
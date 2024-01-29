# How to add a supporting document file upload field

**Supporting document type**
- Create a new FileDocumentType enum constant named after the data item name, e.g. MAP_OF_OPPORTUNITY for map of opportunity.  
  
**Form page class** _(see OpportunityDetailsForm)_
- Add a `List<FileUploadForm>` for each file upload field on the form page.

**Form service class** _(see OpportunityDetailsFormService)_
- Add a method to get all file uploads from the form for the file upload field.

**Controller** _(see OpportunityDetailsController)_
- On GET method:
    - Add to the model and view for each file upload field, a file upload template by calling the supportingDocumentService.buildFileUploadTemplate method.
    - Call method to get uploaded files for the file upload field and pass in to the model and view.
- On POST method:
    - Call the same method to get uploaded files for the file upload field but pass in the binded form.

**Validation** _(see OpportunityDetailsFormValidator)_
- Save and complete (full validation):
    - If file uploads are required then check the file uploads list on the form is empty and if so, add a validation error 
    - Call FileUploadValidationUtils.rejectIfFileDescriptionsAreEmptyOrWhitespace on the file upload field to validate file descriptions have been provided

**Saving** _(see OpportunityDetailsPersistenceService)_
- On the given form page persistence service
    - Save file upload descriptions by calling supportingDocumentService.updateSupportingDocumentDescriptions and passing in the file upload list from the form.

**Freemarker** _(see opportunityDetails.ftl)_
- Use the FileUploadTemplate passed into the model and view with the given attributes to set out the fdsFileUpload.fileUpload macro
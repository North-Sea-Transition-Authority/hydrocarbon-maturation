package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadUtils;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFileView;

@Service
class RegulatorManagedInformationFormService {

  private final RegulatorManagedInformationFormValidator regulatorManagedInformationFormValidator;
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final ResourceSupportingDocumentService resourceSupportingDocumentService;

  @Autowired
  RegulatorManagedInformationFormService(
      RegulatorManagedInformationFormValidator regulatorManagedInformationFormValidator,
      RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
      ResourceSupportingDocumentService resourceSupportingDocumentService) {
    this.regulatorManagedInformationFormValidator = regulatorManagedInformationFormValidator;
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.resourceSupportingDocumentService = resourceSupportingDocumentService;
  }

  RegulatorManagedInformationForm getRegulatorManagedInformationForm(Resource resource) {
    var relinquishmentReportUploads = resourceSupportingDocumentService
        .getFileUploadFormListForSubmissionAndType(resource, ResourceSupportingDocumentType.RELINQUISHMENT_REPORT);

    return regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource)
        .map(regulatorManagedInformation -> regulatorManagedInformationEntityToForm(
            regulatorManagedInformation, relinquishmentReportUploads))
        .orElseGet(RegulatorManagedInformationForm::new);
  }

  public BindingResult validate(
      RegulatorManagedInformationForm form, BindingResult bindingResult, boolean isResourceBeingRelinquished) {
    regulatorManagedInformationFormValidator.validate(
        form, bindingResult, new RegulatorManagedInformationFormValidatorHints(isResourceBeingRelinquished));
    return bindingResult;
  }

  public List<UploadedFileView> getRelinquishmentReportUploads(RegulatorManagedInformationForm form) {
    var relinquishmentReportUploadIdList = FileUploadUtils.getUploadIdList(form.getRelinquishmentReport());
    return resourceSupportingDocumentService.getUploadedFileViewList(relinquishmentReportUploadIdList);
  }

  private RegulatorManagedInformationForm regulatorManagedInformationEntityToForm(
      RegulatorManagedInformation regulatorManagedInformation,
      List<FileUploadForm> relinquishmentReportUploads) {
    var form = new RegulatorManagedInformationForm();

    form.setGeographicalArea(regulatorManagedInformation.getGeographicalArea());
    form.setRelinquishmentReport(relinquishmentReportUploads);

    return form;
  }
}

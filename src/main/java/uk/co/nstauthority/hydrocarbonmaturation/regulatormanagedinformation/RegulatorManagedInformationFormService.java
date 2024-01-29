package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;

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
        .getUploadedFileForms(resource, FileDocumentType.RELINQUISHMENT_REPORT);

    return regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource)
        .map(regulatorManagedInformation -> regulatorManagedInformationEntityToForm(
            regulatorManagedInformation, relinquishmentReportUploads))
        .orElseGet(RegulatorManagedInformationForm::new);
  }

  public BindingResult validate(
      RegulatorManagedInformationForm form, BindingResult bindingResult, boolean isResourceBeingRelinquished,
      boolean isResponsibleTeamBeingUpdated) {
    regulatorManagedInformationFormValidator.validate(
        form, bindingResult, new RegulatorManagedInformationFormValidatorHints(isResourceBeingRelinquished,
            isResponsibleTeamBeingUpdated));
    return bindingResult;
  }

  private RegulatorManagedInformationForm regulatorManagedInformationEntityToForm(
      RegulatorManagedInformation regulatorManagedInformation,
      List<UploadedFileForm> relinquishmentReportUploads) {
    var form = new RegulatorManagedInformationForm();

    form.setGeographicalArea(regulatorManagedInformation.getGeographicalArea());
    form.setRelinquishmentReport(relinquishmentReportUploads);
    form.setResponsibleTeamType(regulatorManagedInformation.getResponsibleTeamType());

    return form;
  }
}

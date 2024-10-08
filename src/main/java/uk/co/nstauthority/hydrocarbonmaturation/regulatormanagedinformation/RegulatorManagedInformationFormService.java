package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.PostFieldDiscoveryLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;

@Service
public class RegulatorManagedInformationFormService {

  private final RegulatorManagedInformationFormValidator regulatorManagedInformationFormValidator;
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final ResourceSupportingDocumentService resourceSupportingDocumentService;
  private final PostFieldDiscoveryLinkService postFieldDiscoveryLinkService;
  private final SearchSelectorService searchSelectorService;

  @Autowired
  RegulatorManagedInformationFormService(
      RegulatorManagedInformationFormValidator regulatorManagedInformationFormValidator,
      RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
      ResourceSupportingDocumentService resourceSupportingDocumentService,
      PostFieldDiscoveryLinkService postFieldDiscoveryLinkService,
      SearchSelectorService searchSelectorService) {
    this.regulatorManagedInformationFormValidator = regulatorManagedInformationFormValidator;
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.resourceSupportingDocumentService = resourceSupportingDocumentService;
    this.postFieldDiscoveryLinkService = postFieldDiscoveryLinkService;
    this.searchSelectorService = searchSelectorService;
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
      boolean isResponsibleTeamBeingUpdated, boolean isAreaTeamUser) {
    regulatorManagedInformationFormValidator.validate(
        form, bindingResult, new RegulatorManagedInformationFormValidatorHints(isResourceBeingRelinquished,
            isResponsibleTeamBeingUpdated, BooleanUtils.isTrue(form.getLinkedToShelvedCopField()), isAreaTeamUser));
    return bindingResult;
  }

  public Map<String, String> getPreSelectedFieldOption(Resource resource) {
    var field = postFieldDiscoveryLinkService.getFieldLinkedToDiscoveryIfExists(resource);

    if (field.isPresent()) {
      var f = field.get();
      return searchSelectorService.buildPrePopulatedSelection(String.valueOf(f.getFieldIdentifier()),
          f.getCorrectedLegalFieldName());
    }

    return Map.of();
  }

  private RegulatorManagedInformationForm regulatorManagedInformationEntityToForm(
      RegulatorManagedInformation regulatorManagedInformation,
      List<UploadedFileForm> relinquishmentReportUploads
  ) {
    var form = new RegulatorManagedInformationForm();

    form.setGeographicalArea(regulatorManagedInformation.getGeographicalArea());
    form.setRelinquishmentReport(relinquishmentReportUploads);
    form.setResponsibleTeamType(regulatorManagedInformation.getResponsibleTeamType());
    form.setLinkedToShelvedCopField(regulatorManagedInformation.getLinkedToShelvedCopField());

    return form;
  }
}

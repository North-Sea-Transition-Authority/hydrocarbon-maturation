package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentsController;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;

@Service
public class RegulatorManagedInformationSummaryService {
  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final ResourceSupportingDocumentService resourceSupportingDocumentService;

  @Autowired
  public RegulatorManagedInformationSummaryService(
      RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
      SubmissionDetailService submissionDetailService,
      ResourceSupportingDocumentService resourceSupportingDocumentService) {
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.resourceSupportingDocumentService = resourceSupportingDocumentService;
  }


  public RegulatorManagedInformationSummaryView getRegulatorManagedInformationSummaryFromResource(
      Resource resource) {
    var regulatorManagedInformation = regulatorManagedInformationPersistenceService
        .getRegulatorManagedInformationByResource(resource);
    var isBeingRelinquished = submissionDetailService.isLatestSubmissionForResourceRelinquishment(resource);
    return regulatorManagedInformation
        .map(regulatorManagedInfo -> createSummaryViewFromRegulatorManagedInformation(regulatorManagedInfo, isBeingRelinquished))
        .orElse(RegulatorManagedInformationSummaryView.empty(isBeingRelinquished));
  }

  private RegulatorManagedInformationSummaryView createSummaryViewFromRegulatorManagedInformation(
      RegulatorManagedInformation regulatorManagedInformation, boolean isBeingRelinquished) {
    return new RegulatorManagedInformationSummaryView(
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(regulatorManagedInformation.getResponsibleTeamType()),
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(regulatorManagedInformation.getGeographicalArea()),
        isBeingRelinquished,
        isBeingRelinquished ? getRelinquishmentReportUploads(regulatorManagedInformation) : Map.of()
    );
  }

  private Map<UploadedFile, String> getRelinquishmentReportUploads(
      RegulatorManagedInformation regulatorManagedInformation) {
    var relinquishmentReportUploadsAndLinks = new HashMap<UploadedFile, String>();
    var fileUploadList = resourceSupportingDocumentService.getUploadedFiles(
        regulatorManagedInformation.getResource(),
        FileDocumentType.RELINQUISHMENT_REPORT);
    fileUploadList.forEach(file -> relinquishmentReportUploadsAndLinks.put(
        file,
        ReverseRouter.route(on(ResourceSupportingDocumentsController.class)
            .download(regulatorManagedInformation.getResource().getId(), file.getId())))
    );
    return relinquishmentReportUploadsAndLinks;
  }
}

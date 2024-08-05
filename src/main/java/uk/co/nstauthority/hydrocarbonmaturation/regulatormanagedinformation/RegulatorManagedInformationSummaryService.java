package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.FileSummaryUtil;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryFileViewRow;

@Service
public class RegulatorManagedInformationSummaryService {

  static final String FILE_ROW_KEY = "Relinquishment report";

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
        .map(regulatorManagedInfo -> createSummaryViewFromRegulatorManagedInformation(regulatorManagedInfo,
            isBeingRelinquished))
        .orElse(RegulatorManagedInformationSummaryView.empty(isBeingRelinquished));
  }

  private RegulatorManagedInformationSummaryView createSummaryViewFromRegulatorManagedInformation(
      RegulatorManagedInformation regulatorManagedInformation, boolean isBeingRelinquished) {
    return new RegulatorManagedInformationSummaryView(
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(regulatorManagedInformation.getResponsibleTeamType()),
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(regulatorManagedInformation.getGeographicalArea()),
        isBeingRelinquished,
        isBeingRelinquished ? getRelinquishmentReportUploads(regulatorManagedInformation) : List.of()
    );
  }

  private List<SummaryFileViewRow> getRelinquishmentReportUploads(
      RegulatorManagedInformation regulatorManagedInformation) {
    var fileUploadList = resourceSupportingDocumentService.getUploadedFiles(
        regulatorManagedInformation.getResource(), FileDocumentType.RELINQUISHMENT_REPORT
    );

    return FileSummaryUtil.convertResourceSupportingFilesToSummaryRows(FILE_ROW_KEY, fileUploadList,
        regulatorManagedInformation.getResource());
  }
}
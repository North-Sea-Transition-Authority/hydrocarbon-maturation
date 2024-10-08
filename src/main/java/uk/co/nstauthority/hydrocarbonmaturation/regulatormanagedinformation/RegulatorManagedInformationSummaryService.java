package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.PostFieldDiscoveryLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.FileSummaryUtil;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryFileViewRow;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;

@Service
public class RegulatorManagedInformationSummaryService {

  static final String FILE_ROW_KEY = "Relinquishment report";

  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final ResourceSupportingDocumentService resourceSupportingDocumentService;
  private final PostFieldDiscoveryLinkService postFieldDiscoveryLinkService;

  @Autowired
  public RegulatorManagedInformationSummaryService(
      RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
      SubmissionDetailService submissionDetailService,
      ResourceSupportingDocumentService resourceSupportingDocumentService,
      PostFieldDiscoveryLinkService postFieldDiscoveryLinkService) {
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.resourceSupportingDocumentService = resourceSupportingDocumentService;
    this.postFieldDiscoveryLinkService = postFieldDiscoveryLinkService;
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
    var linkedToFieldName = "";
    var linkedToFieldUrl = "";

    if (BooleanUtils.isTrue(regulatorManagedInformation.getLinkedToShelvedCopField())) {
      var field = postFieldDiscoveryLinkService.getFieldLinkedToDiscoveryIfExists(regulatorManagedInformation.getResource());
      if (field.isPresent()) {
        linkedToFieldName = field.get().getCorrectedLegalFieldName();
        linkedToFieldUrl = ReverseRouter.route(on(ViewResourceRecordController.class)
            .showResourceRecord(field.get().getResource().getId(), null));
      }
    }

    return new RegulatorManagedInformationSummaryView(
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(regulatorManagedInformation.getResponsibleTeamType()),
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(regulatorManagedInformation.getGeographicalArea()),
        isBeingRelinquished,
        isBeingRelinquished ? getRelinquishmentReportUploads(regulatorManagedInformation) : List.of(),
        linkedToFieldName,
        linkedToFieldUrl
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
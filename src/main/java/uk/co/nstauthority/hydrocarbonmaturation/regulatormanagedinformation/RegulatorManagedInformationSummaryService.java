package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery.PostFieldDiscoveryLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcesupportingdocuments.ResourceSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.enumutil.DisplayableEnumOptionUtil;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.FileSummaryUtil;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryFileViewRow;
import uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.ViewResourceRecordController;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscovery;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoveryService;
import uk.co.nstauthority.hydrocarbonmaturation.util.BooleanUtil;
import uk.co.nstauthority.hydrocarbonmaturation.util.StreamUtil;

@Service
public class RegulatorManagedInformationSummaryService {

  static final String FILE_ROW_KEY = "Relinquishment report";

  private final RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;
  private final SubmissionDetailService submissionDetailService;
  private final ResourceSupportingDocumentService resourceSupportingDocumentService;
  private final PostFieldDiscoveryLinkService postFieldDiscoveryLinkService;
  private final ResourceLinkService resourceLinkService;
  private final FieldPersistenceService fieldPersistenceService;
  private final UndevelopedDiscoveryService undevelopedDiscoveryService;

  @Autowired
  public RegulatorManagedInformationSummaryService(
      RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService,
      SubmissionDetailService submissionDetailService,
      ResourceSupportingDocumentService resourceSupportingDocumentService,
      PostFieldDiscoveryLinkService postFieldDiscoveryLinkService,
      ResourceLinkService resourceLinkService,
      FieldPersistenceService fieldPersistenceService,
      UndevelopedDiscoveryService undevelopedDiscoveryService
  ) {
    this.regulatorManagedInformationPersistenceService = regulatorManagedInformationPersistenceService;
    this.submissionDetailService = submissionDetailService;
    this.resourceSupportingDocumentService = resourceSupportingDocumentService;
    this.postFieldDiscoveryLinkService = postFieldDiscoveryLinkService;
    this.resourceLinkService = resourceLinkService;
    this.fieldPersistenceService = fieldPersistenceService;
    this.undevelopedDiscoveryService = undevelopedDiscoveryService;
  }

  public RegulatorManagedInformationSummaryView getRegulatorManagedInformationSummaryFromResource(
      Resource resource) {
    var regulatorManagedInformation = regulatorManagedInformationPersistenceService
        .getRegulatorManagedInformationByResource(resource);
    var isBeingRelinquished = submissionDetailService.isLatestSubmissionForResourceRelinquishment(resource);
    return regulatorManagedInformation
        .map(regulatorManagedInfo -> createSummaryViewFromRegulatorManagedInformation(
            resource,
            regulatorManagedInfo,
            isBeingRelinquished
        ))
        .orElse(RegulatorManagedInformationSummaryView.empty(isBeingRelinquished));
  }

  public Map<String, String> getFieldsAssociatedWithDiscoveryMap(Resource discoveryResource) {
    return resourceLinkService.getAssociatedFieldResources(discoveryResource).stream()
        .map(fieldPersistenceService::getFieldByResource)
        .flatMap(Optional::stream)
        .sorted(Comparator.comparing(Field::getCorrectedLegalFieldName))
        .collect(StreamUtil.toLinkedHashMap(
            f -> ReverseRouter.route(on(ViewResourceRecordController.class).showResourceRecord(f.getResource().getId(), null)),
            Field::getCorrectedLegalFieldName
        ));
  }

  private RegulatorManagedInformationSummaryView createSummaryViewFromRegulatorManagedInformation(
      Resource resource,
      RegulatorManagedInformation regulatorManagedInformation,
      boolean isBeingRelinquished
  ) {
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

    var isUndevelopedDiscovery = undevelopedDiscoveryService.getUndevelopedDiscoveryForResource(resource)
        .map(UndevelopedDiscovery::getIsUndevelopedDiscovery)
        .orElse(false);


    return new RegulatorManagedInformationSummaryView(
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(regulatorManagedInformation.getResponsibleTeamType()),
        DisplayableEnumOptionUtil.getDisplayableOptionOrEmpty(regulatorManagedInformation.getGeographicalArea()),
        regulatorManagedInformation.getResourceName() != null ? regulatorManagedInformation.getResourceName() : "",
        isBeingRelinquished,
        isBeingRelinquished ? getRelinquishmentReportUploads(regulatorManagedInformation) : List.of(),
        linkedToFieldName,
        linkedToFieldUrl,
        regulatorManagedInformation.getWellReference() != null ? regulatorManagedInformation.getWellReference() : "",
        BooleanUtil.yesNoFromBoolean(isUndevelopedDiscovery)
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
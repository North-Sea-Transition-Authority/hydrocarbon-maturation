package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileUploadService;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionSummaryView;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;

@Service
public class TrapPropertiesSummaryService {

  private final TrapPropertiesPersistenceService trapPropertiesPersistenceService;
  private final ProbabilityDistributionSummaryService probabilityDistributionSummaryService;
  private final FileUploadService fileUploadService;
  private final SupportingDocumentService supportingDocumentService;

  @Autowired
  public TrapPropertiesSummaryService(TrapPropertiesPersistenceService trapPropertiesPersistenceService,
                                      ProbabilityDistributionSummaryService probabilityDistributionSummaryService,
                                      FileUploadService fileUploadService,
                                      SupportingDocumentService supportingDocumentService) {
    this.trapPropertiesPersistenceService = trapPropertiesPersistenceService;
    this.probabilityDistributionSummaryService = probabilityDistributionSummaryService;
    this.fileUploadService = fileUploadService;
    this.supportingDocumentService = supportingDocumentService;
  }

  public TrapPropertiesSummaryView getTrapPropertiesSummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var trapProperties = trapPropertiesPersistenceService.getTrapProperty(submissionDetail);

    return trapProperties
        .map(trapProperty -> createSummaryViewFromTrapProperty(submissionDetail, trapProperty))
        .orElse(createSummaryViewFromTrapProperty(submissionDetail, new TrapProperty()));
  }

  private TrapPropertiesSummaryView createSummaryViewFromTrapProperty(SubmissionDetail submissionDetail,
                                                                      TrapProperty trapProperty) {
    var trapDescription = Objects.requireNonNullElse(trapProperty.getTrapDescription(), "");
    var trapType = trapProperty.getTrapType() != null ? trapProperty.getTrapType().getDisplayName() : "";
    var otherTrapType = Objects.requireNonNullElse(trapProperty.getOtherTrapType(), "");

    var latitudeDegrees = trapProperty.getLatitudeDegrees() != null
        ? trapProperty.getLatitudeDegrees().toString() + "°"
        : "";
    var latitudeMinutes = trapProperty.getLatitudeMinutes() != null
        ? trapProperty.getLatitudeMinutes().toString() + "'"
        : "";
    var latitudeSeconds = trapProperty.getLatitudeSeconds() != null
        ? trapProperty.getLatitudeSeconds().toString() + "\""
        : "";
    var longitudeDegrees = trapProperty.getLongitudeDegrees() != null
        ? trapProperty.getLongitudeDegrees().toString() + "°"
        : "";
    var longitudeMinutes = trapProperty.getLongitudeMinutes() != null
        ? trapProperty.getLongitudeMinutes().toString() + "'"
        : "";
    var longitudeSeconds = trapProperty.getLongitudeSeconds() != null
        ? trapProperty.getLongitudeSeconds().toString() + "\""
        : "";

    var longitudeHemisphere = trapProperty.getLongitudeHemisphere() != null
        ? trapProperty.getLongitudeHemisphere().getDisplayName() : "";

    var listOfInstances = new ArrayList<ProbabilityDistributionFieldInstance>();
    var resourceType = submissionDetail.getSubmission().getResource().getType();

    if (resourceType == ResourceType.PROSPECT) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT);

      listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_PROSPECT);

      listOfInstances.add(ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT);
    } else if (resourceType == ResourceType.DISCOVERY) {
      listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_DISCOVERY);

      listOfInstances.add(ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_DISCOVERY);

      listOfInstances.add(ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_DISCOVERY);
    }

    var listOfSummaryViews = new ArrayList<ProbabilityDistributionSummaryView>();
    listOfInstances.forEach(instance -> listOfSummaryViews.add(
        probabilityDistributionSummaryService.getProbabilityDistributionSummaryViewFromFieldInstanceAndSubmission(
            instance, submissionDetail)));

    var fileUploadList = supportingDocumentService.getFileUploadFormListForSubmissionAndType(
        submissionDetail,
        SupportingDocumentType.PROSPECT_DEPTH_MAP);

    var prospectDepthMapUploadsAndLinks = new HashMap<UploadedFile, String>();

    fileUploadList
        .forEach(file
            -> prospectDepthMapUploadsAndLinks.put(fileUploadService.findUploadedFileOrThrow(file.getUploadedFileId()),
            ReverseRouter.route(on(SubmissionSupportingDocumentsController.class)
                .download(submissionDetail.getId(), file.getUploadedFileId()))));

    var datumDisplay = trapProperty.getDatum() != null
        ? trapProperty.getDatum().getDisplayName()
        : "";

    var quadBlock = trapProperty.getCentreOfInterestQuadrantBlock() != null
        ? trapProperty.getCentreOfInterestQuadrantBlock()
        : "";

    return new TrapPropertiesSummaryView(
        trapDescription,
        trapType,
        otherTrapType,
        latitudeDegrees,
        latitudeMinutes,
        latitudeSeconds,
        longitudeDegrees,
        longitudeMinutes,
        longitudeSeconds,
        longitudeHemisphere,
        datumDisplay,
        quadBlock,
        listOfSummaryViews,
        prospectDepthMapUploadsAndLinks
    );
  }
}

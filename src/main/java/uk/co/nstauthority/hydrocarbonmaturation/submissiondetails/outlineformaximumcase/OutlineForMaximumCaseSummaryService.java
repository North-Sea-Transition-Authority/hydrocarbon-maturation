package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.file.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentsController;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocument;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;

@Service
public class OutlineForMaximumCaseSummaryService {

  private final OutlineForMaximumCaseMapService outlineForMaximumCaseMapService;
  private final SupportingDocumentService supportingDocumentService;
  private final OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;

  @Autowired
  public OutlineForMaximumCaseSummaryService(OutlineForMaximumCaseMapService outlineForMaximumCaseMapService,
                                             SupportingDocumentService supportingDocumentService,
                                             OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService) {
    this.outlineForMaximumCaseMapService = outlineForMaximumCaseMapService;
    this.supportingDocumentService = supportingDocumentService;
    this.outlineForMaximumCasePersistenceService = outlineForMaximumCasePersistenceService;
  }

  public OutlineForMaximumCaseSummaryView getOutlineForMaximumCaseSummaryFromSubmissionDetail(SubmissionDetail submissionDetail) {
    var fileUploadMap = getFileUploadMap(submissionDetail);

    var hasVerifiedShapefile = outlineForMaximumCasePersistenceService.hasVerifiedShapefile(submissionDetail);

    return new OutlineForMaximumCaseSummaryView(
        fileUploadMap,
        hasVerifiedShapefile,
        outlineForMaximumCaseMapService.getMapGeoJsonDataRoute(submissionDetail),
        outlineForMaximumCaseMapService.getMapAttributions()
    );
  }

  private Map<UploadedFile, String> getFileUploadMap(SubmissionDetail submissionDetail) {
    return supportingDocumentService.getSupportingDocumentsForType(
            submissionDetail,
            SupportingDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE
        )
        .map(SupportingDocument::getUploadedFile)
        .map(uploadedFile -> Map.of(uploadedFile, ReverseRouter.route(on(SubmissionSupportingDocumentsController.class)
                .download(submissionDetail.getId(), uploadedFile.getId()))))
        .orElse(Map.of());
  }
}

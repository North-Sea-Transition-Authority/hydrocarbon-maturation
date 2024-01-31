package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentsController;

@Service
public class OutlineForMaximumCaseSummaryService {

  private final OutlineForMaximumCaseMapService outlineForMaximumCaseMapService;
  private final SubmissionSupportingDocumentService supportingDocumentService;
  private final OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;
  private final OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService;

  @Autowired
  public OutlineForMaximumCaseSummaryService(OutlineForMaximumCaseMapService outlineForMaximumCaseMapService,
                                             SubmissionSupportingDocumentService supportingDocumentService,
                                             OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService,
                                             OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService) {
    this.outlineForMaximumCaseMapService = outlineForMaximumCaseMapService;
    this.supportingDocumentService = supportingDocumentService;
    this.outlineForMaximumCasePersistenceService = outlineForMaximumCasePersistenceService;
    this.outlineForMaximumCaseDocumentTypeService = outlineForMaximumCaseDocumentTypeService;
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
    var documentType = outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(submissionDetail);
    return supportingDocumentService.getUploadedFiles(submissionDetail, documentType)
        .stream()
        .collect(Collectors.toMap(Function.identity(), getUploadedFileDownloadRoute(submissionDetail)));
  }

  private static Function<UploadedFile, String> getUploadedFileDownloadRoute(SubmissionDetail submissionDetail) {
    return uploadedFile ->
        ReverseRouter.route(on(SubmissionSupportingDocumentsController.class)
            .download(submissionDetail.getId(), uploadedFile.getId()));
  }
}

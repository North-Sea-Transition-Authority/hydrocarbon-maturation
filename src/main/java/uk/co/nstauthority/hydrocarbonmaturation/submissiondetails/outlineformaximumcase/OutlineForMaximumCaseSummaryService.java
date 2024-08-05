package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.FileSummaryUtil;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryFileViewRow;

@Service
public class OutlineForMaximumCaseSummaryService {

  static final String FILE_ROW_KEY = "Outline for maximum case shapefile";

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

  public OutlineForMaximumCaseSummaryView getOutlineForMaximumCaseSummaryFromSubmissionDetail(
      SubmissionDetail submissionDetail) {
    var uploadedFiles = getUploadedFiles(submissionDetail);

    var hasVerifiedShapefile = outlineForMaximumCasePersistenceService.hasVerifiedShapefile(submissionDetail);

    List<SummaryFileViewRow> fileViews = FileSummaryUtil.convertSubmissionSupportingFilesToSummaryRows(
        FILE_ROW_KEY,
        uploadedFiles,
        submissionDetail
    );
    if (fileViews.isEmpty()) {
      fileViews = List.of(SummaryFileViewRow.empty(FILE_ROW_KEY));
    }

    return new OutlineForMaximumCaseSummaryView(
        fileViews,
        hasVerifiedShapefile,
        outlineForMaximumCaseMapService.getMapGeoJsonDataRoute(submissionDetail),
        outlineForMaximumCaseMapService.getMapAttributions()
    );
  }

  private List<UploadedFile> getUploadedFiles(SubmissionDetail submissionDetail) {
    var documentType = outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(submissionDetail);
    return supportingDocumentService.getUploadedFiles(submissionDetail, documentType);
  }
}

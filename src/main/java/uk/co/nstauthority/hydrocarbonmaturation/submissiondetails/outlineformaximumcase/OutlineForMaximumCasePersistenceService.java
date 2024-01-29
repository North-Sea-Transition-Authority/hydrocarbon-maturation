package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import java.util.Collection;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.gis.ShapefileConversionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;

@Service
public class OutlineForMaximumCasePersistenceService {

  private final SubmissionSupportingDocumentService supportingDocumentService;
  private final ShapefileConversionService shapefileConversionService;
  private final OutlineForMaximumCaseRepository outlineForMaximumCaseRepository;

  @Autowired
  OutlineForMaximumCasePersistenceService(SubmissionSupportingDocumentService supportingDocumentService,
                                          ShapefileConversionService shapefileConversionService,
                                          OutlineForMaximumCaseRepository outlineForMaximumCaseRepository) {
    this.supportingDocumentService = supportingDocumentService;
    this.shapefileConversionService = shapefileConversionService;
    this.outlineForMaximumCaseRepository = outlineForMaximumCaseRepository;
  }

  @Transactional
  public void saveOutlineForMaximumCaseVerifyForm(SubmissionDetail submissionDetail, OutlineForMaximumCaseVerifyForm form) {
    var outlineForMaximumCase = getOutlineForMaximumCaseBySubmissionDetail(submissionDetail)
        .orElseGet(OutlineForMaximumCase::new);

    outlineForMaximumCase.setSubmissionDetail(submissionDetail);
    outlineForMaximumCase.setHasVerifiedShapefile(form.getShapeIsCorrect());

    outlineForMaximumCaseRepository.save(outlineForMaximumCase);
  }

  @Transactional
  public void deleteOutlineForMaximumCase(SubmissionDetail submissionDetail) {
    var uploadedShapefileZip = getUploadedShapefileZip(submissionDetail);
    supportingDocumentService.deleteFile(uploadedShapefileZip.getId());
    outlineForMaximumCaseRepository.deleteBySubmissionDetail(submissionDetail);
  }

  String getGeoJsonForOutlineForMaximumCase(SubmissionDetail submissionDetail) {
    var uploadedShapefileZip = getUploadedShapefileZip(submissionDetail);

    return shapefileConversionService.convertShapefileToGeoJson(uploadedShapefileZip);
  }

  boolean hasVerifiedShapefile(SubmissionDetail submissionDetail) {
    return getOutlineForMaximumCaseBySubmissionDetail(submissionDetail)
        .filter(OutlineForMaximumCase::getHasVerifiedShapefile)
        .isPresent();
  }

  Optional<OutlineForMaximumCase> getOutlineForMaximumCaseBySubmissionDetail(SubmissionDetail submissionDetail) {
    return outlineForMaximumCaseRepository.findBySubmissionDetail(submissionDetail);
  }

  UploadedFile getUploadedShapefileZip(SubmissionDetail submissionDetail) {
    var type = FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE;
    var uploadedFiles = supportingDocumentService.getUploadedFiles(submissionDetail, type);

    if (uploadedFiles.isEmpty()) {
      throw new IllegalStateException("No shapefile found for submission detail %s and type %s"
          .formatted(submissionDetail.getId(), type.name()));
    }

    if (uploadedFiles.size() > 1) {
      throw new IllegalStateException("Multiple shapefiles found for submission detail %s and type %s"
          .formatted(submissionDetail.getId(), type.name()));
    }

    return uploadedFiles.get(0);
  }

  @Transactional
  public void saveOutlineForMaximumCaseUploads(SubmissionDetail submissionDetail,
                                               Collection<UploadedFileForm> uploadedFileForms) {
    supportingDocumentService
        .saveDocuments(submissionDetail, uploadedFileForms, FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE);
  }
}

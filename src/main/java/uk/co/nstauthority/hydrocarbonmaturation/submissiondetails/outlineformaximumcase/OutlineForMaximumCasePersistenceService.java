package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.gis.ShapefileConversionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocument;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;

@Service
public class OutlineForMaximumCasePersistenceService {

  private final SupportingDocumentService supportingDocumentService;
  private final ShapefileConversionService shapefileConversionService;
  private final OutlineForMaximumCaseRepository outlineForMaximumCaseRepository;

  @Autowired
  OutlineForMaximumCasePersistenceService(SupportingDocumentService supportingDocumentService,
                                          ShapefileConversionService shapefileConversionService,
                                          OutlineForMaximumCaseRepository outlineForMaximumCaseRepository) {
    this.supportingDocumentService = supportingDocumentService;
    this.shapefileConversionService = shapefileConversionService;
    this.outlineForMaximumCaseRepository = outlineForMaximumCaseRepository;
  }

  @Transactional
  public void saveOutlineForMaximumCaseUploadDescriptions(OutlineForMaximumCaseUploadForm form) {
    supportingDocumentService.updateSupportingDocumentFileDescriptions(form.getOutlineForMaximumCase());
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
    supportingDocumentService.deleteFile(submissionDetail, uploadedShapefileZip.getUploadedFile().getId());
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

  private SupportingDocument getUploadedShapefileZip(SubmissionDetail submissionDetail) {
    var type = SupportingDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE;
    return supportingDocumentService.getSupportingDocumentsForType(submissionDetail, type)
        .orElseThrow(() -> new IllegalStateException("No shapefile found for submission detail %s and type %s"
            .formatted(submissionDetail.getId(), type.name())));
  }
}

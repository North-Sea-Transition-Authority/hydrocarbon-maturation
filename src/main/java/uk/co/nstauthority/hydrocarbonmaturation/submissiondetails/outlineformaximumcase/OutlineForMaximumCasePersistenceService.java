package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

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

  @Autowired
  OutlineForMaximumCasePersistenceService(SupportingDocumentService supportingDocumentService,
                                          ShapefileConversionService shapefileConversionService) {
    this.supportingDocumentService = supportingDocumentService;
    this.shapefileConversionService = shapefileConversionService;
  }

  @Transactional
  public void saveOutlineForMaximumCaseUploadDescriptions(OutlineForMaximumCaseUploadForm form) {
    supportingDocumentService.updateSupportingDocumentFileDescriptions(form.getOutlineForMaximumCase());
  }

  @Transactional
  public void deleteOutlineForMaximumCase(SubmissionDetail submissionDetail) {
    var uploadedShapefileZip = getUploadedShapefileZip(submissionDetail);
    supportingDocumentService.deleteFile(submissionDetail, uploadedShapefileZip.getUploadedFile().getId());
  }

  String getGeoJsonForOutlineForMaximumCase(SubmissionDetail submissionDetail) {
    var uploadedShapefileZip = getUploadedShapefileZip(submissionDetail);

    return shapefileConversionService.convertShapefileToGeoJson(uploadedShapefileZip);
  }

  private SupportingDocument getUploadedShapefileZip(SubmissionDetail submissionDetail) {
    var type = SupportingDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE;
    return supportingDocumentService.getSupportingDocumentsForType(submissionDetail, type)
        .orElseThrow(() -> new IllegalStateException("No shapefile found for submission detail %s and type %s"
            .formatted(submissionDetail.getId(), type.name())));
  }
}

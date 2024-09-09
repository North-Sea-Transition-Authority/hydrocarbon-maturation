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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.geojson.SubmissionGeoJsonService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;

@Service
public class OutlineForMaximumCasePersistenceService {

  private final SubmissionSupportingDocumentService supportingDocumentService;
  private final ShapefileConversionService shapefileConversionService;
  private final OutlineForMaximumCaseRepository outlineForMaximumCaseRepository;
  private final OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService;
  private final SubmissionGeoJsonService submissionGeoJsonService;

  @Autowired
  OutlineForMaximumCasePersistenceService(SubmissionSupportingDocumentService supportingDocumentService,
                                          ShapefileConversionService shapefileConversionService,
                                          OutlineForMaximumCaseRepository outlineForMaximumCaseRepository,
                                          OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService,
                                          SubmissionGeoJsonService submissionGeoJsonService) {
    this.supportingDocumentService = supportingDocumentService;
    this.shapefileConversionService = shapefileConversionService;
    this.outlineForMaximumCaseRepository = outlineForMaximumCaseRepository;
    this.outlineForMaximumCaseDocumentTypeService = outlineForMaximumCaseDocumentTypeService;
    this.submissionGeoJsonService = submissionGeoJsonService;
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
    submissionGeoJsonService.deleteGeojson(submissionDetail);
    supportingDocumentService.deleteFile(uploadedShapefileZip.getId());
    outlineForMaximumCaseRepository.deleteBySubmissionDetail(submissionDetail);
  }

  String getGeoJsonForOutlineForMaximumCase(SubmissionDetail submissionDetail) {
    var shapefileGeojson = submissionGeoJsonService.getShapefileGeoJsonForSubmissionDetail(submissionDetail)
        .orElseThrow(() -> new IllegalStateException(
            String.format("Could not find geoJson for submission detail %s", submissionDetail.getId())));
    return shapefileGeojson.getGeojson();
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
    var documentType = outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(submissionDetail);
    var uploadedFiles = supportingDocumentService.getUploadedFiles(submissionDetail, documentType);

    if (uploadedFiles.isEmpty()) {
      throw new IllegalStateException("No shapefile found for submission detail %s and type %s"
          .formatted(submissionDetail.getId(), documentType.name()));
    }

    if (uploadedFiles.size() > 1) {
      throw new IllegalStateException("Multiple shapefiles found for submission detail %s and type %s"
          .formatted(submissionDetail.getId(), documentType.name()));
    }

    return uploadedFiles.get(0);
  }

  @Transactional
  public void saveOutlineForMaximumCaseUploads(SubmissionDetail submissionDetail,
                                               Collection<UploadedFileForm> uploadedFileForms) {
    var documentType = outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(submissionDetail);
    supportingDocumentService.saveDocuments(submissionDetail, uploadedFileForms, documentType);
    storeShapefilesAsGeojson(submissionDetail, documentType);
  }

  @Transactional
  public void saveOutlineForMaximumCase(OutlineForMaximumCase outlineForMaximumCase) {
    outlineForMaximumCaseRepository.save(outlineForMaximumCase);
  }

  private void storeShapefilesAsGeojson(SubmissionDetail submissionDetail, FileDocumentType documentType) {
    submissionGeoJsonService.deleteGeojson(submissionDetail);
    supportingDocumentService.getUploadedFiles(submissionDetail, documentType)
        .forEach(uploadedFile -> {
          var geoJson = shapefileConversionService.convertShapefileToGeoJson(uploadedFile);
          submissionGeoJsonService.storeGeoJson(geoJson, submissionDetail);
        });
  }
}

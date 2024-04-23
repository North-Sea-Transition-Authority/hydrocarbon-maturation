package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.fivium.fileuploadlibrary.fds.UploadedFileForm;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.gis.GeoJsonShapefileLinkService;
import uk.co.nstauthority.hydrocarbonmaturation.gis.ShapefileConversionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;

@Service
public class OutlineForMaximumCasePersistenceService {

  private final SubmissionSupportingDocumentService supportingDocumentService;
  private final ShapefileConversionService shapefileConversionService;
  private final OutlineForMaximumCaseRepository outlineForMaximumCaseRepository;
  private final OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService;
  private final GeoJsonShapefileLinkService geoJsonShapefileLinkService;

  @Autowired
  OutlineForMaximumCasePersistenceService(SubmissionSupportingDocumentService supportingDocumentService,
                                          ShapefileConversionService shapefileConversionService,
                                          OutlineForMaximumCaseRepository outlineForMaximumCaseRepository,
                                          OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService,
                                          GeoJsonShapefileLinkService geoJsonShapefileLinkService) {
    this.supportingDocumentService = supportingDocumentService;
    this.shapefileConversionService = shapefileConversionService;
    this.outlineForMaximumCaseRepository = outlineForMaximumCaseRepository;
    this.outlineForMaximumCaseDocumentTypeService = outlineForMaximumCaseDocumentTypeService;
    this.geoJsonShapefileLinkService = geoJsonShapefileLinkService;
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
    geoJsonShapefileLinkService.deleteGeojson(uploadedShapefileZip);
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
                                               Collection<UploadedFileForm> uploadedFileForms,
                                               User user) {
    var documentType = outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(submissionDetail);
    supportingDocumentService.saveDocuments(submissionDetail, uploadedFileForms, documentType);
    storeShapefilesAsGeojson(submissionDetail, user, documentType);
  }

  private void storeShapefilesAsGeojson(SubmissionDetail submissionDetail, User user, FileDocumentType documentType) {
    supportingDocumentService.getUploadedFiles(submissionDetail, documentType)
        .forEach(uploadedFile -> {
          var geoJson = shapefileConversionService.convertShapefileToGeoJson(uploadedFile);
          try {
            geoJsonShapefileLinkService.storeGeoJson(uploadedFile, geoJson, user);
          } catch (IOException e) {
            throw new IllegalArgumentException("Failed to store shapefile %s as geojson".formatted(uploadedFile.getName()), e);
          }
        });
  }
}

package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionSectionDuplicationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.geojson.SubmissionGeoJson;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.geojson.SubmissionGeoJsonService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;

@Service
class OutlineForMaximumCaseDuplicationService implements SubmissionSectionDuplicationService {
  private final SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;
  private final SubmissionSupportingDocumentService supportingDocumentService;
  private final OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;
  private final OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService;
  private final SubmissionGeoJsonService submissionGeoJsonService;

  @Autowired
  OutlineForMaximumCaseDuplicationService(SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService,
                                          SubmissionSupportingDocumentService supportingDocumentService,
                                          OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService,
                                          OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService,
                                          SubmissionGeoJsonService submissionGeoJsonService) {
    this.submissionDuplicationPersistenceService = submissionDuplicationPersistenceService;
    this.supportingDocumentService = supportingDocumentService;
    this.outlineForMaximumCasePersistenceService = outlineForMaximumCasePersistenceService;
    this.outlineForMaximumCaseDocumentTypeService = outlineForMaximumCaseDocumentTypeService;
    this.submissionGeoJsonService = submissionGeoJsonService;
  }

  @Override
  @Transactional
  public void duplicateSubmissionSection(SubmissionDetail oldSubmissionDetail, SubmissionDetail newSubmissionDetail) {
    if (oldSubmissionDetail.isFieldSubmission()) {
      return;
    }

    var documentType = outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(oldSubmissionDetail);
    supportingDocumentService.copyUploadedFiles(
        oldSubmissionDetail,
        newSubmissionDetail,
        documentType
    );

    outlineForMaximumCasePersistenceService.getOutlineForMaximumCaseBySubmissionDetail(oldSubmissionDetail).ifPresent(
        outlineForMaximumCase -> submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
            newSubmissionDetail,
            outlineForMaximumCase,
            OutlineForMaximumCase.class
        )
    );

    submissionGeoJsonService.getShapefileGeoJsonForSubmissionDetail(oldSubmissionDetail).ifPresent(
        shapefileGeojson -> submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(
            newSubmissionDetail,
            shapefileGeojson,
            SubmissionGeoJson.class
        )
    );
  }



}

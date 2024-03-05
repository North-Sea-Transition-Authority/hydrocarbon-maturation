package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.resourcedetails.ResourceDetailsPersistenceService;

@Service
public class OutlineForMaximumCaseDocumentTypeService {

  private final ResourceDetailsPersistenceService resourceDetailsPersistenceService;

  @Autowired
  public OutlineForMaximumCaseDocumentTypeService(ResourceDetailsPersistenceService resourceDetailsPersistenceService) {
    this.resourceDetailsPersistenceService = resourceDetailsPersistenceService;
  }

  public FileDocumentType getOutlineForMaximumCaseDocumentType(SubmissionDetail submissionDetail) {
    var resourceDetail = resourceDetailsPersistenceService.getResourceDetailOrThrow(submissionDetail);

    return getOutlineForMaximumCaseDocumentType(resourceDetail);
  }

  FileDocumentType getOutlineForMaximumCaseDocumentType(ResourceDetail resourceDetail) {
    var onshoreOffshore = resourceDetail.getOnshoreOffshore();

    return switch (onshoreOffshore) {
      case ONSHORE -> FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_ONSHORE;
      case OFFSHORE -> FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_OFFSHORE;
    };
  }
}

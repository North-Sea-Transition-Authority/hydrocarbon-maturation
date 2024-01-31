package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.outlineformaximumcase;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.fivium.fileuploadlibrary.core.UploadedFile;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;

@ExtendWith(MockitoExtension.class)
class OutlineForMaximumCaseDuplicationServiceTest {

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;
  @Mock
  private SubmissionSupportingDocumentService supportingDocumentService;
  @Mock
  private OutlineForMaximumCasePersistenceService outlineForMaximumCasePersistenceService;
  @Mock
  private OutlineForMaximumCaseDocumentTypeService outlineForMaximumCaseDocumentTypeService;
  @InjectMocks
  private OutlineForMaximumCaseDuplicationService underTest;

  @Nested
  class WhenDuplicatingSubmissionSection {
    @Mock
    private SubmissionDetail oldSubmissionDetail;
    @Mock
    private SubmissionDetail newSubmissionDetail;
    @Mock
    private UploadedFile uploadedFile;
    @Mock
    private OutlineForMaximumCase outlineForMaximumCase;

    @Test
    void duplicateSupportingDocument() {
      var documentType = FileDocumentType.SHAPE_FILE_OUTLINE_FOR_MAXIMUM_CASE_OFFSHORE;
      when(outlineForMaximumCaseDocumentTypeService.getOutlineForMaximumCaseDocumentType(oldSubmissionDetail)).thenReturn(documentType);
      underTest.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

      verify(supportingDocumentService).copyUploadedFiles(oldSubmissionDetail, newSubmissionDetail, documentType);
    }

    @Test
    void duplicateOutlineForMaximumCase() {
      when(outlineForMaximumCasePersistenceService.getOutlineForMaximumCaseBySubmissionDetail(oldSubmissionDetail))
          .thenReturn(Optional.of(outlineForMaximumCase));

      underTest.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

      verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, outlineForMaximumCase,
          OutlineForMaximumCase.class);
    }
  }
}
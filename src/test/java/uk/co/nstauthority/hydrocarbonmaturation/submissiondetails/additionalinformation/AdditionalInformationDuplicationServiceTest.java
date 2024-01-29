package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;

@ExtendWith(MockitoExtension.class)
class AdditionalInformationDuplicationServiceTest {

  @Mock
  private AdditionalInformationPersistenceService additionalInformationPersistenceService;

  @Mock
  private SubmissionSupportingDocumentService supportingDocumentService;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @InjectMocks
  private AdditionalInformationDuplicationService additionalInformationDuplicationService;

  @Test
  void duplicateSubmissionSection_AdditionalInformationExists_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    var additionalInformation = mock(AdditionalInformation.class);
    when(additionalInformationPersistenceService.getAdditionalInformation(oldSubmissionDetail))
        .thenReturn(Optional.of(additionalInformation));

    additionalInformationDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, additionalInformation,
        AdditionalInformation.class);
  }

  @Test
  void duplicateSubmissionSection_AdditionalInformationDoesNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    when(additionalInformationPersistenceService.getAdditionalInformation(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    additionalInformationDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(AdditionalInformation.class), eq(AdditionalInformation.class));
  }

  @Test
  void duplicateSubmissionSection_SupportingDocumentsExist_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    additionalInformationDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(supportingDocumentService).copyUploadedFiles(oldSubmissionDetail, newSubmissionDetail, FileDocumentType.ADDITIONAL_DOCUMENT);
  }
}
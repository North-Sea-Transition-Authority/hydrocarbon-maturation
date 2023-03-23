package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.additionalinformation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocument;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentType;

@ExtendWith(MockitoExtension.class)
class AdditionalInformationDuplicationServiceTest {

  @Mock
  private AdditionalInformationPersistenceService additionalInformationPersistenceService;

  @Mock
  private SupportingDocumentService supportingDocumentService;

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

    var supportingDocument1 = mock(SupportingDocument.class);
    var supportingDocument2 = mock(SupportingDocument.class);
    when(supportingDocumentService.getSupportingDocumentsForTypes(oldSubmissionDetail, Set.of(SupportingDocumentType.ADDITIONAL_DOCUMENT)))
        .thenReturn(List.of(supportingDocument1, supportingDocument2));

    additionalInformationDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, supportingDocument1,
        SupportingDocument.class);
    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, supportingDocument2,
        SupportingDocument.class);
  }

  @Test
  void duplicateSubmissionSection_SupportingDocumentsDoNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    when(supportingDocumentService.getSupportingDocumentsForTypes(oldSubmissionDetail, Set.of(SupportingDocumentType.ADDITIONAL_DOCUMENT)))
        .thenReturn(Collections.emptyList());

    additionalInformationDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(SupportingDocument.class), eq(SupportingDocument.class));
  }
}
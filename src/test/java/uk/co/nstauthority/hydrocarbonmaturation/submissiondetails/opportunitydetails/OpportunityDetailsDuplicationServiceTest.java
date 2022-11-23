package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
class OpportunityDetailsDuplicationServiceTest {

  @Mock
  private OpportunityDetailsPersistenceService opportunityDetailPersistenceService;

  @Mock
  private SupportingDocumentService supportingDocumentService;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @InjectMocks
  private OpportunityDetailsDuplicationService opportunityDetailDuplicationService;

  @Test
  void duplicateSubmissionSection_OpportunityDetailExists_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    var opportunityDetail = mock(OpportunityDetail.class);
    when(opportunityDetailPersistenceService.getOpportunityDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(opportunityDetail));

    opportunityDetailDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, opportunityDetail,
        OpportunityDetail.class);
  }

  @Test
  void duplicateSubmissionSection_OpportunityDetailDoesNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    when(opportunityDetailPersistenceService.getOpportunityDetail(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    opportunityDetailDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(OpportunityDetail.class), eq(OpportunityDetail.class));
  }

  @Test
  void duplicateSubmissionSection_SupportingDocumentsExist_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    var supportingDocument1 = mock(SupportingDocument.class);
    var supportingDocument2 = mock(SupportingDocument.class);
    when(supportingDocumentService.getSupportingDocumentsForType(oldSubmissionDetail, SupportingDocumentType.MAP_OF_OPPORTUNITY))
        .thenReturn(List.of(supportingDocument1, supportingDocument2));

    opportunityDetailDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, supportingDocument1,
        SupportingDocument.class);
    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, supportingDocument2,
        SupportingDocument.class);
  }

  @Test
  void duplicateSubmissionSection_SupportingDocumentsDoNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    when(supportingDocumentService.getSupportingDocumentsForType(oldSubmissionDetail, SupportingDocumentType.MAP_OF_OPPORTUNITY))
        .thenReturn(Collections.emptyList());

    opportunityDetailDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(SupportingDocument.class), eq(SupportingDocument.class));
  }
}
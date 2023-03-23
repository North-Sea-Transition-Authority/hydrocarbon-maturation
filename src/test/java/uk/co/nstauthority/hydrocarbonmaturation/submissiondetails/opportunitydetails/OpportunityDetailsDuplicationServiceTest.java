package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
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
  private EntityManager entityManager;

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
  void duplicateSubmissionSection_whenDiscovery_OpportunityDetailExists_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = new SubmissionDetail();
    newSubmissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY);

    var opportunityDetail = new OpportunityDetail();
    when(opportunityDetailPersistenceService.getOpportunityDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(opportunityDetail));
    when(submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, opportunityDetail,
        OpportunityDetail.class)).thenReturn(opportunityDetail);
    opportunityDetailDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    assertThat(opportunityDetail.getOpportunityType())
        .isEqualTo(OpportunityType.DISCOVERY);
    verify(entityManager, times(1)).persist(opportunityDetail);
  }

  @Test
  void duplicateSubmissionSection_whenDryWell_OpportunityDetailExists_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = new SubmissionDetail();
    newSubmissionDetail.setSubmissionType(SubmissionType.POST_DRILL_DRY_HOLE);

    var opportunityDetail = new OpportunityDetail();
    when(opportunityDetailPersistenceService.getOpportunityDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(opportunityDetail));
    when(submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, opportunityDetail,
        OpportunityDetail.class)).thenReturn(opportunityDetail);
    opportunityDetailDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    assertThat(opportunityDetail.getOpportunityType())
        .isEqualTo(OpportunityType.DRY_WELL);
    verify(entityManager, times(1)).persist(opportunityDetail);
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
    var supportingDocument3 = mock(SupportingDocument.class);
    when(supportingDocumentService.getSupportingDocumentsForTypes(oldSubmissionDetail,
        Set.of(SupportingDocumentType.MAP_OF_OPPORTUNITY, SupportingDocumentType.REPRESENTATIVE_SEISMIC_SECTION, SupportingDocumentType.GEOLOGICAL_CROSS_SECTION)
    )).thenReturn(List.of(supportingDocument1, supportingDocument2, supportingDocument3));

    opportunityDetailDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, supportingDocument1,
        SupportingDocument.class);
    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, supportingDocument2,
        SupportingDocument.class);
    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, supportingDocument3,
        SupportingDocument.class);
  }

  @Test
  void duplicateSubmissionSection_SupportingDocumentsDoNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    when(supportingDocumentService.getSupportingDocumentsForTypes(oldSubmissionDetail,
        Set.of(SupportingDocumentType.MAP_OF_OPPORTUNITY, SupportingDocumentType.REPRESENTATIVE_SEISMIC_SECTION, SupportingDocumentType.GEOLOGICAL_CROSS_SECTION)
    )).thenReturn(Collections.emptyList());

    opportunityDetailDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(SupportingDocument.class), eq(SupportingDocument.class));
  }
}
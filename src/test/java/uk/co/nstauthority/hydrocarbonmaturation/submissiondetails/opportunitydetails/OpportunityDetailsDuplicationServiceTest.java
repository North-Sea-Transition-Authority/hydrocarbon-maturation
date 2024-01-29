package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;

@ExtendWith(MockitoExtension.class)
class OpportunityDetailsDuplicationServiceTest {

  @Mock
  private OpportunityDetailsPersistenceService opportunityDetailPersistenceService;

  @Mock
  private SubmissionSupportingDocumentService supportingDocumentService;

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
  void duplicateSubmissionSection_UploadedFilesExist_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    opportunityDetailDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(supportingDocumentService).copyUploadedFiles(oldSubmissionDetail, newSubmissionDetail, FileDocumentType.MAP_OF_OPPORTUNITY);
    verify(supportingDocumentService).copyUploadedFiles(oldSubmissionDetail, newSubmissionDetail, FileDocumentType.REPRESENTATIVE_SEISMIC_SECTION);
    verify(supportingDocumentService).copyUploadedFiles(oldSubmissionDetail, newSubmissionDetail, FileDocumentType.GEOLOGICAL_CROSS_SECTION);
  }
}
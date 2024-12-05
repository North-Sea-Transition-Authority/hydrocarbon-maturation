package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

@ExtendWith(MockitoExtension.class)
class GeneralDetailsDuplicationServiceTest {
  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @Mock
  private QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;

  @Mock
  private LicencePersistenceService licencePersistenceService;

  @Mock
  private EntityManager entityManager;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @Mock
  private SubmissionSupportingDocumentService supportingDocumentService;

  @InjectMocks
  private GeneralDetailsDuplicationService generalDetailsDuplicationService;

  @Test
  void duplicateSubmissionSection_GeneralDetailExists_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();
    var newGeneralDetail = new GeneralDetail();
    var generalDetail = mock(GeneralDetail.class);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(generalDetail));
    when(submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, generalDetail,
        GeneralDetail.class))
        .thenReturn(newGeneralDetail);
    generalDetailsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, generalDetail,
        GeneralDetail.class);
    verify(entityManager, times(1))
        .persist(newGeneralDetail);
  }


  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"PROSPECT_UPDATE", "UPDATE_DISCOVERY", "CREATE_DISCOVERY_FROM_PROSPECT"},
      mode = EnumSource.Mode.INCLUDE)
  void duplicateSubmissionSection_sourceOfInformationAndLicenceAppDoesNotCopyForward_DoesDuplicate(
      SubmissionType submissionType) {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);
    var generalDetail = new GeneralDetail();
    generalDetail.setInformationSource(InformationSource.OTHER);
    generalDetail.setOtherInformationSource("other info");
    generalDetail.setLicenceApplicationId(1);
    generalDetail.setLicenceApplicationReference("ref");
    generalDetail.setLicenceApplicationInfoSource("source");
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(generalDetail));
    when(submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, generalDetail,
        GeneralDetail.class))
        .thenReturn(generalDetail);
    generalDetailsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, generalDetail,
        GeneralDetail.class);
    verify(entityManager, times(1))
        .persist(generalDetail);
    assertThat(generalDetail)
        .extracting(
            GeneralDetail::getInformationSource,
            GeneralDetail::getOtherInformationSource,
            GeneralDetail::getLicenceApplicationInfoSource,
            GeneralDetail::getLicenceApplicationId,
            GeneralDetail::getLicenceApplicationReference)
        .containsOnlyNulls();
  }

  @Test
  void duplicateSubmissionSection_GeneralDetailDoesNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);

    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    generalDetailsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(GeneralDetail.class), eq(GeneralDetail.class));
  }

  @Test
  void duplicateSubmissionSection_QuadrantBlocksExist_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);
    var generalDetail = mock(GeneralDetail.class);
    var newGeneralDetail = mock(GeneralDetail.class);
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(generalDetail));

    var quadrantAndBlock1 = mock(QuadrantAndBlock.class);
    var quadrantAndBlock2 = mock(QuadrantAndBlock.class);
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail))
        .thenReturn(List.of(quadrantAndBlock1, quadrantAndBlock2));

    when(submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, generalDetail,
        GeneralDetail.class)).thenReturn(newGeneralDetail);

    generalDetailsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newGeneralDetail, quadrantAndBlock1, QuadrantAndBlock.class);
    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newGeneralDetail, quadrantAndBlock2, QuadrantAndBlock.class);
  }

  @Test
  void duplicateSubmissionSection_QuadrantBlocksDoNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    var newSubmissionDetail = mock(SubmissionDetail.class);
    var generalDetail = mock(GeneralDetail.class);
    var newGeneralDetail = new GeneralDetail();
    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(generalDetail));

    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(generalDetail))
        .thenReturn(Collections.emptyList());
    when(submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, generalDetail,
        GeneralDetail.class))
        .thenReturn(newGeneralDetail);
    generalDetailsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(generalDetail), any(QuadrantAndBlock.class), eq(QuadrantAndBlock.class));
  }

  @ParameterizedTest
  @EnumSource(SubmissionType.class)
  void duplicateSubmissionSection_sameSubmissionType_DoesDuplicate_andCopiesForward(SubmissionType submissionType) {
    var oldSubmissionDetail = new SubmissionDetail();
    oldSubmissionDetail.setSubmissionType(submissionType);
    var newSubmissionDetail = new SubmissionDetail();
    newSubmissionDetail.setSubmissionType(submissionType);
    newSubmissionDetail.setVersionNumber(1);
    var oldGeneralDetail = new GeneralDetail();
    var newGeneralDetail = mock(GeneralDetail.class);

    var newResource = new Resource();
    var newSubmission = new Submission();
    newSubmission.setResource(newResource);
    newSubmissionDetail.setSubmission(newSubmission);

    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(oldGeneralDetail));
    when(quadrantAndBlockPersistenceService.getQuadrantBlocksByGeneralDetail(oldGeneralDetail))
        .thenReturn(Collections.emptyList());
    when(submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(newSubmissionDetail, oldGeneralDetail,
        GeneralDetail.class))
        .thenReturn(newGeneralDetail);
    generalDetailsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(oldGeneralDetail), any(QuadrantAndBlock.class), eq(QuadrantAndBlock.class));
  }

  @Test
  void duplicateSubmissionSection_fieldSubmission_isCorrection_verifyFieldReportCopied() {
    var submissionType = SubmissionType.CREATE_CSR;

    var oldSubmissionDetail = new SubmissionDetail();
    oldSubmissionDetail.setSubmissionType(submissionType);
    var newSubmissionDetail = new SubmissionDetail();
    newSubmissionDetail.setSubmissionType(submissionType);
    newSubmissionDetail.setCorrection(true);

    var newResource = new Resource();
    var newSubmission = new Submission();
    newSubmission.setResource(newResource);
    newSubmissionDetail.setSubmission(newSubmission);

    var oldGeneralDetail = new GeneralDetail();

    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(oldGeneralDetail));

    when(submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(any(SubmissionDetail.class), any(), any()))
            .thenReturn(new GeneralDetail());

    generalDetailsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(supportingDocumentService).copyUploadedFiles(oldSubmissionDetail, newSubmissionDetail, FileDocumentType.FIELD_REPORT);
  }

  @Test
  void duplicateSubmissionSection_fieldSubmission_isUpdate_verifyFieldReportCopied() {
    var submissionType = SubmissionType.CREATE_CSR;

    var oldSubmissionDetail = new SubmissionDetail();
    oldSubmissionDetail.setSubmissionType(submissionType);
    var newSubmissionDetail = new SubmissionDetail();
    newSubmissionDetail.setSubmissionType(submissionType);
    newSubmissionDetail.setCorrection(false);
    newSubmissionDetail.setVersionNumber(2);

    var newResource = new Resource();
    var newSubmission = new Submission();
    newSubmission.setResource(newResource);
    newSubmissionDetail.setSubmission(newSubmission);

    var oldGeneralDetail = new GeneralDetail();
    var newGeneralDetail = new GeneralDetail();
    newGeneralDetail.setProspectDiscoveryLocatedInUkcs(true);

    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(oldGeneralDetail));

    when(submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(any(SubmissionDetail.class), any(), any()))
            .thenReturn(newGeneralDetail);

    generalDetailsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(supportingDocumentService).copyUploadedFiles(oldSubmissionDetail, newSubmissionDetail, FileDocumentType.FIELD_REPORT);
    verify(entityManager, times(1)).persist(newGeneralDetail);

    assertThat(newGeneralDetail)
            .extracting(
                    GeneralDetail::getProspectDiscoveryLocatedInUkcs,
                    GeneralDetail::getProspectDiscoveryLocatedInEngland
            )
            .containsOnlyNulls();
  }

  @Test
  void duplicateSubmissionSection_fieldSubmission_isNotCorrectionOrUpdate_verifyNoFieldReportCopied() {
    var submissionType = SubmissionType.CREATE_CSR;

    var oldSubmissionDetail = new SubmissionDetail();
    oldSubmissionDetail.setSubmissionType(submissionType);
    var newSubmissionDetail = new SubmissionDetail();
    newSubmissionDetail.setSubmissionType(submissionType);
    newSubmissionDetail.setCorrection(false);
    newSubmissionDetail.setVersionNumber(1);

    var newResource = new Resource();
    var newSubmission = new Submission();
    newSubmission.setResource(newResource);
    newSubmissionDetail.setSubmission(newSubmission);

    var oldGeneralDetail = new GeneralDetail();

    when(generalDetailsPersistenceService.getGeneralDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(oldGeneralDetail));

    when(submissionDuplicationPersistenceService.duplicateChildAndReplaceParent(any(SubmissionDetail.class), any(), any()))
            .thenReturn(new GeneralDetail());

    generalDetailsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verifyNoInteractions(supportingDocumentService);
  }
}
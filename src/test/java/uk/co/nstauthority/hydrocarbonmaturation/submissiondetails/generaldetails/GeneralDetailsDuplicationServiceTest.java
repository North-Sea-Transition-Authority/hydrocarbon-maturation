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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDuplicationPersistenceService;

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
    var oldGeneralDetail = new GeneralDetail();
    var newGeneralDetail = mock(GeneralDetail.class);

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

    verifyNoInteractions(newGeneralDetail);
  }
}
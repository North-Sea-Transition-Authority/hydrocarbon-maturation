package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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
class ReservoirAdditionsDuplicationServiceTest {
  @Mock
  private ReservoirAdditionsRepository reservoirAdditionsRepository;
  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;
  @InjectMocks
  private ReservoirAdditionsDuplicationService reservoirAdditionsDuplicationService;

  private SubmissionDetail oldSubmissionDetail;
  private SubmissionDetail newSubmissionDetail;

  @BeforeEach
  void setUp() {
    // start with assumption they are not updates
    oldSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY);
    newSubmissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_DISCOVERY);

    newSubmissionDetail.setVersionNumber(1);
  }

  @Test
  void duplicateSubmissionSection_firstVersion_thenDoesNotDuplicate() {
    newSubmissionDetail.setVersionNumber(1);
    reservoirAdditionsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verifyNoInteractions(reservoirAdditionsRepository);
    verifyNoInteractions(submissionDuplicationPersistenceService);
  }

  @Test
  void duplicateSubmissionSection_versionGreaterThan1_thenDoesDuplicate() {
    newSubmissionDetail.setVersionNumber(2);

    var reservoirAdditions = new ReservoirAdditions();
    when(reservoirAdditionsRepository.findBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(reservoirAdditions));

    reservoirAdditionsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService)
        .duplicateChildAndReplaceParent(newSubmissionDetail, reservoirAdditions, ReservoirAdditions.class);
    verifyNoMoreInteractions(submissionDuplicationPersistenceService);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"PROSPECT_UPDATE", "UPDATE_DISCOVERY"},
      mode = EnumSource.Mode.EXCLUDE
  )
  void duplicateSubmissionSection_nonUpdateType_thenDoesNotDuplicate(SubmissionType submissionType) {
    newSubmissionDetail.setSubmissionType(submissionType);
    reservoirAdditionsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verifyNoInteractions(reservoirAdditionsRepository);
    verifyNoInteractions(submissionDuplicationPersistenceService);
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"PROSPECT_UPDATE", "UPDATE_DISCOVERY"},
      mode = EnumSource.Mode.INCLUDE
  )
  void duplicateSubmissionSection_updateType_thenDoesDuplicate(SubmissionType submissionType) {
    newSubmissionDetail.setSubmissionType(submissionType);

    var reservoirAdditions = mock(ReservoirAdditions.class);
    when(reservoirAdditionsRepository.findBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(reservoirAdditions));

    reservoirAdditionsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService)
        .duplicateChildAndReplaceParent(newSubmissionDetail, reservoirAdditions, ReservoirAdditions.class);
    verifyNoMoreInteractions(submissionDuplicationPersistenceService);
  }
}
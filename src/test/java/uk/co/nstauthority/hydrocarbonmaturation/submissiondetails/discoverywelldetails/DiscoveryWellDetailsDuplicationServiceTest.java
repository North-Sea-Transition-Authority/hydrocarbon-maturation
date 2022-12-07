package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
class DiscoveryWellDetailsDuplicationServiceTest {

  @Mock
  private DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  @Mock
  private SubmissionDuplicationPersistenceService submissionDuplicationPersistenceService;

  @InjectMocks
  private DiscoveryWellDetailsDuplicationService discoveryWellDetailsDuplicationService;

  private SubmissionDetail newSubmissionDetail;

  @BeforeEach
  void setUp() {
    newSubmissionDetail = SubmissionDetailTestUtils.createNewDiscoveryUpdateSubmissionDetail();
  }

  @Test
  void duplicateSubmissionSection_DiscoveryWellDetailsExists_DoesDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);

    var discoveryWellDetails = mock(DiscoveryWellDetail.class);
    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.of(discoveryWellDetails));

    discoveryWellDetailsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService).duplicateChildAndReplaceParent(newSubmissionDetail, discoveryWellDetails,
        DiscoveryWellDetail.class);
  }

  @Test
  void duplicateSubmissionSection_DiscoveryWellDetailsDoesNotExist_DoesNotDuplicate() {
    var oldSubmissionDetail = mock(SubmissionDetail.class);

    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(oldSubmissionDetail))
        .thenReturn(Optional.empty());

    discoveryWellDetailsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(DiscoveryWellDetail.class), eq(DiscoveryWellDetail.class));
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = {"UPDATE_DISCOVERY"}, mode = EnumSource.Mode.EXCLUDE)
  void duplicateSubmissionSection_DiscoveryWellDetailsDoesNotExist_DoesNotDuplicate(SubmissionType submissionType) {
    var oldSubmissionDetail = mock(SubmissionDetail.class);
    newSubmissionDetail.setSubmissionType(submissionType);

    discoveryWellDetailsDuplicationService.duplicateSubmissionSection(oldSubmissionDetail, newSubmissionDetail);

    verify(submissionDuplicationPersistenceService, never())
        .duplicateChildAndReplaceParent(eq(newSubmissionDetail), any(DiscoveryWellDetail.class), eq(DiscoveryWellDetail.class));
  }
}
package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsPersistenceService;

@ExtendWith(MockitoExtension.class)
class SubmissionDetailServiceTest {
  @Mock
  private SubmissionDetailRepository submissionDetailRepository;
  @Mock
  private GeneralDetailsPersistenceService generalDetailsPersistenceService;

  @InjectMocks
  private SubmissionDetailService submissionDetailService;

  private final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();

  @Test
  void getLatestApprovedDiscoverySubmissionDetailForResource_assertReturn() {
    var resource = new Resource();
    when(submissionDetailRepository.findFirstByResourceIdAndSubmissionTypeInAndStatusOrderByApprovedTimestampDesc(resource,
        List.of(
            SubmissionType.CREATE_DISCOVERY,
            SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT,
            SubmissionType.UPDATE_DISCOVERY),
        SubmissionDetailStatus.APPROVED))
        .thenReturn(Optional.of(submissionDetail));

    assertThat(submissionDetailService.getLatestApprovedDiscoverySubmissionDetailForResource(resource))
        .isEqualTo(Optional.of(submissionDetail));
  }

  @Test
  void getSubmissionDetailsForResource_verifyMethodCall() {
    var resource = new Resource();

    submissionDetailService.getSubmissionDetailsForResource(resource);

    verify(submissionDetailRepository, times(1)).findSubmissionDetailsByResourceId(resource);
  }

  @Test
  void getLatestApprovedProspectSubmissionForResource_assertSubmissionDetailFound() {
    var resource = new Resource();
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectUpdateSubmissionDetail();
    when(submissionDetailRepository
        .findFirstByResourceIdAndSubmissionTypeInAndStatusOrderByApprovedTimestampDesc(resource,
            List.of(SubmissionType.PROSPECT_UPDATE, SubmissionType.CREATE_PROSPECT),
            SubmissionDetailStatus.APPROVED))
        .thenReturn(Optional.of(submissionDetail));

    var result = submissionDetailService.getLatestApprovedProspectSubmissionForResource(resource);

    assertThat(result).isEqualTo(Optional.of(submissionDetail));
  }

  @Test
  void getApprovedOrSubmittedSubmissionDetailByResource() {
    var resource = new Resource();
    var submissionDetail = new SubmissionDetail();
    when(submissionDetailRepository.findFirstByResourceIdAndStatusIn(
        resource,
        List.of(SubmissionDetailStatus.APPROVED, SubmissionDetailStatus.SUBMITTED)
    )).thenReturn(Optional.of(submissionDetail));

    assertThat(submissionDetailService.getApprovedOrSubmittedSubmissionDetailByResource(resource))
        .contains(submissionDetail);
  }
}

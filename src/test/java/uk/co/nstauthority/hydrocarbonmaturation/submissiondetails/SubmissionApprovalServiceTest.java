package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.teams.permissionmanagement.regulator.RegulatorTeamService;

@ExtendWith(MockitoExtension.class)
class SubmissionApprovalServiceTest {

  private static final ServiceUserDetail USER = ServiceUserDetailTestUtil.Builder().build();

  @Mock
  private SubmissionDetailPersistenceService submissionDetailPersistenceService;
  @Mock
  private RegulatorTeamService regulatorTeamService;
  @Mock
  private UserDetailService userDetailService;
  @InjectMocks
  private SubmissionApprovalService underTest;

  @Nested
  class WhenSubmittingSubmission {
    @Mock
    private SubmissionDetail submissionDetail;

    @BeforeEach
    void setup() {
      when(userDetailService.getUserDetail()).thenReturn(USER);
    }

    @Test
    void andCanNotAutoApprove_ThenVerifyCallToSubmit() {
      when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(false);

      underTest.submitSubmission(submissionDetail);

      verify(submissionDetailPersistenceService).submitSubmission(submissionDetail);
    }

    @Test
    void andCanAutoApprove_ThenVerifyCallsToSubmitAndApprove() {
      when(regulatorTeamService.isRegulatorUser(USER)).thenReturn(true);

      underTest.submitSubmission(submissionDetail);

      verify(submissionDetailPersistenceService).submitSubmission(submissionDetail);
      verify(submissionDetailPersistenceService).approveSubmission(submissionDetail);
    }
  }
}
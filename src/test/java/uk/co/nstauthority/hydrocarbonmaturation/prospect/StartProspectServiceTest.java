package uk.co.nstauthority.hydrocarbonmaturation.prospect;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionCreateService;

@ExtendWith(MockitoExtension.class)
class StartProspectServiceTest {

  private static SubmissionCreateService submissionCreateService;
  private static StartProspectService startProspectService;

  @BeforeAll
  static void setup() {
    submissionCreateService = mock(SubmissionCreateService.class);
    startProspectService = new StartProspectService(submissionCreateService);
  }

  @Test
  void createInitialProspect_verifyMethodCall() {
    startProspectService.createInitialProspect();
    verify(submissionCreateService, times(1)).createInitialProspect();
  }
}
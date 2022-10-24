package uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class ProbabilityDistributionPersistenceServiceTest {

  @Mock
  private ProbabilityDistributionRepository probabilityDistributionRepository;

  @InjectMocks
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Test
  void deleteProbabilityDistribution_verifyMethodCall() {
    var probabilityDistributionFieldInstance = ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA;
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    probabilityDistributionPersistenceService.deleteProbabilityDistribution(probabilityDistributionFieldInstance, submissionDetail);

    verify(probabilityDistributionRepository, times(1)).deleteBySubmissionDetailAndInstance(submissionDetail, probabilityDistributionFieldInstance);
  }
}
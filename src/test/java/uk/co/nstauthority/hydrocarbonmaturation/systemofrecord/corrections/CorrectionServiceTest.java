package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@ExtendWith(MockitoExtension.class)
class CorrectionServiceTest {

  @Mock
  private SubmissionDetailRepository submissionDetailRepository;

  private CorrectionService correctionService;

  @BeforeEach
  void setUp() {
    correctionService = new CorrectionService(submissionDetailRepository);
  }

  @Test
  void canStartCorrection() {
    var resource = new Resource();
    when(submissionDetailRepository.existsSubmissionDetailByCorrectionTrueAndStatusInAndSubmission_Resource(
        Set.of(SubmissionDetailStatus.DRAFT),
        resource
    )).thenReturn(false);
    var isInResponsibleTeam = true;
    assertTrue(correctionService.canStartCorrection(resource, isInResponsibleTeam));
  }

  @Test
  void cantStartCorrection_whenNotInResponsibleTeam() {
    var resource = new Resource();
    var isInResponsibleTeam = false;
    assertFalse(correctionService.canStartCorrection(resource, isInResponsibleTeam));
  }

  @Test
  void cantStartCorrection_whenDraftCorrectionExists() {
    var resource = new Resource();
    when(submissionDetailRepository.existsSubmissionDetailByCorrectionTrueAndStatusInAndSubmission_Resource(
        Set.of(SubmissionDetailStatus.DRAFT),
        resource
    )).thenReturn(true);
    var isInResponsibleTeam = true;
    assertFalse(correctionService.canStartCorrection(resource, isInResponsibleTeam));
  }
}

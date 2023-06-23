package uk.co.nstauthority.hydrocarbonmaturation.systemofrecord.corrections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetail;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.ServiceUserDetailTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserDetailService;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformation;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.ResponsibleTeamType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailRepository;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailStatus;

@ExtendWith(MockitoExtension.class)
class CorrectionServiceTest {

  @Mock
  private SubmissionDetailRepository submissionDetailRepository;

  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @Mock
  private UserDetailService userDetailService;

  @Mock
  private ResponsibleTeamService responsibleTeamService;

  private CorrectionService correctionService;

  private Resource resource;
  private ServiceUserDetail user;

  @BeforeEach
  void setUp() {
    correctionService = new CorrectionService(
        regulatorManagedInformationPersistenceService,
        userDetailService,
        responsibleTeamService,
        submissionDetailRepository);

    resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    user = ServiceUserDetailTestUtil.Builder().build();
  }

  @Test
  void canStartCorrection() {
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource)).thenReturn(
        Optional.of(regulatorManagedInformation)
    );
    when(userDetailService.getUserDetail()).thenReturn(user);
    when(responsibleTeamService.isInResponsibleTeam(user, regulatorManagedInformation.getResponsibleTeamType())).thenReturn(true);
    when(submissionDetailRepository.existsSubmissionDetailByCorrectionTrueAndStatusInAndSubmission_Resource(
        Set.of(SubmissionDetailStatus.DRAFT),
        resource
    )).thenReturn(false);
    assertTrue(correctionService.canStartCorrection(resource));
  }

  @Test
  void cantStartCorrect_whenNoRegulatorManagedInfo() {
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource)).thenReturn(
        Optional.empty()
    );

    assertFalse(correctionService.canStartCorrection(resource));
  }

  @Test
  void cantStartCorrection_whenNoResponsibleTeam() {
    var regulatorManagedInformation = new RegulatorManagedInformation();
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource)).thenReturn(
        Optional.of(regulatorManagedInformation)
    );
    assertFalse(correctionService.canStartCorrection(resource));
  }

  @Test
  void cantStartCorrection_whenNotInResponsibleTeam() {
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource)).thenReturn(
        Optional.of(regulatorManagedInformation)
    );
    when(userDetailService.getUserDetail()).thenReturn(user);
    when(responsibleTeamService.isInResponsibleTeam(user, regulatorManagedInformation.getResponsibleTeamType())).thenReturn(false);
    assertFalse(correctionService.canStartCorrection(resource));
  }

  @Test
  void cantStartCorrection_whenDraftCorrectionExists() {
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(ResponsibleTeamType.EXPLORATION);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResource(resource)).thenReturn(
        Optional.of(regulatorManagedInformation)
    );
    when(userDetailService.getUserDetail()).thenReturn(user);
    when(responsibleTeamService.isInResponsibleTeam(user, regulatorManagedInformation.getResponsibleTeamType())).thenReturn(true);
    when(submissionDetailRepository.existsSubmissionDetailByCorrectionTrueAndStatusInAndSubmission_Resource(
        Set.of(SubmissionDetailStatus.DRAFT),
        resource
    )).thenReturn(true);
    assertFalse(correctionService.canStartCorrection(resource));
  }
}

package uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDto;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.user.EnergyPortalUserDtoTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.northernoffshore.NorthernOffshoreAreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.onshore.OnshoreAreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.area.southernoffshore.SouthernOffshoreAreaTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator.RegulatorTeamRole;
import uk.co.nstauthority.hydrocarbonmaturation.legacyteams.permissionmanagement.regulator.RegulatorTeamService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissions.Submission;

@ExtendWith(MockitoExtension.class)
class ResponsibleLegacyTeamServiceTest {

  @Mock
  private RegulatorTeamService regulatorTeamService;

  @Mock
  private NorthernOffshoreAreaTeamService northernOffshoreAreaTeamService;

  @Mock
  private SouthernOffshoreAreaTeamService southernOffshoreAreaTeamService;

  @Mock
  private OnshoreAreaTeamService onshoreAreaTeamService;

  @Mock
  private RegulatorManagedInformationPersistenceService regulatorManagedInformationPersistenceService;

  @InjectMocks
  private ResponsibleTeamService responsibleTeamService;

  private final User user = UserTestUtil.Builder().build();
  private final EnergyPortalUserDto energyPortalUserDto = EnergyPortalUserDtoTestUtil.Builder().build();

  @ParameterizedTest
  @EnumSource(ResponsibleTeamType.class)
  void isInResponsibleTeam_takingSubmissionDetails(ResponsibleTeamType responsibleTeamType) {

    var resource = mock(Resource.class);
    var submission = new Submission();
    submission.setResource(resource);
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setSubmission(submission);
    var regulatorManagedInformation = new RegulatorManagedInformation();
    regulatorManagedInformation.setResponsibleTeamType(responsibleTeamType);
    when(regulatorManagedInformationPersistenceService.getRegulatorManagedInformationByResourceOrThrow(resource))
        .thenReturn(regulatorManagedInformation);

    switch (responsibleTeamType) {
      case EXPLORATION -> when(regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.EXPLORATION))
          .thenReturn(true);
      case NORTHERN_NORTH_SEA_WEST_OF_SHETLAND -> when(northernOffshoreAreaTeamService
          .isNorthernOffshoreTeamMember(user))
          .thenReturn(true);
      case CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA -> when(southernOffshoreAreaTeamService
          .isSouthernOffshoreTeamMember(user))
          .thenReturn(true);
      case ONSHORE -> when(onshoreAreaTeamService
          .isOnshoreTeamMember(user))
          .thenReturn(true);
    }

    assertThat(responsibleTeamService.isInResponsibleTeam(user, submissionDetail)).isTrue();
  }

  @ParameterizedTest
  @EnumSource(ResponsibleTeamType.class)
  void isInResponsibleTeam(ResponsibleTeamType responsibleTeamType) {

    switch (responsibleTeamType) {
      case EXPLORATION -> when(regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.EXPLORATION))
          .thenReturn(true);
      case NORTHERN_NORTH_SEA_WEST_OF_SHETLAND -> when(northernOffshoreAreaTeamService
          .isNorthernOffshoreTeamMember(user))
          .thenReturn(true);
      case CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA -> when(southernOffshoreAreaTeamService
          .isSouthernOffshoreTeamMember(user))
          .thenReturn(true);
      case ONSHORE -> when(onshoreAreaTeamService
          .isOnshoreTeamMember(user))
          .thenReturn(true);
    }

    assertThat(responsibleTeamService.isInResponsibleTeam(user, responsibleTeamType)).isTrue();
  }

  @ParameterizedTest
  @EnumSource(ResponsibleTeamType.class)
  void getResponsibleTeamUsers(ResponsibleTeamType responsibleTeamType) {

    switch (responsibleTeamType) {
      case EXPLORATION -> when(regulatorTeamService.getExplorationTeamEnergyPortalUsers())
          .thenReturn(List.of(energyPortalUserDto));
      case NORTHERN_NORTH_SEA_WEST_OF_SHETLAND -> when(northernOffshoreAreaTeamService
          .getNorthernOffShoreAreaTeamEnergyPortalUsers())
          .thenReturn(List.of(energyPortalUserDto));
      case CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA -> when(southernOffshoreAreaTeamService
          .getSouthernOffShoreAreaTeamEnergyPortalUsers())
          .thenReturn(List.of(energyPortalUserDto));
      case ONSHORE -> when(onshoreAreaTeamService.getOnShoreAreaTeamEnergyPortalUsers())
          .thenReturn(List.of(energyPortalUserDto));
    }

    assertThat(responsibleTeamService.getResponsibleTeamUsers(responsibleTeamType))
        .isEqualTo(List.of(energyPortalUserDto));
  }

  @Test
  void getResponsibleTeamTypesForUser_all() {
    when(regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.EXPLORATION)).thenReturn(true);
    when(northernOffshoreAreaTeamService.isNorthernOffshoreTeamMember(user)).thenReturn(true);
    when(southernOffshoreAreaTeamService.isSouthernOffshoreTeamMember(user)).thenReturn(true);
    when(onshoreAreaTeamService.isOnshoreTeamMember(user)).thenReturn(true);

    assertThat(responsibleTeamService.getResponsibleTeamTypesForUser(user)).containsExactly(
        ResponsibleTeamType.EXPLORATION, ResponsibleTeamType.NORTHERN_NORTH_SEA_WEST_OF_SHETLAND,
        ResponsibleTeamType.CENTRAL_NORTH_SEA_SOUTHERN_NORTH_SEA_EAST_IRISH_SEA, ResponsibleTeamType.ONSHORE);
  }

  @Test
  void getResponsibleTeamTypesForUser_none() {
    when(regulatorTeamService.isRegulatorTeamRole(user, RegulatorTeamRole.EXPLORATION)).thenReturn(false);
    when(northernOffshoreAreaTeamService.isNorthernOffshoreTeamMember(user)).thenReturn(false);
    when(southernOffshoreAreaTeamService.isSouthernOffshoreTeamMember(user)).thenReturn(false);
    when(onshoreAreaTeamService.isOnshoreTeamMember(user)).thenReturn(false);

    assertThat(responsibleTeamService.getResponsibleTeamTypesForUser(user)).isEmpty();
  }
}
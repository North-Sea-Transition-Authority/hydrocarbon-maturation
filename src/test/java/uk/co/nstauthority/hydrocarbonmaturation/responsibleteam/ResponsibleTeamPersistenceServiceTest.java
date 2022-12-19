package uk.co.nstauthority.hydrocarbonmaturation.responsibleteam;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@ExtendWith(MockitoExtension.class)
class ResponsibleTeamPersistenceServiceTest {

  @Mock
  private ResponsibleTeamRepository responsibleTeamRepository;

  @InjectMocks
  private ResponsibleTeamPersistenceService responsibleTeamPersistenceService;

  @Test
  void saveResponsibleTeamForResource_verifyMethodCall() {
    var resource = mock(Resource.class);

    var argCaptor = ArgumentCaptor.forClass(ResponsibleTeam.class);

    responsibleTeamPersistenceService.saveResponsibleTeamForResource(resource, ResponsibleTeamType.EXPLORATION);

    verify(responsibleTeamRepository, times(1)).save(argCaptor.capture());

    assertThat(argCaptor.getValue())
        .hasOnlyFields("id", "resource", "responsibleTeamType")
        .extracting(
            ResponsibleTeam::getResource,
            ResponsibleTeam::getResponsibleTeamType)
        .containsExactly(
            resource,
            ResponsibleTeamType.EXPLORATION);
  }

  @Test
  void saveResponsibleTeamForResource_verifyItems() {
    var resource = mock(Resource.class);

    var argCaptor = ArgumentCaptor.forClass(ResponsibleTeam.class);

    responsibleTeamPersistenceService.saveResponsibleTeamForResource(resource, ResponsibleTeamType.CENTRAL_NORTH_SEA);

    verify(responsibleTeamRepository, times(1)).save(argCaptor.capture());

    assertThat(argCaptor.getValue())
        .hasOnlyFields("id", "resource", "responsibleTeamType")
        .extracting(
            ResponsibleTeam::getResource,
            ResponsibleTeam::getResponsibleTeamType)
        .containsExactly(
            resource,
            ResponsibleTeamType.CENTRAL_NORTH_SEA);
  }
}
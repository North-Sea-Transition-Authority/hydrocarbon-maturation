package uk.co.nstauthority.hydrocarbonmaturation.responsibleteam;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;

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

  @Test
  void getResponsibleTeamForResourceOrThrow_assertReturn() {
    var resource = new Resource();
    resource.setId(UUID.randomUUID());
    var responsibleTeam = mock(ResponsibleTeam.class);
    when(responsibleTeamRepository.findResponsibleTeamByResource(resource))
        .thenReturn(Optional.of(responsibleTeam));
    var result = responsibleTeamPersistenceService.getResponsibleTeamForResourceOrThrow(resource);
    assertThat(result).isEqualTo(responsibleTeam);
  }

  @Test
  void getResponsibleTeamForResourceOrThrow_assertThrows() {
    var resource = new Resource();
    resource.setId(UUID.randomUUID());
    when(responsibleTeamRepository.findResponsibleTeamByResource(resource))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> responsibleTeamPersistenceService
        .getResponsibleTeamForResourceOrThrow(resource))
        .hasMessage("Could not find the responsible team for resource with ID %s"
            .formatted(resource.getId()))
        .isExactlyInstanceOf(RemiEntityNotFoundException.class);
  }
}
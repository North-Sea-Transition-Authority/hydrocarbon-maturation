package uk.co.nstauthority.hydrocarbonmaturation.reservoirs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;

@ExtendWith(MockitoExtension.class)
class ReservoirPersistenceServiceTest {

  private static final String RESERVOIR_NAME_1 = "reservoir name 1";

  @Mock
  private ReservoirRepository reservoirRepository;

  @Captor
  private ArgumentCaptor<Reservoir> reservoirArgumentCaptor;

  private ReservoirPersistenceService reservoirPersistenceService;

  @BeforeEach
  void setUp() {
    reservoirPersistenceService = new ReservoirPersistenceService(reservoirRepository);
  }

  @Test
  void createReservoir() {
    reservoirPersistenceService.createReservoir(RESERVOIR_NAME_1);
    verify(reservoirRepository, times(1)).save(reservoirArgumentCaptor.capture());
    assertEquals(RESERVOIR_NAME_1, reservoirArgumentCaptor.getValue().getName());
  }

  @Test
  void saveReservoir() {
    var reservoir = new Reservoir();
    reservoirPersistenceService.saveReservoir(reservoir, RESERVOIR_NAME_1);
    verify(reservoirRepository, times(1)).save(reservoirArgumentCaptor.capture());
    assertThat(reservoirArgumentCaptor.getValue())
        .extracting(
            Reservoir::getId,
            Reservoir::getName)
        .containsExactly(
            reservoir.getId(),
            RESERVOIR_NAME_1);
  }

  @Test
  void getReservoir_verifyCalls() {
    var reservoir = new Reservoir();
    reservoir.setId(UUID.randomUUID());
    reservoirPersistenceService.getReservoir(reservoir.getId());
    verify(reservoirRepository, times(1)).findById(reservoir.getId());
  }

  @Test
  void getReservoirOrThrow() {
    var reservoir = new Reservoir();
    reservoir.setId(UUID.randomUUID());
    when(reservoirRepository.findById(reservoir.getId())).thenReturn(Optional.of(reservoir));
    assertThat(reservoirPersistenceService.getReservoirOrThrow(reservoir.getId()))
        .isEqualTo(reservoir);
  }

  @Test
  void getReservoirOrThrow_throws() {
    var id = UUID.randomUUID();
    when(reservoirRepository.findById(id)).thenReturn(Optional.empty());
    assertThatThrownBy(() -> reservoirPersistenceService.getReservoirOrThrow(id))
        .isInstanceOf(RemiEntityNotFoundException.class)
        .hasMessage("No reservoir found for id %s".formatted(id));
  }

  @Test
  void getReservoirs_verifyCalls() {
    var reservoir = new Reservoir();
    reservoir.setId(UUID.randomUUID());
    reservoirPersistenceService.getReservoirs(List.of(reservoir.getId()));
    verify(reservoirRepository, times(1)).findAllByIdIn(List.of(reservoir.getId()));
  }
}

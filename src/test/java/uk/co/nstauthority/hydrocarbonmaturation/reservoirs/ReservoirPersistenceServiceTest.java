package uk.co.nstauthority.hydrocarbonmaturation.reservoirs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}

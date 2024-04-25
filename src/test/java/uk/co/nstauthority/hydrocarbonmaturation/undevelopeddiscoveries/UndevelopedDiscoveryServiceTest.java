package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@ExtendWith(MockitoExtension.class)
class UndevelopedDiscoveryServiceTest {

  @Mock
  private Resource resource;

  @Mock
  private UndevelopedDiscoveryRepository undevelopedDiscoveryRepository;

  @InjectMocks
  private UndevelopedDiscoveryService undevelopedDiscoveryService;

  @Nested
  class getUndevelopedDiscoveryForResource {
    @Test
    void assertReturn() {
      var undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(resource, Boolean.TRUE);

      when(undevelopedDiscoveryRepository.findByResource(resource))
          .thenReturn(Optional.of(undevelopedDiscovery));

      assertThat(undevelopedDiscoveryService.getUndevelopedDiscoveryForResource(resource))
          .contains(undevelopedDiscovery);
    }

    @Test
    void assertNotPresent() {
      when(undevelopedDiscoveryRepository.findByResource(resource))
          .thenReturn(Optional.empty());

      assertThat(undevelopedDiscoveryService.getUndevelopedDiscoveryForResource(resource))
          .isNotPresent();
    }
  }

  @Nested
  class saveUndevelopedDiscoveryTrackingDetails {

    private ArgumentCaptor<UndevelopedDiscovery> undevelopedDiscoveryArgumentCaptor = ArgumentCaptor.forClass(
        UndevelopedDiscovery.class);

    @Test
    void newUndevelopedDiscovery() {
      when(undevelopedDiscoveryRepository.findByResource(resource)).thenReturn(Optional.empty());

      var trackingDetailsForm = new UndevelopedDiscoveryTrackingDetailsForm();
      trackingDetailsForm.setIsUndevelopedDiscovery(Boolean.TRUE);
      undevelopedDiscoveryService.saveUndevelopedDiscoveryTrackingDetails(resource, trackingDetailsForm);

      verify(undevelopedDiscoveryRepository, times(1))
          .save(undevelopedDiscoveryArgumentCaptor.capture());
      verifyNoMoreInteractions(undevelopedDiscoveryRepository);

      var savedValue = undevelopedDiscoveryArgumentCaptor.getValue();
      assertThat(savedValue.getIsUndevelopedDiscovery()).isTrue();
      assertThat(savedValue.getResource()).isEqualTo(resource);
    }

    @Test
    void existingUndevelopedDiscovery() {
      var undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(resource, Boolean.TRUE);
      when(undevelopedDiscoveryRepository.findByResource(resource)).thenReturn(Optional.of(undevelopedDiscovery));

      var trackingDetailsForm = new UndevelopedDiscoveryTrackingDetailsForm();
      trackingDetailsForm.setIsUndevelopedDiscovery(Boolean.FALSE);
      undevelopedDiscoveryService.saveUndevelopedDiscoveryTrackingDetails(resource, trackingDetailsForm);

      verify(undevelopedDiscoveryRepository, times(1))
          .save(undevelopedDiscoveryArgumentCaptor.capture());
      verifyNoMoreInteractions(undevelopedDiscoveryRepository);

      var savedValue = undevelopedDiscoveryArgumentCaptor.getValue();
      assertThat(savedValue.getIsUndevelopedDiscovery()).isFalse();
      assertThat(savedValue.getResource()).isEqualTo(resource);
      assertThat(savedValue.getId()).isEqualTo(undevelopedDiscovery.getId());
    }
  }
}

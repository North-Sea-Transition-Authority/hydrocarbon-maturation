package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

  @Mock
  private ResourceRepository resourceRepository;

  @Mock
  private Clock clock;

  private ResourceService resourceService;

  private Instant instant;

  @BeforeEach
  void setUp() {
    resourceService = new ResourceService(resourceRepository, clock);
    instant = Instant.now();
    when(clock.instant()).thenReturn(instant);
  }

  @Test
  public void createResource() {
    var resourceType = ResourceType.PROSPECT;
    var resourceArgumentCaptor = ArgumentCaptor.forClass(Resource.class);
    var resource = resourceService.createResource(resourceType);
    verify(resourceRepository, times(1)).save(resourceArgumentCaptor.capture());

    var createdResource = resourceArgumentCaptor.getValue();
    assertThat(createdResource.getType()).isEqualTo(ResourceType.PROSPECT);
    assertThat(createdResource.getCreatedTimestamp()).isEqualTo(instant);
  }
}

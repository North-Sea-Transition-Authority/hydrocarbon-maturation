package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

  private static final Instant INSTANT = Instant.now();

  @Mock
  private ResourceRepository resourceRepository;

  private ResourceService resourceService;

  @BeforeEach
  void setUp() {
    Clock clock = Clock.fixed(Instant.from(INSTANT), ZoneId.systemDefault());
    resourceService = new ResourceService(resourceRepository, clock);
  }

  @Test
  void getResourceForId_assertReturn() {
    var resource = new Resource();
    resource.setId(UUID.randomUUID());
    when(resourceRepository.findById(resource.getId()))
        .thenReturn(Optional.of(resource));
    assertThat(resourceService.getResourceById(resource.getId()))
        .contains(resource);
  }

  @Test
  void getResourceForId_assertNotPresent() {
    var uuidNotForResource = UUID.randomUUID();
    when(resourceRepository.findById(uuidNotForResource))
        .thenReturn(Optional.empty());
    assertThat(resourceService.getResourceById(uuidNotForResource))
        .isNotPresent();
  }

  @Test
  void createResource() {
    var resourceType = ResourceType.PROSPECT;
    var resourceArgumentCaptor = ArgumentCaptor.forClass(Resource.class);
    var resource = resourceService.createResource(resourceType);
    verify(resourceRepository, times(1)).save(resourceArgumentCaptor.capture());

    var createdResource = resourceArgumentCaptor.getValue();
    assertThat(createdResource.getType()).isEqualTo(ResourceType.PROSPECT);
    assertThat(createdResource.getCreatedTimestamp()).isEqualTo(INSTANT);
  }
}

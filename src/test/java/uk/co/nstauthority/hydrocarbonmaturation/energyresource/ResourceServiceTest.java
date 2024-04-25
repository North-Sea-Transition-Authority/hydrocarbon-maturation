package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
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
import uk.co.nstauthority.hydrocarbonmaturation.exception.RemiEntityNotFoundException;

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
  void getResourceOfType_isPresent() {
    var uuid = UUID.randomUUID();
    var resourceType = ResourceType.DISCOVERY;
    var resource = Optional.of(getResource(uuid, resourceType));

    when(resourceRepository.findById(uuid)).thenReturn(resource);

    assertThat(resourceService.getResourceOfType(uuid, resourceType))
        .isEqualTo(resource);
  }

  @Test
  void getResourceOfType_isNotOfResourceType() {
    var uuid = UUID.randomUUID();
    var resource = Optional.of(getResource(uuid, ResourceType.DISCOVERY));

    when(resourceRepository.findById(uuid)).thenReturn(resource);

    assertThat(resourceService.getResourceOfType(uuid, ResourceType.FIELD))
        .isEmpty();
  }

  @Test
  void getResourceOfType_throwsEntityNotFound() {
    var uuid = UUID.randomUUID();

    when(resourceRepository.findById(uuid)).thenReturn(Optional.empty());

    assertThatExceptionOfType(RemiEntityNotFoundException.class)
        .isThrownBy(() -> resourceService.getResourceOfType(uuid, ResourceType.DISCOVERY))
        .withMessage("Could not find resource with id %s".formatted(uuid.toString()));
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

  private Resource getResource(UUID id, ResourceType resourceType) {
    var resource = new Resource();
    resource.setId(id);
    resource.setType(resourceType);
    return resource;
  }
}

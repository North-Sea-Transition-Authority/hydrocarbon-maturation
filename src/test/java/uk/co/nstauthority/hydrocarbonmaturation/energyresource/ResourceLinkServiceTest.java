package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ResourceLinkServiceTest {

  @Mock
  private ResourceLinksRepository resourceLinksRepository;

  @InjectMocks
  private ResourceLinkService resourceLinkService;

  @Test
  void createNewResourceLink() {
    var prospectResource = new Resource();
    var discoveryResource = new Resource();
    prospectResource.setType(ResourceType.PROSPECT);
    discoveryResource.setType(ResourceType.DISCOVERY);

    ArgumentCaptor<ResourceLink> argumentCaptor = ArgumentCaptor.forClass(ResourceLink.class);

    resourceLinkService.createNewResourceLink(prospectResource, discoveryResource);

    verify(resourceLinksRepository, times(1))
        .save(argumentCaptor.capture());

    assertThat(argumentCaptor.getValue())
        .extracting(
            ResourceLink::getDiscoveryResource,
            ResourceLink::getProspectResource)
        .containsExactly(
            discoveryResource,
            prospectResource
        );
  }

  @Test
  void getResourceLinkByProspect_verifyMethodCall() {
    var prospect = new Resource();

    resourceLinkService.getResourceLinksByProspect(prospect);

    verify(resourceLinksRepository, times(1)).findAllByProspectResource(prospect);
  }

  @Test
  void isResourceLinkedOrSameResource_whenSameResource_assertTrue() {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);

    assertThat(resourceLinkService.isResourceLinkedOrSameResource(resource, resource))
        .isTrue();
  }

  @Test
  void isResourceLinkedOrSameResource_whenResourcesLinked_assertTrue() {
    var prospect = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    var linkedDiscovery = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    var resourceLink = new ResourceLink();
    resourceLink.setProspectResource(prospect);
    resourceLink.setDiscoveryResource(linkedDiscovery);

    when(resourceLinksRepository.findAllByProspectResource(prospect))
        .thenReturn(List.of(resourceLink));
    when(resourceLinksRepository.findAllByDiscoveryResource(linkedDiscovery))
        .thenReturn(List.of(resourceLink));

    assertThat(resourceLinkService.isResourceLinkedOrSameResource(prospect, linkedDiscovery))
        .isTrue();
    assertThat(resourceLinkService.isResourceLinkedOrSameResource(linkedDiscovery, prospect))
        .isTrue();
  }

  @Test
  void isResourceLinkedOrSameResource_whenResourcesNotLinked_assertFalse() {
    var prospect = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    var discovery = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);

    when(resourceLinksRepository.findAllByProspectResource(prospect))
        .thenReturn(List.of());
    when(resourceLinksRepository.findAllByDiscoveryResource(discovery))
        .thenReturn(List.of());

    assertThat(resourceLinkService.isResourceLinkedOrSameResource(prospect, discovery))
        .isFalse();
    assertThat(resourceLinkService.isResourceLinkedOrSameResource(discovery, prospect))
        .isFalse();
  }

  @Test
  void isResourceLinkedOrSameResource_whenResourceTypeField_assertThrows() {
    var field = ResourceTestUtils.buildResource(ResourceType.FIELD);
    var discovery = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);

    assertThatThrownBy(() -> resourceLinkService.isResourceLinkedOrSameResource(field, discovery))
        .isExactlyInstanceOf(NotImplementedException.class)
        .hasMessage("This functionality has not yet been implemented for resource type Field");
  }
}
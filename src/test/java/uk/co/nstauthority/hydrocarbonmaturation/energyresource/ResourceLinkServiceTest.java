package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
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
  void createNewProspectDiscoveryResourceLink() {
    var prospectResource = new Resource();
    var discoveryResource = new Resource();
    prospectResource.setType(ResourceType.PROSPECT);
    discoveryResource.setType(ResourceType.DISCOVERY);

    ArgumentCaptor<ResourceLink> argumentCaptor = ArgumentCaptor.forClass(ResourceLink.class);

    resourceLinkService.createNewProspectDiscoveryResourceLink(prospectResource, discoveryResource);

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
  void createNewDiscoveryFieldResourceLink() {
    var discoveryResource = new Resource();
    var fieldResource = new Resource();
    discoveryResource.setType(ResourceType.DISCOVERY);
    fieldResource.setType(ResourceType.FIELD);

    ArgumentCaptor<ResourceLink> argumentCaptor = ArgumentCaptor.forClass(ResourceLink.class);

    resourceLinkService.createNewDiscoveryFieldResourceLink(discoveryResource, fieldResource);

    verify(resourceLinksRepository, times(1))
        .save(argumentCaptor.capture());

    assertThat(argumentCaptor.getValue())
        .extracting(
            ResourceLink::getDiscoveryResource,
            ResourceLink::getFieldResource)
        .containsExactly(
            discoveryResource,
            fieldResource
        );
  }

  @Test
  void getResourceLinkByProspect_verifyMethodCall() {
    var prospect = new Resource();

    resourceLinkService.getResourceLinksByProspect(prospect);

    verify(resourceLinksRepository, times(1)).findAllByProspectResource(prospect);
  }

  @Test
  void getResourceLinkByDiscovery_verifyMethodCall() {
    var discovery = new Resource();

    resourceLinkService.getResourceLinksByDiscovery(discovery);

    verify(resourceLinksRepository, times(1)).findAllByDiscoveryResource(discovery);
  }

  @Test
  void getResourceLinkByField_verifyMethodCall() {
    var field = new Resource();

    resourceLinkService.getResourceLinksByField(field);

    verify(resourceLinksRepository, times(1)).findAllByFieldResource(field);
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
  void isResourceLinkedOrSameResource_whenFieldResourcesLinked_assertTrue() {
    var field = ResourceTestUtils.buildResource(ResourceType.FIELD);
    var linkedDiscovery = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    var resourceLink = new ResourceLink();
    resourceLink.setDiscoveryResource(linkedDiscovery);
    resourceLink.setProspectResource(ResourceTestUtils.buildResource(ResourceType.PROSPECT));
    resourceLink.setFieldResource(field);

    when(resourceLinksRepository.findAllByFieldResource(field))
        .thenReturn(List.of(resourceLink));
    when(resourceLinksRepository.findAllByDiscoveryResource(linkedDiscovery))
        .thenReturn(List.of(resourceLink));

    assertThat(resourceLinkService.isResourceLinkedOrSameResource(field, linkedDiscovery))
        .isTrue();
    assertThat(resourceLinkService.isResourceLinkedOrSameResource(linkedDiscovery, field))
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
  void prospectIsLinkedToDiscovery_whenNotProspect() {
    var discovery = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    assertFalse(resourceLinkService.prospectIsLinkedToDiscovery(discovery));
  }

  @Test
  void prospectIsLinkedToDiscovery_whenProspectLinkedToDiscovery() {
    var prospect = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    when(resourceLinksRepository.existsByProspectResourceAndDiscoveryResourceIsNotNull(prospect)).thenReturn(true);
    assertTrue(resourceLinkService.prospectIsLinkedToDiscovery(prospect));
  }

  @Test
  void prospectIsLinkedToDiscovery_whenProspectNotLinkedToDiscovery() {
    var prospect = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    when(resourceLinksRepository.existsByProspectResourceAndDiscoveryResourceIsNotNull(prospect)).thenReturn(false);
    assertFalse(resourceLinkService.prospectIsLinkedToDiscovery(prospect));
  }

  @Test
  void deleteDiscoveryToProspectLink_verifyMethodCall() {
    var resource = new Resource();
    resourceLinkService.deleteDiscoveryToProspectLink(resource);
    verify(resourceLinksRepository).deleteByDiscoveryResourceAndProspectResourceIsNotNull(resource);
  }
}
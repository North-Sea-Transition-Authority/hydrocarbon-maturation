package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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

}
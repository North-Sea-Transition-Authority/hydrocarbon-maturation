package uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldRepository;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationForm;

@ExtendWith(MockitoExtension.class)
class PostFieldDiscoveryLinkServiceTest {

  @Mock
  PostFieldDiscoveryLinkRepository postFieldDiscoveryLinkRepository;

  @Mock
  FieldRepository fieldRepository;

  @InjectMocks
  PostFieldDiscoveryLinkService postFieldDiscoveryLinkService;

  @Captor
  private ArgumentCaptor<PostFieldDiscoveryLink> linkArgumentCaptor;

  private Resource fieldResource;
  private Resource discovery;
  private Field field;
  private RegulatorManagedInformationForm form;

  @BeforeEach
  void setUp() {
    fieldResource = new Resource();
    fieldResource.setType(ResourceType.FIELD);

    discovery = new Resource();
    discovery.setType(ResourceType.DISCOVERY);

    field = new Field();
    field.setResource(fieldResource);
    field.setFieldIdentifier(1);

    form = new RegulatorManagedInformationForm();
    form.setLinkedToShelvedCopField(true);
    form.setFieldSelectedOption("1");
  }

  @Test
  void createPostFieldDiscoveryLink() {
    postFieldDiscoveryLinkService.createPostFieldDiscoveryLink(fieldResource, discovery);

    verify(postFieldDiscoveryLinkRepository, times(1)).save(linkArgumentCaptor.capture());

    assertThat(linkArgumentCaptor.getValue())
        .extracting(
            PostFieldDiscoveryLink::getFieldResource,
            PostFieldDiscoveryLink::getDiscoveryResource
        ).containsExactly(
            fieldResource,
            discovery
        );
  }

  @Test
  void createOrUpdatePostFieldDiscoveryLinkFromForm_fieldDoesNotExist_noSave_noDelete() {
    when(fieldRepository.findById(1)).thenReturn(Optional.empty());

    postFieldDiscoveryLinkService.createOrUpdatePostFieldDiscoveryLinkFromForm(form, discovery);

    verify(postFieldDiscoveryLinkRepository, never()).save(any(PostFieldDiscoveryLink.class));
  }

  @Test
  void createOrUpdatePostFieldDiscoveryLinkFromForm_oldLinkDoesNotExist_saveNewLink_noDelete() {
    when(fieldRepository.findById(1)).thenReturn(Optional.of(field));
    when(postFieldDiscoveryLinkRepository.getPostFieldDiscoveryLinkByDiscoveryResource(discovery)).thenReturn(Optional.empty());

    postFieldDiscoveryLinkService.createOrUpdatePostFieldDiscoveryLinkFromForm(form, discovery);

    verify(postFieldDiscoveryLinkRepository, times(1)).save(linkArgumentCaptor.capture());
    verify(postFieldDiscoveryLinkRepository, never()).delete(any(PostFieldDiscoveryLink.class));

    assertThat(linkArgumentCaptor.getValue())
        .extracting(
            PostFieldDiscoveryLink::getFieldResource,
            PostFieldDiscoveryLink::getDiscoveryResource
        ).containsExactly(
            fieldResource,
            discovery
        );
  }

  @Test
  void createOrUpdatePostFieldDiscoveryLinkFromForm_oldLinkDoesExist_saveNewLink_deleteOldLink() {
    var oldFieldResource = new Resource();

    var oldField = new Field();
    oldField.setResource(oldFieldResource);

    var oldLink = new PostFieldDiscoveryLink();
    oldLink.setFieldResource(oldFieldResource);
    oldLink.setDiscoveryResource(discovery);

    when(fieldRepository.findById(1)).thenReturn(Optional.of(field));
    when(postFieldDiscoveryLinkRepository.getPostFieldDiscoveryLinkByDiscoveryResource(discovery)).thenReturn(Optional.of(oldLink));

    postFieldDiscoveryLinkService.createOrUpdatePostFieldDiscoveryLinkFromForm(form, discovery);

    verify(postFieldDiscoveryLinkRepository, times(1)).save(linkArgumentCaptor.capture());
    verify(postFieldDiscoveryLinkRepository, times(1)).delete(oldLink);

    assertThat(linkArgumentCaptor.getValue())
        .extracting(
            PostFieldDiscoveryLink::getFieldResource,
            PostFieldDiscoveryLink::getDiscoveryResource
        ).containsExactly(
            fieldResource,
            discovery
        );
  }

  @Test
  void createOrUpdatePostFieldDiscoveryLinkFromForm_oldLinkSameAsNewLink_noSave_noDelete() {
    var oldLink = new PostFieldDiscoveryLink();
    oldLink.setFieldResource(fieldResource);
    oldLink.setDiscoveryResource(discovery);

    when(fieldRepository.findById(1)).thenReturn(Optional.of(field));
    when(postFieldDiscoveryLinkRepository.getPostFieldDiscoveryLinkByDiscoveryResource(discovery)).thenReturn(Optional.of(oldLink));

    postFieldDiscoveryLinkService.createOrUpdatePostFieldDiscoveryLinkFromForm(form, discovery);

    verify(postFieldDiscoveryLinkRepository, never()).save(any(PostFieldDiscoveryLink.class));
    verify(postFieldDiscoveryLinkRepository, never()).delete(any(PostFieldDiscoveryLink.class));
  }

  @Test
  void getPostFieldDiscoveryLinkByDiscovery() {
    postFieldDiscoveryLinkService.getPostFieldDiscoveryLinkByDiscovery(discovery);
    verify(postFieldDiscoveryLinkRepository, times(1)).getPostFieldDiscoveryLinkByDiscoveryResource(discovery);
  }

  @Test
  void deletePostFieldDiscoveryLinkIfExists_doesExist_deleteLink() {
    var oldLink = new PostFieldDiscoveryLink();
    when(postFieldDiscoveryLinkRepository.getPostFieldDiscoveryLinkByDiscoveryResource(discovery)).thenReturn(Optional.of(oldLink));

    postFieldDiscoveryLinkService.deletePostFieldDiscoveryLinkIfExists(discovery);

    verify(postFieldDiscoveryLinkRepository, times(1)).delete(oldLink);
  }

  @Test
  void deletePostFieldDiscoveryLinkIfExists_doesNotExist_doNothing() {
    when(postFieldDiscoveryLinkRepository.getPostFieldDiscoveryLinkByDiscoveryResource(discovery)).thenReturn(Optional.empty());

    postFieldDiscoveryLinkService.deletePostFieldDiscoveryLinkIfExists(discovery);

    verify(postFieldDiscoveryLinkRepository, never()).delete(any(PostFieldDiscoveryLink.class));
  }

  @Test
  void getFieldLinkedToDiscoveryIfExists_doesExist() {
    var link = new PostFieldDiscoveryLink();
    link.setFieldResource(fieldResource);
    link.setDiscoveryResource(discovery);

    when(postFieldDiscoveryLinkRepository.getPostFieldDiscoveryLinkByDiscoveryResource(discovery)).thenReturn(Optional.of(link));
    when(fieldRepository.findByResource(fieldResource)).thenReturn(Optional.of(field));

    assertThat(postFieldDiscoveryLinkService.getFieldLinkedToDiscoveryIfExists(discovery)).isEqualTo(Optional.of(field));
  }

  @Test
  void getFieldLinkedToDiscoveryIfExists_doesNotExist() {
    when(postFieldDiscoveryLinkRepository.getPostFieldDiscoveryLinkByDiscoveryResource(discovery)).thenReturn(Optional.empty());

    assertThat(postFieldDiscoveryLinkService.getFieldLinkedToDiscoveryIfExists(discovery)).isEmpty();
  }

  @Test
  void getAllDiscoveriesLinkedToField() {
    var link1 = new PostFieldDiscoveryLink();
    link1.setFieldResource(fieldResource);
    link1.setDiscoveryResource(discovery);

    var discovery2 = new Resource();

    var link2 = new PostFieldDiscoveryLink();
    link2.setFieldResource(fieldResource);
    link2.setDiscoveryResource(discovery2);

    when(postFieldDiscoveryLinkRepository.getPostFieldDiscoveryLinksByFieldResource(fieldResource)).thenReturn(List.of(link1, link2));

    assertThat(postFieldDiscoveryLinkService.getAllDiscoveriesLinkedToField(fieldResource)).isEqualTo(List.of(discovery, discovery2));
  }

}

package uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldRepository;
import uk.co.nstauthority.hydrocarbonmaturation.regulatormanagedinformation.RegulatorManagedInformationForm;

@Service
public class PostFieldDiscoveryLinkService {

  private final PostFieldDiscoveryLinkRepository postFieldDiscoveryLinkRepository;
  private final FieldRepository fieldRepository;

  @Autowired
  PostFieldDiscoveryLinkService(PostFieldDiscoveryLinkRepository postFieldDiscoveryLinkRepository,
                                FieldRepository fieldRepository) {
    this.postFieldDiscoveryLinkRepository = postFieldDiscoveryLinkRepository;
    this.fieldRepository = fieldRepository;
  }

  @Transactional
  public void createPostFieldDiscoveryLink(Resource field, Resource discovery) {
    var postFieldDiscoveryLink = new PostFieldDiscoveryLink();
    postFieldDiscoveryLink.setFieldResource(field);
    postFieldDiscoveryLink.setDiscoveryResource(discovery);

    postFieldDiscoveryLinkRepository.save(postFieldDiscoveryLink);
  }

  @Transactional
  public void createOrUpdatePostFieldDiscoveryLinkFromForm(RegulatorManagedInformationForm form, Resource discovery) {
    var field = fieldRepository.findById(Integer.parseInt(form.getFieldSelectedOption()));

    field.ifPresent(f -> {
      var discoveryLink = getPostFieldDiscoveryLinkByDiscovery(discovery);
      discoveryLink.ifPresentOrElse(link -> {
        if (f.getResource() != link.getFieldResource()) {
          createPostFieldDiscoveryLink(f.getResource(), discovery);
          postFieldDiscoveryLinkRepository.delete(link);
        }
      }, () -> createPostFieldDiscoveryLink(f.getResource(), discovery));
    });
  }

  public Optional<PostFieldDiscoveryLink> getPostFieldDiscoveryLinkByDiscovery(Resource discovery) {
    return postFieldDiscoveryLinkRepository.getPostFieldDiscoveryLinkByDiscoveryResource(discovery);
  }

  @Transactional
  public void deletePostFieldDiscoveryLinkIfExists(Resource discovery) {
    var postFieldDiscoveryLink = getPostFieldDiscoveryLinkByDiscovery(discovery);
    postFieldDiscoveryLink.ifPresent(postFieldDiscoveryLinkRepository::delete);
  }

  public Optional<Field> getFieldLinkedToDiscoveryIfExists(Resource discovery) {
    var postFieldDiscoveryLink = getPostFieldDiscoveryLinkByDiscovery(discovery);

    return postFieldDiscoveryLink.flatMap(link -> fieldRepository.findByResource(link.getFieldResource()));
  }

  public boolean postFieldDiscoveryLinkExists(Resource discovery) {
    return postFieldDiscoveryLinkRepository.existsByDiscoveryResource(discovery);
  }

  public List<Resource> getAllDiscoveriesLinkedToField(Resource fieldResource) {
    return postFieldDiscoveryLinkRepository.getPostFieldDiscoveryLinksByFieldResource(fieldResource).stream()
        .map(PostFieldDiscoveryLink::getDiscoveryResource)
        .toList();
  }

}

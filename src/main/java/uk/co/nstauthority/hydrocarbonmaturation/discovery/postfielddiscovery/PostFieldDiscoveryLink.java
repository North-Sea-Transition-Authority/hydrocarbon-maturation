package uk.co.nstauthority.hydrocarbonmaturation.discovery.postfielddiscovery;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Entity
@Table(name = "post_field_discovery_links")
public class PostFieldDiscoveryLink {

  @Id
  @UuidGenerator
  private UUID id;

  @JoinColumn(name = "field_resource_id")
  @ManyToOne
  private Resource fieldResource;

  @JoinColumn(name = "discovery_resource_id")
  @ManyToOne
  private Resource discoveryResource;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Resource getDiscoveryResource() {
    return discoveryResource;
  }

  public void setDiscoveryResource(Resource discoveryResource) {
    this.discoveryResource = discoveryResource;
  }

  public Resource getFieldResource() {
    return fieldResource;
  }

  public void setFieldResource(Resource fieldResource) {
    this.fieldResource = fieldResource;
  }

}

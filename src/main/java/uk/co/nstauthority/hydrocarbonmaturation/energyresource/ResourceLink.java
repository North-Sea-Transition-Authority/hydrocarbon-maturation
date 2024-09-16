package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "resource_links")
public class ResourceLink {

  @Id
  @UuidGenerator
  private UUID id;

  @JoinColumn(name = "prospect_resource_id")
  @ManyToOne
  private Resource prospectResource;

  @JoinColumn(name = "discovery_resource_id")
  @ManyToOne
  private Resource discoveryResource;

  @JoinColumn(name = "field_resource_id")
  @ManyToOne
  private Resource fieldResource;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Resource getProspectResource() {
    return prospectResource;
  }

  public void setProspectResource(Resource prospectResource) {
    this.prospectResource = prospectResource;
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

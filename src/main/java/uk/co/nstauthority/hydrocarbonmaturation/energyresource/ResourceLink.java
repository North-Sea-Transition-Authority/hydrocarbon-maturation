package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "resource_links")
class ResourceLink {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @JoinColumn(name = "prospect_resource_id")
  @ManyToOne
  private Resource prospectResource;

  @JoinColumn(name = "discovery_resource_id")
  @ManyToOne
  private Resource discoveryResource;

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
}

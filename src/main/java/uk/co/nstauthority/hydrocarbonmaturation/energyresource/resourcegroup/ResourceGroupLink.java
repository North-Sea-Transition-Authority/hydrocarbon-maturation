package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Entity
@Table(name = "resource_group_links")
@Audited
public class ResourceGroupLink {

  @Id
  @UuidGenerator
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "resource_id")
  @NotAudited
  private Resource resource;

  private UUID resourceGroupId;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Resource getResource() {
    return resource;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }

  public UUID getResourceGroupId() {
    return resourceGroupId;
  }

  public void setResourceGroupId(UUID resourceGroupId) {
    this.resourceGroupId = resourceGroupId;
  }
}

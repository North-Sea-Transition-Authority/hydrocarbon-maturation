package uk.co.nstauthority.hydrocarbonmaturation.energyresource.resourcegroup;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Entity
@Table(name = "resource_group_links")
public class ResourceGroupLink {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "resource_id")
  private Resource resource;

  private UUID resourceGroupId;

  private boolean preferredView;

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

  public boolean getPreferredView() {
    return preferredView;
  }

  public void setPreferredView(boolean preferredView) {
    this.preferredView = preferredView;
  }
}

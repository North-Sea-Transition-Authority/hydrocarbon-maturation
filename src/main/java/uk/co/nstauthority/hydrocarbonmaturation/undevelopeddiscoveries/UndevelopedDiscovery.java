package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Entity
@Table(name = "undeveloped_discoveries")
@Audited
public class UndevelopedDiscovery {

  @Id
  @UuidGenerator
  private UUID id;

  private Boolean isUndevelopedDiscovery;

  @ManyToOne
  @JoinColumn(name = "resource_id")
  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  private Resource resource;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Boolean getIsUndevelopedDiscovery() {
    return isUndevelopedDiscovery;
  }

  public void setIsUndevelopedDiscovery(Boolean undevelopedDiscovery) {
    isUndevelopedDiscovery = undevelopedDiscovery;
  }

  public Resource getResource() {
    return resource;
  }

  public void setResource(Resource resource) {
    this.resource = resource;
  }
}

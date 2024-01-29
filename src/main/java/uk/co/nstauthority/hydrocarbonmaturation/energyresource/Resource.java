package uk.co.nstauthority.hydrocarbonmaturation.energyresource;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "resources")
public class Resource {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @Enumerated(EnumType.STRING)
  private ResourceType type;

  private Instant createdTimestamp;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public ResourceType getType() {
    return type;
  }

  public void setType(ResourceType type) {
    this.type = type;
  }

  public Instant getCreatedTimestamp() {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(Instant createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }
}

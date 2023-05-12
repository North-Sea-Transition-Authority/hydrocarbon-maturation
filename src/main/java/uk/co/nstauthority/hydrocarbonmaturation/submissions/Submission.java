package uk.co.nstauthority.hydrocarbonmaturation.submissions;

import java.time.Instant;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Entity
@Table(name = "submissions")
public class Submission {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @JoinColumn(name = "resource_id")
  @ManyToOne
  private Resource resource;

  private Instant withdrawnTimestamp;

  private String withdrawalReason;

  private Integer withdrawnByWuaId;

  private Boolean withdrawn;

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

  public Instant getWithdrawnTimestamp() {
    return withdrawnTimestamp;
  }

  public void setWithdrawnTimestamp(Instant withdrawnTimestamp) {
    this.withdrawnTimestamp = withdrawnTimestamp;
  }

  public String getWithdrawalReason() {
    return withdrawalReason;
  }

  public void setWithdrawalReason(String withdrawalReason) {
    this.withdrawalReason = withdrawalReason;
  }

  public Integer getWithdrawnByWuaId() {
    return withdrawnByWuaId;
  }

  public void setWithdrawnByWuaId(Integer withdrawnByWuaId) {
    this.withdrawnByWuaId = withdrawnByWuaId;
  }

  public Boolean isWithdrawn() {
    return withdrawn;
  }

  public void setWithdrawn(Boolean withdrawn) {
    this.withdrawn = withdrawn;
  }
}

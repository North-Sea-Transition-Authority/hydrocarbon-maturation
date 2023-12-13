package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import java.math.BigDecimal;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.PlannedSanctionedWellsChildEntity;

@Entity
@Table(name = "artificial_lift_mechanisms")
public class ArtificialLiftMechanisms implements PlannedSanctionedWellsChildEntity {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "planned_sanctioned_wells_id")
  private PlannedSanctionedWells plannedSanctionedWells;

  @Enumerated(EnumType.STRING)
  private ArtificialLiftMechanism artificialLiftMechanism;

  private String otherArtificialLiftMechanism;

  private BigDecimal numberOfWellsWithArtificialLiftMechanism;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public PlannedSanctionedWells getPlannedSanctionedWells() {
    return plannedSanctionedWells;
  }

  public void setPlannedSanctionedWells(PlannedSanctionedWells plannedSanctionedWells) {
    this.plannedSanctionedWells = plannedSanctionedWells;
  }

  public ArtificialLiftMechanism getArtificialLiftMechanism() {
    return artificialLiftMechanism;
  }

  public void setArtificialLiftMechanism(ArtificialLiftMechanism artificialLiftMechanism) {
    this.artificialLiftMechanism = artificialLiftMechanism;
  }

  public String getOtherArtificialLiftMechanism() {
    return otherArtificialLiftMechanism;
  }

  public void setOtherArtificialLiftMechanism(String otherArtificialLiftMechanism) {
    this.otherArtificialLiftMechanism = otherArtificialLiftMechanism;
  }

  public BigDecimal getNumberOfWellsWithArtificialLiftMechanism() {
    return numberOfWellsWithArtificialLiftMechanism;
  }

  public void setNumberOfWellsWithArtificialLiftMechanism(BigDecimal numberOfWellsWithArtificialLiftMechanismType) {
    this.numberOfWellsWithArtificialLiftMechanism = numberOfWellsWithArtificialLiftMechanismType;
  }
}

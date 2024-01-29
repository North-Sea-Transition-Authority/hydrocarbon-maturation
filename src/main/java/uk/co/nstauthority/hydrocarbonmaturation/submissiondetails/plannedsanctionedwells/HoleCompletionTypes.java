package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.PlannedSanctionedWellsChildEntity;

@Entity
@Table(name = "hole_completion_types")
public class HoleCompletionTypes implements PlannedSanctionedWellsChildEntity {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "planned_sanctioned_wells_id")
  private PlannedSanctionedWells plannedSanctionedWells;

  @Enumerated(EnumType.STRING)
  private HoleCompletionType holeCompletionType;

  private String otherHoleCompletionType;

  private BigDecimal numberOfWellsWithHoleCompletionType;

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

  public HoleCompletionType getHoleCompletionType() {
    return holeCompletionType;
  }

  public void setHoleCompletionType(HoleCompletionType holeCompletionType) {
    this.holeCompletionType = holeCompletionType;
  }

  public String getOtherHoleCompletionType() {
    return otherHoleCompletionType;
  }

  public void setOtherHoleCompletionType(String otherHoleCompletionType) {
    this.otherHoleCompletionType = otherHoleCompletionType;
  }

  public BigDecimal getNumberOfWellsWithHoleCompletionType() {
    return numberOfWellsWithHoleCompletionType;
  }

  public void setNumberOfWellsWithHoleCompletionType(BigDecimal numberOfWellsWithHoleCompletionType) {
    this.numberOfWellsWithHoleCompletionType = numberOfWellsWithHoleCompletionType;
  }
}

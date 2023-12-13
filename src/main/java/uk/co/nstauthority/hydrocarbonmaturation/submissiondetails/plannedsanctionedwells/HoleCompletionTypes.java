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

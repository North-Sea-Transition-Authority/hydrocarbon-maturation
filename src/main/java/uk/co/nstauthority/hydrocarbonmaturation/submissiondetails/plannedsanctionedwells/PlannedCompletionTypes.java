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

@Entity
@Table(name = "planned_completion_types")
public class PlannedCompletionTypes {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "planned_sanctioned_wells_id")
  private PlannedSanctionedWells plannedSanctionedWells;

  @Enumerated(EnumType.STRING)
  private PlannedCompletionType plannedCompletionType;

  private String otherPlannedCompletionType;

  private BigDecimal numberOfWellsWithPlannedCompletionType;

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

  public PlannedCompletionType getPlannedCompletionType() {
    return plannedCompletionType;
  }

  public void setPlannedCompletionType(PlannedCompletionType plannedCompletionType) {
    this.plannedCompletionType = plannedCompletionType;
  }

  public String getOtherPlannedCompletionType() {
    return otherPlannedCompletionType;
  }

  public void setOtherPlannedCompletionType(String otherPlannedCompletionType) {
    this.otherPlannedCompletionType = otherPlannedCompletionType;
  }

  public BigDecimal getNumberOfWellsWithPlannedCompletionType() {
    return numberOfWellsWithPlannedCompletionType;
  }

  public void setNumberOfWellsWithPlannedCompletionType(BigDecimal numberOfWellsWithPlannedCompletionType) {
    this.numberOfWellsWithPlannedCompletionType = numberOfWellsWithPlannedCompletionType;
  }
}

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
@Table(name = "planned_completion_types")
public class PlannedCompletionTypes implements PlannedSanctionedWellsChildEntity {
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

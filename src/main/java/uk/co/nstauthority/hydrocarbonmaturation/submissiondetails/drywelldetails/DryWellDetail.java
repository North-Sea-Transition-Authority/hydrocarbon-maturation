package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.DrillDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;

@Entity
@Table(name = "dry_well_details")
public class DryWellDetail extends DrillDetail implements SubmissionDetailChildEntity {

  @Enumerated(EnumType.STRING)
  private DryHoleType dryHoleType;

  private String dryHoleDescription;

  private String dryHoleScope;

  private String dryHoleComments;

  private BigDecimal dryHoleCost;

  @Enumerated(EnumType.STRING)
  private SourceFailureCategory sourceFailureCategory;

  @Enumerated(EnumType.STRING)
  private ReservoirFailureCategory reservoirFailureCategory;

  @Enumerated(EnumType.STRING)
  private SealFailureCategory sealFailureCategory;

  @Enumerated(EnumType.STRING)
  private TrapFailureCategory trapFailureCategory;

  @Enumerated(EnumType.STRING)
  private MigrationFailureCategory migrationFailureCategory;

  public DryHoleType getDryHoleType() {
    return dryHoleType;
  }

  public void setDryHoleType(DryHoleType dryHoleType) {
    this.dryHoleType = dryHoleType;
  }

  public String getDryHoleDescription() {
    return dryHoleDescription;
  }

  public void setDryHoleDescription(String dryHoleDescription) {
    this.dryHoleDescription = dryHoleDescription;
  }

  public String getDryHoleScope() {
    return dryHoleScope;
  }

  public void setDryHoleScope(String dryHoleScope) {
    this.dryHoleScope = dryHoleScope;
  }

  public String getDryHoleComments() {
    return dryHoleComments;
  }

  public void setDryHoleComments(String dryHoleComments) {
    this.dryHoleComments = dryHoleComments;
  }

  public BigDecimal getDryHoleCost() {
    return dryHoleCost;
  }

  public void setDryHoleCost(BigDecimal dryHoleCost) {
    this.dryHoleCost = dryHoleCost;
  }

  public SourceFailureCategory getSourceFailureCategory() {
    return sourceFailureCategory;
  }

  public void setSourceFailureCategory(
      SourceFailureCategory sourceFailureCategory) {
    this.sourceFailureCategory = sourceFailureCategory;
  }

  public ReservoirFailureCategory getReservoirFailureCategory() {
    return reservoirFailureCategory;
  }

  public void setReservoirFailureCategory(
      ReservoirFailureCategory reservoirFailureCategory) {
    this.reservoirFailureCategory = reservoirFailureCategory;
  }

  public SealFailureCategory getSealFailureCategory() {
    return sealFailureCategory;
  }

  public void setSealFailureCategory(
      SealFailureCategory sealFailureCategory) {
    this.sealFailureCategory = sealFailureCategory;
  }

  public TrapFailureCategory getTrapFailureCategory() {
    return trapFailureCategory;
  }

  public void setTrapFailureCategory(
      TrapFailureCategory trapFailureCategory) {
    this.trapFailureCategory = trapFailureCategory;
  }

  public MigrationFailureCategory getMigrationFailureCategory() {
    return migrationFailureCategory;
  }

  public void setMigrationFailureCategory(
      MigrationFailureCategory migrationFailureCategory) {
    this.migrationFailureCategory = migrationFailureCategory;
  }
}

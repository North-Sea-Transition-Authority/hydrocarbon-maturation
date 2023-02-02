package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.DrillDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;

@Entity
@Table(name = "dry_well_details")
class DryWellDetail extends DrillDetail implements SubmissionDetailChildEntity {

  @Enumerated(EnumType.STRING)
  private DryHoleType dryHoleType;

  private String dryHoleDescription;

  private String dryHoleScope;

  private String dryHoleComments;

  private BigDecimal dryHoleCost;

  private SourceFailureCategory sourceFailureCategory;

  private ReservoirFailureCategory reservoirFailureCategory;

  private SealFailureCategory sealFailureCategory;

  private TrapFailureCategory trapFailureCategory;

  private MigrationFailureCategory migrationFailureCategory;

  DryHoleType getDryHoleType() {
    return dryHoleType;
  }

  void setDryHoleType(
      DryHoleType dryHoleType) {
    this.dryHoleType = dryHoleType;
  }

  String getDryHoleDescription() {
    return dryHoleDescription;
  }

  void setDryHoleDescription(String dryHoleDescription) {
    this.dryHoleDescription = dryHoleDescription;
  }

  public String getDryHoleScope() {
    return dryHoleScope;
  }

  void setDryHoleScope(String dryHoleScope) {
    this.dryHoleScope = dryHoleScope;
  }

  String getDryHoleComments() {
    return dryHoleComments;
  }

  void setDryHoleComments(String dryHoleComments) {
    this.dryHoleComments = dryHoleComments;
  }

  BigDecimal getDryHoleCost() {
    return dryHoleCost;
  }

  void setDryHoleCost(BigDecimal dryHoleCost) {
    this.dryHoleCost = dryHoleCost;
  }

  SourceFailureCategory getSourceFailureCategory() {
    return sourceFailureCategory;
  }

  void setSourceFailureCategory(
      SourceFailureCategory sourceFailureCategory) {
    this.sourceFailureCategory = sourceFailureCategory;
  }

  ReservoirFailureCategory getReservoirFailureCategory() {
    return reservoirFailureCategory;
  }

  void setReservoirFailureCategory(
      ReservoirFailureCategory reservoirFailureCategory) {
    this.reservoirFailureCategory = reservoirFailureCategory;
  }

  SealFailureCategory getSealFailureCategory() {
    return sealFailureCategory;
  }

  void setSealFailureCategory(
      SealFailureCategory sealFailureCategory) {
    this.sealFailureCategory = sealFailureCategory;
  }

  TrapFailureCategory getTrapFailureCategory() {
    return trapFailureCategory;
  }

  void setTrapFailureCategory(
      TrapFailureCategory trapFailureCategory) {
    this.trapFailureCategory = trapFailureCategory;
  }

  MigrationFailureCategory getMigrationFailureCategory() {
    return migrationFailureCategory;
  }

  void setMigrationFailureCategory(
      MigrationFailureCategory migrationFailureCategory) {
    this.migrationFailureCategory = migrationFailureCategory;
  }
}

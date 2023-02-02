package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.WellDetailsForm;

class DryWellDetailsForm implements SubmissionForm {

  private WellDetailsForm drillDetailsForm = new WellDetailsForm();
  private DryHoleType dryHoleType;
  private String dryHoleDescription;
  private String dryHoleScope;
  private String dryHoleComments;
  private DecimalInput dryHoleCost = new DecimalInput();
  private SourceFailureCategory sourceFailureCategory;
  private ReservoirFailureCategory reservoirFailureCategory;
  private SealFailureCategory sealFailureCategory;
  private TrapFailureCategory trapFailureCategory;
  private MigrationFailureCategory migrationFailureCategory;

  public DryHoleType getDryHoleType() {
    return dryHoleType;
  }

  public void setDryHoleType(
      DryHoleType dryHoleType) {
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

  public DecimalInput getDryHoleCost() {
    return dryHoleCost;
  }

  public void setDryHoleCost(DecimalInput dryHoleCost) {
    this.dryHoleCost = dryHoleCost;
  }

  public WellDetailsForm getDrillDetailsForm() {
    return drillDetailsForm;
  }

  public void setDrillDetailsForm(
      WellDetailsForm drillDetailsForm) {
    this.drillDetailsForm = drillDetailsForm;
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

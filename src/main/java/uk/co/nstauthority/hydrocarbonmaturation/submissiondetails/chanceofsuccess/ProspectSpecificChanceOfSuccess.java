package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
class ProspectSpecificChanceOfSuccess extends ChanceOfSuccessDetails {

  @Column(name = "prospect_specific_trap_percentage")
  private String trapPercentage;

  @Column(name = "prospect_specific_trap_comments")
  private String trapComments;

  @Column(name = "prospect_specific_migration_percentage")
  private String migrationPercentage;

  @Column(name = "prospect_specific_migration_comments")
  private String migrationComments;

  public String getTrapPercentage() {
    return trapPercentage;
  }

  public void setTrapPercentage(String trapPercentage) {
    this.trapPercentage = trapPercentage;
  }

  public String getTrapComments() {
    return trapComments;
  }

  public void setTrapComments(String trapComments) {
    this.trapComments = trapComments;
  }

  public String getMigrationPercentage() {
    return migrationPercentage;
  }

  public void setMigrationPercentage(String migrationPercentage) {
    this.migrationPercentage = migrationPercentage;
  }

  public String getMigrationComments() {
    return migrationComments;
  }

  public void setMigrationComments(String migrationComments) {
    this.migrationComments = migrationComments;
  }
}

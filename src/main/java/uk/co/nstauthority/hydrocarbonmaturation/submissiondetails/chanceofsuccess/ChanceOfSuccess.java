package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import java.math.BigDecimal;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Entity
@Table(name = "chance_of_success")
public class ChanceOfSuccess {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  @Column(name = "play_source_percentage")
  private BigDecimal sourcePercentage;

  @Column(name = "play_source_comments")
  private String sourceComments;

  @Column(name = "play_reservoir_percentage")
  private BigDecimal reservoirPercentage;

  @Column(name = "play_reservoir_comments")
  private String reservoirComments;

  @Column(name = "play_top_seal_percentage")
  private BigDecimal topSealPercentage;

  @Column(name = "play_top_seal_comments")
  private String topSealComments;

  @Column(name = "prospect_specific_trap_percentage")
  private BigDecimal prospectTrapPercentage;

  @Column(name = "prospect_specific_trap_comments")
  private String prospectTrapComments;

  @Column(name = "prospect_specific_migration_percentage")
  private BigDecimal prospectMigrationPercentage;

  @Column(name = "prospect_specific_migration_comments")
  private String prospectMigrationComments;

  @Column(name = "prospect_specific_source_percentage")
  private BigDecimal prospectSourcePercentage;

  @Column(name = "prospect_specific_source_comments")
  private String prospectSourceComments;

  @Column(name = "prospect_specific_reservoir_percentage")
  private BigDecimal prospectReservoirPercentage;

  @Column(name = "prospect_specific_reservoir_comments")
  private String prospectReservoirComments;

  @Column(name = "prospect_specific_top_seal_percentage")
  private BigDecimal prospectTopSealPercentage;

  @Column(name = "prospect_specific_top_seal_comments")
  private String prospectTopSealComments;

  private Boolean geophysicallyModified;
  private BigDecimal geophysicallyModifiedPercentage;
  private String geophysicallyModifiedComments;

  private Boolean relatedToSpecificWellLocation;
  private BigDecimal wellModifiedPercentage;
  private String wellModifiedComments;

  public ChanceOfSuccess() {
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public SubmissionDetail getSubmissionDetail() {
    return submissionDetail;
  }

  public void setSubmissionDetail(SubmissionDetail submissionDetail) {
    this.submissionDetail = submissionDetail;
  }

  public BigDecimal getSourcePercentage() {
    return sourcePercentage;
  }

  public void setSourcePercentage(BigDecimal sourcePercentage) {
    this.sourcePercentage = sourcePercentage;
  }

  public String getSourceComments() {
    return sourceComments;
  }

  public void setSourceComments(String sourceComments) {
    this.sourceComments = sourceComments;
  }

  public BigDecimal getReservoirPercentage() {
    return reservoirPercentage;
  }

  public void setReservoirPercentage(BigDecimal reservoirPercentage) {
    this.reservoirPercentage = reservoirPercentage;
  }

  public String getReservoirComments() {
    return reservoirComments;
  }

  public void setReservoirComments(String reservoirComments) {
    this.reservoirComments = reservoirComments;
  }

  public BigDecimal getTopSealPercentage() {
    return topSealPercentage;
  }

  public void setTopSealPercentage(BigDecimal topSealPercentage) {
    this.topSealPercentage = topSealPercentage;
  }

  public String getTopSealComments() {
    return topSealComments;
  }

  public void setTopSealComments(String topSealComments) {
    this.topSealComments = topSealComments;
  }

  public BigDecimal getProspectTrapPercentage() {
    return prospectTrapPercentage;
  }

  public void setProspectTrapPercentage(BigDecimal trapPercentage) {
    this.prospectTrapPercentage = trapPercentage;
  }

  public String getProspectTrapComments() {
    return prospectTrapComments;
  }

  public void setProspectTrapComments(String trapComments) {
    this.prospectTrapComments = trapComments;
  }

  public BigDecimal getProspectMigrationPercentage() {
    return prospectMigrationPercentage;
  }

  public void setProspectMigrationPercentage(BigDecimal migrationPercentage) {
    this.prospectMigrationPercentage = migrationPercentage;
  }

  public String getProspectMigrationComments() {
    return prospectMigrationComments;
  }

  public void setProspectMigrationComments(String migrationComments) {
    this.prospectMigrationComments = migrationComments;
  }

  public BigDecimal getProspectSourcePercentage() {
    return prospectSourcePercentage;
  }

  public void setProspectSourcePercentage(BigDecimal prospectSourcePercentage) {
    this.prospectSourcePercentage = prospectSourcePercentage;
  }

  public String getProspectSourceComments() {
    return prospectSourceComments;
  }

  public void setProspectSourceComments(String prospectSourceComments) {
    this.prospectSourceComments = prospectSourceComments;
  }

  public BigDecimal getProspectReservoirPercentage() {
    return prospectReservoirPercentage;
  }

  public void setProspectReservoirPercentage(BigDecimal prospectReservoirPercentage) {
    this.prospectReservoirPercentage = prospectReservoirPercentage;
  }

  public String getProspectReservoirComments() {
    return prospectReservoirComments;
  }

  public void setProspectReservoirComments(String prospectReservoirComments) {
    this.prospectReservoirComments = prospectReservoirComments;
  }

  public BigDecimal getProspectTopSealPercentage() {
    return prospectTopSealPercentage;
  }

  public void setProspectTopSealPercentage(BigDecimal prospectTopSealPercentage) {
    this.prospectTopSealPercentage = prospectTopSealPercentage;
  }

  public String getProspectTopSealComments() {
    return prospectTopSealComments;
  }

  public void setProspectTopSealComments(String prospectTopSealComments) {
    this.prospectTopSealComments = prospectTopSealComments;
  }

  public Boolean getGeophysicallyModified() {
    return geophysicallyModified;
  }

  public void setGeophysicallyModified(Boolean geophysicallyModified) {
    this.geophysicallyModified = geophysicallyModified;
  }

  public BigDecimal getGeophysicallyModifiedPercentage() {
    return geophysicallyModifiedPercentage;
  }

  public void setGeophysicallyModifiedPercentage(BigDecimal geophysicallyModifiedPercentage) {
    this.geophysicallyModifiedPercentage = geophysicallyModifiedPercentage;
  }

  public String getGeophysicallyModifiedComments() {
    return geophysicallyModifiedComments;
  }

  public void setGeophysicallyModifiedComments(String geophysicallyModifiedComments) {
    this.geophysicallyModifiedComments = geophysicallyModifiedComments;
  }

  public Boolean getRelatedToSpecificWellLocation() {
    return relatedToSpecificWellLocation;
  }

  public void setRelatedToSpecificWellLocation(Boolean relatedToSpecificWellLocation) {
    this.relatedToSpecificWellLocation = relatedToSpecificWellLocation;
  }

  public BigDecimal getWellModifiedPercentage() {
    return wellModifiedPercentage;
  }

  public void setWellModifiedPercentage(BigDecimal wellModifiedPercentage) {
    this.wellModifiedPercentage = wellModifiedPercentage;
  }

  public String getWellModifiedComments() {
    return wellModifiedComments;
  }

  public void setWellModifiedComments(String wellModifiedComments) {
    this.wellModifiedComments = wellModifiedComments;
  }
}

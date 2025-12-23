package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;

@Entity
@Table(name = "chance_of_success")
public class ChanceOfSuccess implements SubmissionDetailChildEntity {

  @Id
  @UuidGenerator
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

  private BigDecimal playTotal;

  @Column(name = "prospect_specific_trap_presence")
  private BigDecimal prospectTrapPresence;

  @Column(name = "prospect_specific_trap_presence_comments")
  private String prospectTrapPresenceComments;

  @Column(name = "prospect_specific_trap_effectiveness")
  private BigDecimal prospectTrapEffectiveness;

  @Column(name = "prospect_specific_trap_effectiveness_comments")
  private String prospectTrapEffectivenessComments;

  @Column(name = "prospect_specific_source_presence")
  private BigDecimal prospectSourcePresence;

  @Column(name = "prospect_specific_source_presence_comments")
  private String prospectSourcePresenceComments;

  @Column(name = "prospect_specific_source_effectiveness")
  private BigDecimal prospectSourceEffectiveness;

  @Column(name = "prospect_specific_source_effectiveness_comments")
  private String prospectSourceEffectivenessComments;

  @Column(name = "prospect_specific_reservoir_presence")
  private BigDecimal prospectReservoirPresence;

  @Column(name = "prospect_specific_reservoir_presence_comments")
  private String prospectReservoirPresenceComments;

  @Column(name = "prospect_specific_reservoir_effectiveness")
  private BigDecimal prospectReservoirEffectiveness;

  @Column(name = "prospect_specific_reservoir_effectiveness_comments")
  private String prospectReservoirEffectivenessComments;

  private BigDecimal prospectSpecificTotal;

  private BigDecimal overallTotal;

  private Boolean geophysicallyModified;
  private BigDecimal geophysicallyModifiedPercentage;
  private String geophysicallyModifiedComments;

  private Boolean relatedToSpecificWellLocation;
  private BigDecimal wellModifiedPercentage;
  private String wellModifiedComments;

  public UUID getId() {
    return id;
  }

  @Override
  public void setId(UUID id) {
    this.id = id;
  }

  public SubmissionDetail getSubmissionDetail() {
    return submissionDetail;
  }

  @Override
  public void setSubmissionDetail(SubmissionDetail submissionDetail) {
    this.submissionDetail = submissionDetail;
  }

  public BigDecimal getSourcePercentage() {
    return sourcePercentage;
  }

  public void setSourcePercentage(BigDecimal sourcePresence) {
    this.sourcePercentage = sourcePresence;
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

  public void setReservoirPercentage(BigDecimal reservoirPresence) {
    this.reservoirPercentage = reservoirPresence;
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

  public void setTopSealPercentage(BigDecimal topSealPresence) {
    this.topSealPercentage = topSealPresence;
  }

  public String getTopSealComments() {
    return topSealComments;
  }

  public void setTopSealComments(String topSealComments) {
    this.topSealComments = topSealComments;
  }

  public BigDecimal getPlayTotal() {
    return playTotal;
  }

  public void setPlayTotal(BigDecimal playTotal) {
    this.playTotal = playTotal;
  }

  public BigDecimal getProspectTrapPresence() {
    return prospectTrapPresence;
  }

  public void setProspectTrapPresence(BigDecimal prospectTrapPresence) {
    this.prospectTrapPresence = prospectTrapPresence;
  }

  public String getProspectTrapPresenceComments() {
    return prospectTrapPresenceComments;
  }

  public void setProspectTrapPresenceComments(String prospectTrapComments) {
    this.prospectTrapPresenceComments = prospectTrapComments;
  }

  public BigDecimal getProspectTrapEffectiveness() {
    return prospectTrapEffectiveness;
  }

  public void setProspectTrapEffectiveness(BigDecimal prospectTopSealPresence) {
    this.prospectTrapEffectiveness = prospectTopSealPresence;
  }

  public String getProspectTrapEffectivenessComments() {
    return prospectTrapEffectivenessComments;
  }

  public void setProspectTrapEffectivenessComments(String prospectTopSealComments) {
    this.prospectTrapEffectivenessComments = prospectTopSealComments;
  }

  public BigDecimal getProspectSourcePresence() {
    return prospectSourcePresence;
  }

  public void setProspectSourcePresence(BigDecimal prospectSourcePresence) {
    this.prospectSourcePresence = prospectSourcePresence;
  }

  public BigDecimal getProspectSourceEffectiveness() {
    return prospectSourceEffectiveness;
  }

  public void setProspectSourceEffectiveness(BigDecimal prospectMigrationPresence) {
    this.prospectSourceEffectiveness = prospectMigrationPresence;
  }

  public String getProspectSourceEffectivenessComments() {
    return prospectSourceEffectivenessComments;
  }

  public void setProspectSourceEffectivenessComments(String prospectMigrationComments) {
    this.prospectSourceEffectivenessComments = prospectMigrationComments;
  }

  public String getProspectSourcePresenceComments() {
    return prospectSourcePresenceComments;
  }

  public void setProspectSourcePresenceComments(String prospectSourceComments) {
    this.prospectSourcePresenceComments = prospectSourceComments;
  }

  public BigDecimal getProspectReservoirPresence() {
    return prospectReservoirPresence;
  }

  public void setProspectReservoirPresence(BigDecimal prospectReservoirPresence) {
    this.prospectReservoirPresence = prospectReservoirPresence;
  }

  public String getProspectReservoirPresenceComments() {
    return prospectReservoirPresenceComments;
  }

  public void setProspectReservoirPresenceComments(String prospectReservoirComments) {
    this.prospectReservoirPresenceComments = prospectReservoirComments;
  }

  public BigDecimal getProspectReservoirEffectiveness() {
    return prospectReservoirEffectiveness;
  }

  public void setProspectReservoirEffectiveness(BigDecimal prospectReservoirEffectiveness) {
    this.prospectReservoirEffectiveness = prospectReservoirEffectiveness;
  }

  public String getProspectReservoirEffectivenessComments() {
    return prospectReservoirEffectivenessComments;
  }

  public void setProspectReservoirEffectivenessComments(String prospectReservoirEffectivenessComments) {
    this.prospectReservoirEffectivenessComments = prospectReservoirEffectivenessComments;
  }

  public BigDecimal getProspectSpecificTotal() {
    return prospectSpecificTotal;
  }

  public void setProspectSpecificTotal(BigDecimal prospectSpecificTotal) {
    this.prospectSpecificTotal = prospectSpecificTotal;
  }

  public BigDecimal getOverallTotal() {
    return overallTotal;
  }

  public void setOverallTotal(BigDecimal overallTotal) {
    this.overallTotal = overallTotal;
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

  public void setGeophysicallyModifiedPercentage(BigDecimal geophysicallyModifiedPresence) {
    this.geophysicallyModifiedPercentage = geophysicallyModifiedPresence;
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

  public void setWellModifiedPercentage(BigDecimal wellModifiedPresence) {
    this.wellModifiedPercentage = wellModifiedPresence;
  }

  public String getWellModifiedComments() {
    return wellModifiedComments;
  }

  public void setWellModifiedComments(String wellModifiedComments) {
    this.wellModifiedComments = wellModifiedComments;
  }
}

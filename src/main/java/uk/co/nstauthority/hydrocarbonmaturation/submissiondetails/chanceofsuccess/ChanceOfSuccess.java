package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import java.util.UUID;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
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

  @Embedded
  @AttributeOverrides(
      {
          @AttributeOverride(name = "sourcePercentage", column = @Column(name = "play_source_percentage")),
          @AttributeOverride(name = "sourceComments", column = @Column(name = "play_source_comments")),
          @AttributeOverride(name = "reservoirPercentage", column = @Column(name = "play_reservoir_percentage")),
          @AttributeOverride(name = "reservoirComments", column = @Column(name = "play_reservoir_comments")),
          @AttributeOverride(name = "topSealPercentage", column = @Column(name = "play_top_seal_percentage")),
          @AttributeOverride(name = "topSealComments", column = @Column(name = "play_top_seal_comments"))
      }
  )
  private ChanceOfSuccessDetails playChanceOfSuccess;

  @Embedded
  @AttributeOverrides(
      {
          @AttributeOverride(name = "sourcePercentage", column = @Column(name = "prospect_specific_source_percentage")),
          @AttributeOverride(name = "sourceComments", column = @Column(name = "prospect_specific_source_comments")),
          @AttributeOverride(name = "reservoirPercentage", column = @Column(name = "prospect_specific_reservoir_percentage")),
          @AttributeOverride(name = "reservoirComments", column = @Column(name = "prospect_specific_reservoir_comments")),
          @AttributeOverride(name = "topSealPercentage", column = @Column(name = "prospect_specific_top_seal_percentage")),
          @AttributeOverride(name = "topSealComments", column = @Column(name = "prospect_specific_top_seal_comments"))
      }
  )
  private ProspectSpecificChanceOfSuccess prospectSpecificChanceOfSuccess;

  private Boolean geophysicallyModified;
  private String geophysicallyModifiedPercentage;
  private String geophysicallyModifiedComments;

  private Boolean relatedToSpecificWellLocation;
  private String wellModifiedPercentage;
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

  public ChanceOfSuccessDetails getPlayChanceOfSuccess() {
    return playChanceOfSuccess;
  }

  public void setPlayChanceOfSuccess(
      ChanceOfSuccessDetails playChanceOfSuccess) {
    this.playChanceOfSuccess = playChanceOfSuccess;
  }

  public ProspectSpecificChanceOfSuccess getProspectSpecificChanceOfSuccess() {
    return prospectSpecificChanceOfSuccess;
  }

  public void setProspectSpecificChanceOfSuccess(
      ProspectSpecificChanceOfSuccess prospectSpecificChanceOfSuccess) {
    this.prospectSpecificChanceOfSuccess = prospectSpecificChanceOfSuccess;
  }

  public Boolean getGeophysicallyModified() {
    return geophysicallyModified;
  }

  public void setGeophysicallyModified(Boolean geophysicallyModified) {
    this.geophysicallyModified = geophysicallyModified;
  }

  public String getGeophysicallyModifiedPercentage() {
    return geophysicallyModifiedPercentage;
  }

  public void setGeophysicallyModifiedPercentage(String geophysicallyModifiedPercentage) {
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

  public String getWellModifiedPercentage() {
    return wellModifiedPercentage;
  }

  public void setWellModifiedPercentage(String wellModifiedPercentage) {
    this.wellModifiedPercentage = wellModifiedPercentage;
  }

  public String getWellModifiedComments() {
    return wellModifiedComments;
  }

  public void setWellModifiedComments(String wellModifiedComments) {
    this.wellModifiedComments = wellModifiedComments;
  }
}

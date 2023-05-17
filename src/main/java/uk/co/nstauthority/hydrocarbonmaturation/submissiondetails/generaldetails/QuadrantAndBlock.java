package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.GeneralDetailChildEntity;

@Entity
@Table(name = "general_details_quadrant_blocks")
public class QuadrantAndBlock implements GeneralDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @JoinColumn(name = "general_details_id")
  @ManyToOne
  private GeneralDetail generalDetail;

  private String quadrantBlockReference;

  private String quadrant;

  private Integer block;

  @Enumerated(EnumType.STRING)
  private OnshoreOffshore onshoreOffshore;



  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public GeneralDetail getGeneralDetail() {
    return generalDetail;
  }

  public void setGeneralDetail(
      GeneralDetail generalDetail) {
    this.generalDetail = generalDetail;
  }

  public String getQuadrantBlockReference() {
    return quadrantBlockReference;
  }

  public void setQuadrantBlockReference(String quadrantBlockReference) {
    this.quadrantBlockReference = quadrantBlockReference;
  }

  public String getQuadrant() {
    return quadrant;
  }

  public void setQuadrant(String quadrant) {
    this.quadrant = quadrant;
  }

  public Integer getBlock() {
    return block;
  }

  public void setBlock(Integer block) {
    this.block = block;
  }

  public OnshoreOffshore getOnshoreOffshore() {
    return onshoreOffshore;
  }

  public void setOnshoreOffshore(
      OnshoreOffshore onshoreOffshore) {
    this.onshoreOffshore = onshoreOffshore;
  }
}

package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "quadrant_blocks")
class QuadrantBlock {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @JoinColumn(name = "general_details_id")
  @ManyToOne
  private GeneralDetail generalDetail;

  private String quadrantBlock;

  public QuadrantBlock() {
  }

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

  public String getQuadrantBlock() {
    return quadrantBlock;
  }

  public void setQuadrantBlock(String quadrantBlock) {
    this.quadrantBlock = quadrantBlock;
  }
}

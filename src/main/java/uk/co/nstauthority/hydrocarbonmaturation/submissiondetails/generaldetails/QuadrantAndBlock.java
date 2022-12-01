package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails;

import java.util.UUID;
import javax.persistence.Entity;
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

  private String quadrantBlock;

  private String sortKey;

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

  public String getSortKey() {
    return sortKey;
  }

  public void setSortKey(String sortKey) {
    this.sortKey = sortKey;
  }
}

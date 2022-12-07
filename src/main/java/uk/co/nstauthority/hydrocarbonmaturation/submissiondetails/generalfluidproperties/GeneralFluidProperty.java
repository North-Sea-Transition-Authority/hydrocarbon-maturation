package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

import java.math.BigDecimal;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;

@Entity
@Table(name = "general_fluid_properties")
class GeneralFluidProperty implements SubmissionDetailChildEntity {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  private BigDecimal datumDepth;

  private Integer temperatureAtDatumDepth;

  private Integer pressureAtDatumDepth;

  private Integer saturationPressureAtDatumDepth;

  private Integer waterSalinity;

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

  public BigDecimal getDatumDepth() {
    return datumDepth;
  }

  public void setDatumDepth(BigDecimal datumDepth) {
    this.datumDepth = datumDepth;
  }

  public Integer getTemperatureAtDatumDepth() {
    return temperatureAtDatumDepth;
  }

  public void setTemperatureAtDatumDepth(Integer temperatureAtDatumDepth) {
    this.temperatureAtDatumDepth = temperatureAtDatumDepth;
  }

  public Integer getPressureAtDatumDepth() {
    return pressureAtDatumDepth;
  }

  public void setPressureAtDatumDepth(Integer pressureAtDatumDepth) {
    this.pressureAtDatumDepth = pressureAtDatumDepth;
  }

  public Integer getSaturationPressureAtDatumDepth() {
    return saturationPressureAtDatumDepth;
  }

  public void setSaturationPressureAtDatumDepth(Integer saturationPressureAtDatumDepth) {
    this.saturationPressureAtDatumDepth = saturationPressureAtDatumDepth;
  }

  public Integer getWaterSalinity() {
    return waterSalinity;
  }

  public void setWaterSalinity(Integer waterSalinity) {
    this.waterSalinity = waterSalinity;
  }
}

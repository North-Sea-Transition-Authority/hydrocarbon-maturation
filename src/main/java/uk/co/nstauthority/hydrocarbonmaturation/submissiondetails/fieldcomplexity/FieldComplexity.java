package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.duplicate.SubmissionDetailChildEntity;

@Entity
@Table(name = "field_complexity")
public class FieldComplexity implements SubmissionDetailChildEntity {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "submission_detail_id")
  private SubmissionDetail submissionDetail;

  @Enumerated(EnumType.STRING)
  private ViscosityRange viscosityRange;

  @Enumerated(EnumType.STRING)
  private VerticalHeterogeneityRange verticalHeterogeneityRange;

  @Enumerated(EnumType.STRING)
  private ArealDensityRange arealDensityRange;

  @Enumerated(EnumType.STRING)
  private StructuralComplexityRange structuralComplexityRange;

  private BigDecimal calculatedFieldComplexity;

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

  public ViscosityRange getViscosityRange() {
    return viscosityRange;
  }

  public void setViscosityRange(ViscosityRange viscosityRange) {
    this.viscosityRange = viscosityRange;
  }

  public VerticalHeterogeneityRange getVerticalHeterogeneityRange() {
    return verticalHeterogeneityRange;
  }

  public void setVerticalHeterogeneityRange(VerticalHeterogeneityRange verticalHeterogeneityRange) {
    this.verticalHeterogeneityRange = verticalHeterogeneityRange;
  }

  public ArealDensityRange getArealDensityRange() {
    return arealDensityRange;
  }

  public void setArealDensityRange(ArealDensityRange arealDensityRange) {
    this.arealDensityRange = arealDensityRange;
  }

  public StructuralComplexityRange getStructuralComplexityRange() {
    return structuralComplexityRange;
  }

  public void setStructuralComplexityRange(StructuralComplexityRange structuralComplexityRange) {
    this.structuralComplexityRange = structuralComplexityRange;
  }

  public BigDecimal getCalculatedFieldComplexity() {
    return calculatedFieldComplexity;
  }

  public void setCalculatedFieldComplexity(BigDecimal fieldComplexity) {
    this.calculatedFieldComplexity = fieldComplexity;
  }
}

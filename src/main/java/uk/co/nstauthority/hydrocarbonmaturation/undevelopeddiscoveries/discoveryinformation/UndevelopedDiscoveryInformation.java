package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.ArealDensityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.StructuralComplexityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.VerticalHeterogeneityRange;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldcomplexity.ViscosityRange;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscovery;

@Entity
@Table(name = "undeveloped_discovery_information")
@Audited
public class UndevelopedDiscoveryInformation {

  @Id
  @UuidGenerator
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "undeveloped_discovery_id")
  @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
  private UndevelopedDiscovery undevelopedDiscovery;

  private LocalDate informationDate;

  private String differenceReason;

  @Column(name = "oil_in_place_p50")
  private BigDecimal oilInPlaceP50;

  @Column(name = "oil_recoverable_one_c")
  private BigDecimal oilRecoverableOneC;

  @Column(name = "oil_recoverable_two_c")
  private BigDecimal oilRecoverableTwoC;

  @Column(name = "oil_recoverable_three_c")
  private BigDecimal oilRecoverableThreeC;

  @Column(name = "gas_in_place_p50")
  private BigDecimal gasInPlaceP50;

  @Column(name = "gas_recoverable_one_c")
  private BigDecimal gasRecoverableOneC;

  @Column(name = "gas_recoverable_two_c")
  private BigDecimal gasRecoverableTwoC;

  @Column(name = "gas_recoverable_three_c")
  private BigDecimal gasRecoverableThreeC;

  private Boolean providedDiscoveryComplexity;

  private ViscosityRange viscosityRange;

  private VerticalHeterogeneityRange verticalHeterogeneityRange;

  private ArealDensityRange arealDensityRange;

  private StructuralComplexityRange structuralComplexityRange;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UndevelopedDiscovery getUndevelopedDiscovery() {
    return undevelopedDiscovery;
  }

  public void setUndevelopedDiscovery(
      UndevelopedDiscovery undevelopedDiscovery) {
    this.undevelopedDiscovery = undevelopedDiscovery;
  }

  public LocalDate getInformationDate() {
    return informationDate;
  }

  public void setInformationDate(LocalDate informationDate) {
    this.informationDate = informationDate;
  }

  public String getDifferenceReason() {
    return differenceReason;
  }

  public void setDifferenceReason(String differenceReason) {
    this.differenceReason = differenceReason;
  }

  public Boolean getProvidedDiscoveryComplexity() {
    return providedDiscoveryComplexity;
  }

  public void setProvidedDiscoveryComplexity(Boolean providedDiscoveryComplexity) {
    this.providedDiscoveryComplexity = providedDiscoveryComplexity;
  }

  public ViscosityRange getViscosityRange() {
    return viscosityRange;
  }

  public void setViscosityRange(
      ViscosityRange viscosityRange) {
    this.viscosityRange = viscosityRange;
  }

  public VerticalHeterogeneityRange getVerticalHeterogeneityRange() {
    return verticalHeterogeneityRange;
  }

  public void setVerticalHeterogeneityRange(
      VerticalHeterogeneityRange verticalHeterogeneityRange) {
    this.verticalHeterogeneityRange = verticalHeterogeneityRange;
  }

  public ArealDensityRange getArealDensityRange() {
    return arealDensityRange;
  }

  public void setArealDensityRange(
      ArealDensityRange arealDensityRange) {
    this.arealDensityRange = arealDensityRange;
  }

  public StructuralComplexityRange getStructuralComplexityRange() {
    return structuralComplexityRange;
  }

  public void setStructuralComplexityRange(
      StructuralComplexityRange structuralComplexityRange) {
    this.structuralComplexityRange = structuralComplexityRange;
  }

  public BigDecimal getOilInPlaceP50() {
    return oilInPlaceP50;
  }

  public void setOilInPlaceP50(BigDecimal oilInPlace) {
    this.oilInPlaceP50 = oilInPlace;
  }

  public BigDecimal getOilRecoverableOneC() {
    return oilRecoverableOneC;
  }

  public void setOilRecoverableOneC(BigDecimal oilRecoverableOneC) {
    this.oilRecoverableOneC = oilRecoverableOneC;
  }

  public BigDecimal getOilRecoverableTwoC() {
    return oilRecoverableTwoC;
  }

  public void setOilRecoverableTwoC(BigDecimal oilRecoverableTwoC) {
    this.oilRecoverableTwoC = oilRecoverableTwoC;
  }

  public BigDecimal getOilRecoverableThreeC() {
    return oilRecoverableThreeC;
  }

  public void setOilRecoverableThreeC(BigDecimal oilRecoverableThreeC) {
    this.oilRecoverableThreeC = oilRecoverableThreeC;
  }

  public BigDecimal getGasInPlaceP50() {
    return gasInPlaceP50;
  }

  public void setGasInPlaceP50(BigDecimal gasInPlace) {
    this.gasInPlaceP50 = gasInPlace;
  }

  public BigDecimal getGasRecoverableOneC() {
    return gasRecoverableOneC;
  }

  public void setGasRecoverableOneC(BigDecimal gasRecoverableOneC) {
    this.gasRecoverableOneC = gasRecoverableOneC;
  }

  public BigDecimal getGasRecoverableTwoC() {
    return gasRecoverableTwoC;
  }

  public void setGasRecoverableTwoC(BigDecimal gasRecoverableTwoC) {
    this.gasRecoverableTwoC = gasRecoverableTwoC;
  }

  public BigDecimal getGasRecoverableThreeC() {
    return gasRecoverableThreeC;
  }

  public void setGasRecoverableThreeC(BigDecimal gasRecoverableThreeC) {
    this.gasRecoverableThreeC = gasRecoverableThreeC;
  }
}
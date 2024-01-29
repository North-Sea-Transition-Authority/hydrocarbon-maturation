package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "barrier_baffle")
public class BarrierBaffleDetail {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "reservoir_properties_id")
  private ReservoirProperty reservoirProperty;

  @Enumerated(EnumType.STRING)
  private BarrierBaffle barrierBaffle;

  @Enumerated(EnumType.STRING)
  private VerticalLateralType verticalLateralType;

  public BarrierBaffleDetail() {
  }

  public BarrierBaffleDetail(ReservoirProperty reservoirProperty, BarrierBaffle barrierBaffle,
                             VerticalLateralType verticalLateralType) {
    this.reservoirProperty = reservoirProperty;
    this.barrierBaffle = barrierBaffle;
    this.verticalLateralType = verticalLateralType;
  }

  public UUID getId() {
    return id;
  }

  public BarrierBaffleDetail setId(UUID id) {
    this.id = id;
    return this;
  }

  public ReservoirProperty getReservoirProperty() {
    return reservoirProperty;
  }

  public BarrierBaffleDetail setReservoirProperty(
      ReservoirProperty reservoirProperty) {
    this.reservoirProperty = reservoirProperty;
    return this;
  }

  public BarrierBaffle getBarrierBaffle() {
    return barrierBaffle;
  }

  public BarrierBaffleDetail setBarrierBaffle(
      BarrierBaffle barrierBaffle) {
    this.barrierBaffle = barrierBaffle;
    return this;
  }

  public VerticalLateralType getVerticalLateralType() {
    return verticalLateralType;
  }

  public BarrierBaffleDetail setVerticalLateralType(
      VerticalLateralType verticalLateralType) {
    this.verticalLateralType = verticalLateralType;
    return this;
  }
}

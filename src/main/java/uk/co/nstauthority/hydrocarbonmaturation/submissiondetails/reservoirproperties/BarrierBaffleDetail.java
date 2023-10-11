package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

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

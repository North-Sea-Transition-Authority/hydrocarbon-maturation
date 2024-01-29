package uk.co.nstauthority.hydrocarbonmaturation.field.fieldstatushistory;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;
import uk.co.nstauthority.hydrocarbonmaturation.field.FieldStatus;

@Entity
@Table(name = "field_status_history")
public class FieldStatusHistory {

  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @JoinColumn(name = "field_identifier")
  @ManyToOne
  private Field field;

  @Enumerated(EnumType.STRING)
  private FieldStatus fieldStatus;

  private Instant statusDate;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Field getField() {
    return field;
  }

  public void setField(Field field) {
    this.field = field;
  }

  public FieldStatus getFieldStatus() {
    return fieldStatus;
  }

  public void setFieldStatus(FieldStatus fieldStatus) {
    this.fieldStatus = fieldStatus;
  }

  public Instant getStatusDate() {
    return statusDate;
  }

  public void setStatusDate(Instant statusDate) {
    this.statusDate = statusDate;
  }
}

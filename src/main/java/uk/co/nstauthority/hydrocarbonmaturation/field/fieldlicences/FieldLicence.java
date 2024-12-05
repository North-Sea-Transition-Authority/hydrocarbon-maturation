package uk.co.nstauthority.hydrocarbonmaturation.field.fieldlicences;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import org.hibernate.annotations.GenericGenerator;
import uk.co.nstauthority.hydrocarbonmaturation.field.Field;

@Entity
@Table(name = "field_licences")
public class FieldLicence {
  @Id
  @GeneratedValue(generator = "uuid")
  @GenericGenerator(name = "uuid", strategy = "uuid2")
  private UUID id;

  @ManyToOne
  @JoinColumn(name = "field_identifier")
  private Field field;

  @Column(name = "licence_prior_to_field_operator_reference")
  private String prePearsLicenceReference;

  @Column(name = "licence_prior_to_field_operator_id")
  private Integer prePearsLicenceId;

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

  public String getPrePearsLicenceReference() {
    return prePearsLicenceReference;
  }

  public void setPrePearsLicenceReference(String licenceReference) {
    this.prePearsLicenceReference = licenceReference;
  }

  public Integer getPrePearsLicenceId() {
    return prePearsLicenceId;
  }

  public void setPrePearsLicenceId(Integer licenceId) {
    this.prePearsLicenceId = licenceId;
  }
}

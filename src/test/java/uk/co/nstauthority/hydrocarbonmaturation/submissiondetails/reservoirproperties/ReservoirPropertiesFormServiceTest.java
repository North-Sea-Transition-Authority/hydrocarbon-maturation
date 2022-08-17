package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class ReservoirPropertiesFormServiceTest {

  private static ReservoirPropertiesFormService reservoirPropertiesFormService;

  @Mock
  private static ReservoirPropertiesFormValidator reservoirPropertiesFormValidator;

  @Mock
  private static ReservoirPropertiesPersistenceService reservoirPropertiesPersistenceService;

  private static SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    reservoirPropertiesFormService = new ReservoirPropertiesFormService(reservoirPropertiesFormValidator,
        reservoirPropertiesPersistenceService);

    UUID submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new ReservoirPropertiesForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    reservoirPropertiesFormService.validate(form, bindingResult, ValidationType.FULL);
    verify(reservoirPropertiesFormValidator, times(1)).validate(form, bindingResult, ValidationType.FULL);
  }

  @Test
  void sourceAndSealEntityToForm_newEntity_thenEmptyForm() {
    when(reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail)).thenReturn(Optional.empty());

    assertThat(reservoirPropertiesFormService.getReservoirPropertiesForm(submissionDetail))
        .extracting(
            ReservoirPropertiesForm::getFormationName,
            ReservoirPropertiesForm::getAgeStageOrSeries,
            ReservoirPropertiesForm::getLithology
        )
        .containsExactly(
            null,
            null,
            null
        );
  }

  @Test
  void sourceAndSealEntityToForm_existingEntity_thenPopulatedForm() {
    var reservoirProperty = getReservoirProperty(submissionDetail);
    when(reservoirPropertiesPersistenceService.getReservoirProperty(submissionDetail)).thenReturn(Optional.of(reservoirProperty));

    assertThat(reservoirPropertiesFormService.getReservoirPropertiesForm(submissionDetail))
        .extracting(
            ReservoirPropertiesForm::getFormationName,
            ReservoirPropertiesForm::getAgeStageOrSeries,
            ReservoirPropertiesForm::getLithology
        )
        .containsExactly(
            reservoirProperty.getFormationName(),
            reservoirProperty.getAgeStage(),
            reservoirProperty.getLithology()
        );
  }

  private ReservoirProperty getReservoirProperty(SubmissionDetail submissionDetail) {
    var reservoirProperty = new ReservoirProperty();
    reservoirProperty.setSubmissionDetail(submissionDetail);
    reservoirProperty.setFormationName("formation name");
    reservoirProperty.setAgeStage("age stage");
    reservoirProperty.setLithology("lithology");
    return reservoirProperty;
  }
}
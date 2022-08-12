package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@ExtendWith(MockitoExtension.class)
class TrapPropertiesFormServiceTest {

  private static TrapPropertiesFormValidator trapPropertiesFormValidator;
  private static TrapPropertiesFormService trapPropertiesFormService;
  private static TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  private static SubmissionDetail submissionDetail;


  @BeforeAll
  static void setup() {
    trapPropertiesFormValidator = mock(TrapPropertiesFormValidator.class);
    trapPropertiesPersistenceService = mock(TrapPropertiesPersistenceService.class);
    trapPropertiesFormService = new TrapPropertiesFormService(trapPropertiesFormValidator, trapPropertiesPersistenceService);

    UUID submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new TrapPropertiesForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    trapPropertiesFormService.validate(form, bindingResult);
    verify(trapPropertiesFormValidator, times(1)).validate(form, bindingResult);
  }

  @Test
  void sourceAndSealEntityToForm_newEntity_thenEmptyForm() {
    when(trapPropertiesPersistenceService.getTrapProperty(submissionDetail)).thenReturn(Optional.empty());

    assertThat(trapPropertiesFormService.getTrapPropertiesForm(submissionDetail))
        .extracting(
            TrapPropertiesForm::getTrapType,
            TrapPropertiesForm::getOtherTrapType,
            TrapPropertiesForm::getTrapDescription,
            (form) -> form.getLatitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            (form) -> form.getLatitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            (form) -> form.getLatitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            (form) -> form.getLongitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            (form) -> form.getLongitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            (form) -> form.getLongitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            TrapPropertiesForm::getLongitudeHemisphere
        )
        .containsExactly(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
  }

  @Test
  void sourceAndSealEntityToForm_existingEntity_thenPopulatedForm() {
    var trapProperty = getTrapProperty(submissionDetail);
    when(trapPropertiesPersistenceService.getTrapProperty(submissionDetail)).thenReturn(Optional.of(trapProperty));

    assertThat(trapPropertiesFormService.getTrapPropertiesForm(submissionDetail))
        .extracting(
            TrapPropertiesForm::getTrapType,
            TrapPropertiesForm::getOtherTrapType,
            TrapPropertiesForm::getTrapDescription,
            (form) -> form.getLatitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            (form) -> form.getLatitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            (form) -> form.getLatitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            (form) -> form.getLongitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            (form) -> form.getLongitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            (form) -> form.getLongitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            TrapPropertiesForm::getLongitudeHemisphere
        )
        .containsExactly(
            trapProperty.getTrapType(),
            trapProperty.getOtherTrapType(),
            trapProperty.getTrapDescription(),
            trapProperty.getLatitudeDegrees(),
            trapProperty.getLatitudeMinutes(),
            trapProperty.getLatitudeSeconds(),
            trapProperty.getLongitudeDegrees(),
            trapProperty.getLongitudeMinutes(),
            trapProperty.getLongitudeSeconds(),
            trapProperty.getLongitudeHemisphere()
        );
  }

  private TrapProperty getTrapProperty(SubmissionDetail submissionDetail) {
    return new TrapProperty()
        .setSubmissionDetail(submissionDetail)
        .setTrapType(TrapType.COMBINATION)
        .setTrapDescription("description")
        .setLatitudeDegrees(BigDecimal.valueOf(45))
        .setLatitudeMinutes(BigDecimal.valueOf(4))
        .setLatitudeSeconds(BigDecimal.valueOf(14))
        .setLongitudeDegrees(BigDecimal.valueOf(5))
        .setLongitudeMinutes(BigDecimal.valueOf(6))
        .setLongitudeSeconds(BigDecimal.valueOf(40))
        .setLongitudeHemisphere(Hemisphere.EAST);
  }
}
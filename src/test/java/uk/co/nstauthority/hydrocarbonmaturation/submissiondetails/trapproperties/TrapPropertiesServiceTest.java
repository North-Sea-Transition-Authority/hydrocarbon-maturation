package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.DecimalInput;

@ExtendWith(MockitoExtension.class)
class TrapPropertiesServiceTest {

  private static TrapPropertiesFormValidator trapPropertiesFormValidator;
  private static TrapPropertiesService trapPropertiesService;
  private static TrapPropertiesRepository trapPropertiesRepository;

  private static UUID submissionDetailId;
  private static SubmissionDetail submissionDetail;
  private static CoordinateInput latitudeCoordinate = new CoordinateInput();
  private static CoordinateInput longitudeCoordinate = new CoordinateInput();


  @BeforeAll
  static void setup() {
    trapPropertiesFormValidator = mock(TrapPropertiesFormValidator.class);
    trapPropertiesRepository = mock(TrapPropertiesRepository.class);
    trapPropertiesService = new TrapPropertiesService(trapPropertiesFormValidator, trapPropertiesRepository);

    submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);

    latitudeCoordinate.setDegrees(new DecimalInput("45"));
    latitudeCoordinate.setMinutes(new DecimalInput("4"));
    latitudeCoordinate.setSeconds(new DecimalInput("5.3"));
    longitudeCoordinate.setDegrees(new DecimalInput("30"));
    longitudeCoordinate.setMinutes(new DecimalInput("4"));
    longitudeCoordinate.setSeconds(new DecimalInput("5.3"));
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new TrapPropertiesForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    trapPropertiesService.validate(form, bindingResult);
    verify(trapPropertiesFormValidator, times(1)).validate(form, bindingResult);
  }

  @Test
  void saveTrapProperties_whenGivenAForm_verifyCreated_Entity() {
    var form = new TrapPropertiesForm();
    form.setTrapType(TrapType.COMBINATION);
    form.setTrapDescription("description");
    form.setLatitudeCoordinates(latitudeCoordinate);
    form.setLongitudeCoordinates(longitudeCoordinate);
    form.setLongitudeHemisphere(Hemisphere.EAST);

    var trapPropertiesArgumentCaptor = ArgumentCaptor.forClass(TrapProperty.class);
    trapPropertiesService.saveTrapProperties(form, submissionDetail);

    verify(trapPropertiesRepository, times(1)).save(trapPropertiesArgumentCaptor.capture());
    var savedTrapProperty = trapPropertiesArgumentCaptor.getValue();

    assertThat(savedTrapProperty)
        .extracting(
            TrapProperty::getTrapDescription,
            TrapProperty::getTrapType,
            TrapProperty::getOtherTrapType,
            TrapProperty::getLatitudeDegrees,
            TrapProperty::getLatitudeMinutes,
            TrapProperty::getLatitudeSeconds,
            TrapProperty::getLongitudeDegrees,
            TrapProperty::getLongitudeMinutes,
            TrapProperty::getLongitudeSeconds,
            TrapProperty::getLongitudeHemisphere
        )
        .containsExactly(
            form.getTrapDescription(),
            form.getTrapType(),
            form.getOtherTrapType(),
            form.getLatitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            form.getLatitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            form.getLatitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            form.getLongitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            form.getLongitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            form.getLongitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            form.getLongitudeHemisphere()
        );
  }
}
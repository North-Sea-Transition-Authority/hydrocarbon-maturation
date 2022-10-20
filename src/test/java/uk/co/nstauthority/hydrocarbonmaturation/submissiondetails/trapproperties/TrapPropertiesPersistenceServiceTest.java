package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;

@ExtendWith(MockitoExtension.class)
class TrapPropertiesPersistenceServiceTest {

  @Mock
  private TrapPropertiesRepository trapPropertiesRepository;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private SupportingDocumentService supportingDocumentService;

  private TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  private static SubmissionDetail submissionDetail;
  private static final CoordinateInput latitudeCoordinate = new CoordinateInput();
  private static final CoordinateInput longitudeCoordinate = new CoordinateInput();

  @BeforeEach
  void setup() {
    trapPropertiesPersistenceService = new TrapPropertiesPersistenceService(
        trapPropertiesRepository,
        probabilityDistributionPersistenceService,
        supportingDocumentService);

    var submissionDetailId = UUID.randomUUID();
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
  void saveTrapProperties_whenGivenAForm_verifyCreated_Entity() {
    var form = new TrapPropertiesForm();
    form.setTrapType(TrapType.COMBINATION);
    form.setTrapDescription("description");
    form.setLatitudeCoordinates(latitudeCoordinate);
    form.setLongitudeCoordinates(longitudeCoordinate);
    form.setLongitudeHemisphere(Hemisphere.EAST);

    var trapPropertiesArgumentCaptor = ArgumentCaptor.forClass(TrapProperty.class);
    trapPropertiesPersistenceService.saveTrapProperties(form, submissionDetail);

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

  @Test
  void getTrapProperty_verifyMethodCall() {
    trapPropertiesPersistenceService.getTrapProperty(submissionDetail);
    verify(trapPropertiesRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }
}
package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlock;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.QuadrantAndBlockPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SupportingDocumentService;

@ExtendWith(MockitoExtension.class)
class TrapPropertiesPersistenceServiceTest {

  @Mock
  private TrapPropertiesRepository trapPropertiesRepository;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private SupportingDocumentService supportingDocumentService;

  @Mock
  private QuadrantAndBlockPersistenceService quadrantAndBlockPersistenceService;

  private TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  private static SubmissionDetail submissionDetail;
  private static final CoordinateInput latitudeCoordinate = new CoordinateInput();
  private static final CoordinateInput longitudeCoordinate = new CoordinateInput();

  @BeforeEach
  void setup() {
    trapPropertiesPersistenceService = new TrapPropertiesPersistenceService(
        trapPropertiesRepository,
        probabilityDistributionPersistenceService,
        supportingDocumentService,
        quadrantAndBlockPersistenceService);

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
    form.getCoordinateInputForm().setLatitudeCoordinates(latitudeCoordinate);
    form.getCoordinateInputForm().setLongitudeCoordinates(longitudeCoordinate);
    form.getCoordinateInputForm().setLongitudeHemisphere(Hemisphere.EAST);
    form.setDatum(ReservoirDatum.ED50);
    var quadBlock = new QuadrantAndBlock();
    quadBlock.setQuadrantBlock("quadBlock");
    var quadBlockId = UUID.randomUUID();
    form.setCentreOfInterestQuadrantBlock(quadBlockId.toString());
    quadBlock.setId(quadBlockId);

    when(quadrantAndBlockPersistenceService.getQuadrantBlockById(quadBlockId))
        .thenReturn(Optional.of(quadBlock));

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
            TrapProperty::getLongitudeHemisphere,
            TrapProperty::getDatum,
            trapProperty -> trapProperty.getCentreOfInterestQuadrantBlock().getQuadrantBlock(),
            trapProperty -> trapProperty.getCentreOfInterestQuadrantBlock().getId().toString()
        )
        .containsExactly(
            form.getTrapDescription(),
            form.getTrapType(),
            form.getOtherTrapType(),
            form.getCoordinateInputForm().getLatitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            form.getCoordinateInputForm().getLatitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            form.getCoordinateInputForm().getLatitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            form.getCoordinateInputForm().getLongitudeCoordinates().getDegrees().createBigDecimalOrNull(),
            form.getCoordinateInputForm().getLongitudeCoordinates().getMinutes().createBigDecimalOrNull(),
            form.getCoordinateInputForm().getLongitudeCoordinates().getSeconds().createBigDecimalOrNull(),
            form.getCoordinateInputForm().getLongitudeHemisphere(),
            form.getDatum(),
            "quadBlock",
            form.getCentreOfInterestQuadrantBlock()
        );
  }

  @Test
  void getTrapProperty_verifyMethodCall() {
    trapPropertiesPersistenceService.getTrapProperty(submissionDetail);
    verify(trapPropertiesRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }
}
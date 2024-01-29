package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.coordinateinput.CoordinateInput;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.file.FileDocumentType;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.supportingdocuments.SubmissionSupportingDocumentService;

@ExtendWith(MockitoExtension.class)
class TrapPropertiesPersistenceServiceTest {

  @Mock
  private TrapPropertiesRepository trapPropertiesRepository;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Mock
  private SubmissionSupportingDocumentService supportingDocumentService;

  private TrapPropertiesPersistenceService trapPropertiesPersistenceService;

  private static final SubmissionDetail submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  private static final CoordinateInput latitudeCoordinate = new CoordinateInput();
  private static final CoordinateInput longitudeCoordinate = new CoordinateInput();

  @BeforeEach
  void setup() {
    trapPropertiesPersistenceService = new TrapPropertiesPersistenceService(
        trapPropertiesRepository,
        probabilityDistributionPersistenceService,
        supportingDocumentService);

    latitudeCoordinate.setDegrees(new DecimalInput("45"));
    latitudeCoordinate.setMinutes(new DecimalInput("4"));
    latitudeCoordinate.setSeconds(new DecimalInput("5.3"));
    longitudeCoordinate.setDegrees(new DecimalInput("30"));
    longitudeCoordinate.setMinutes(new DecimalInput("4"));
    longitudeCoordinate.setSeconds(new DecimalInput("5.3"));
  }

  @Test
  void saveTrapProperties_whenProspect_whenGivenAForm_verifyCreated_Entity() {
    var form = new TrapPropertiesForm();
    form.setTrapType(TrapType.COMBINATION);
    form.setTrapDescription("description");
    form.getCoordinateInputForm().setLatitudeCoordinates(latitudeCoordinate);
    form.getCoordinateInputForm().setLongitudeCoordinates(longitudeCoordinate);
    form.getCoordinateInputForm().setLongitudeHemisphere(Hemisphere.EAST);
    form.setDatum(ReservoirDatum.ED50);
    form.setDepthToCrest(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setDepthToSpillPoint(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setProductiveArea(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());

    var trapPropertiesArgumentCaptor = ArgumentCaptor.forClass(TrapProperty.class);
    trapPropertiesPersistenceService.saveTrapProperties(form, submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_PROSPECT,
            form.getDepthToCrest(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_PROSPECT,
            form.getDepthToSpillPoint(),
            submissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_PROSPECT,
            form.getProductiveArea(),
            submissionDetail);

    verify(supportingDocumentService, times(1)).saveDocuments(submissionDetail, form.getProspectDepthMap(), FileDocumentType.PROSPECT_DEPTH_MAP);
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
            TrapProperty::getCentreOfInterestQuadrantBlock
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
            form.getCentreOfInterestQuadrantBlock()
        );
  }

  @Test
  void saveTrapProperties_whenDiscovery_whenGivenAForm_verifyCreated_Entity() {
    var discoverySubmissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();

    var form = new TrapPropertiesForm();
    form.setTrapType(TrapType.COMBINATION);
    form.setTrapDescription("description");
    form.getCoordinateInputForm().setLatitudeCoordinates(latitudeCoordinate);
    form.getCoordinateInputForm().setLongitudeCoordinates(longitudeCoordinate);
    form.getCoordinateInputForm().setLongitudeHemisphere(Hemisphere.EAST);
    form.setDatum(ReservoirDatum.ED50);
    form.setDepthToCrest(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setDepthToSpillPoint(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());
    form.setProductiveArea(ProbabilityDistributionTestUtil.getValidConstantProbabilityDistributionForm());

    var trapPropertiesArgumentCaptor = ArgumentCaptor.forClass(TrapProperty.class);
    trapPropertiesPersistenceService.saveTrapProperties(form, discoverySubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.DEPTH_TO_CREST_DISCOVERY,
            form.getDepthToCrest(),
            discoverySubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.DEPTH_TO_SPILL_POINT_DISCOVERY,
            form.getDepthToSpillPoint(),
            discoverySubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1))
        .saveProbabilityDistributionWhenNotNull(
            ProbabilityDistributionFieldInstance.PRODUCTIVE_AREA_DISCOVERY,
            form.getProductiveArea(),
            discoverySubmissionDetail);

    verify(supportingDocumentService, times(1)).saveDocuments(discoverySubmissionDetail, form.getProspectDepthMap(), FileDocumentType.PROSPECT_DEPTH_MAP);
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
            TrapProperty::getCentreOfInterestQuadrantBlock
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
            form.getCentreOfInterestQuadrantBlock()
        );
  }

  @Test
  void getTrapProperty_verifyMethodCall() {
    trapPropertiesPersistenceService.getTrapProperty(submissionDetail);
    verify(trapPropertiesRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }

  @Test
  void getCentreOfInterestBlockFromSubmissionDetail_verifyMethodCall() {
    trapPropertiesPersistenceService.getCentreOfInterestBlockFromSubmissionDetail(submissionDetail);

    verify(trapPropertiesRepository, times(1))
        .findBySubmissionDetail(submissionDetail);
  }
}
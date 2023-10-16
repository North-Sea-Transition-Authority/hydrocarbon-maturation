package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class GeneralFluidPropertyFormServiceTest {
  @Mock
  private GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService;

  @InjectMocks
  private GeneralFluidPropertyFormService generalFluidPropertyFormService;

  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
  }

  @Test
  void generalFluidPropertyEntityToForm_newEntity_thenEmptyForm() {
    when(generalFluidPropertyPersistenceService.getGeneralFluidProperty(reservoirSubmissionDetail)).thenReturn(Optional.empty());

    assertThat(generalFluidPropertyFormService.getGeneralFluidPropertyForm(reservoirSubmissionDetail))
        .extracting(
            theForm -> theForm.getDatumDepth().asBigDecimal(),
            theForm -> theForm.getTemperatureAtDatumDepth().asInteger(),
            theForm -> theForm.getInitialTemperature().asInteger(),
            theForm -> theForm.getPressureAtDatumDepth().asInteger(),
            theForm -> theForm.getInitialPressure().asInteger(),
            theForm -> theForm.getSaturationPressureAtDatumDepth().asInteger(),
            theForm -> theForm.getWaterSalinity().asInteger())
        .containsExactly(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
        );
  }

  @Test
  void generalFluidPropertyEntityToForm_existingEntity_thenPopulatedForm() {
    var generalFluidProperty = new GeneralFluidProperty();
    generalFluidProperty.setWaterSalinity(1);
    generalFluidProperty.setTemperatureAtDatumDepth(3);
    generalFluidProperty.setPressureAtDatumDepth(2);
    generalFluidProperty.setDatumDepth(BigDecimal.ONE);
    generalFluidProperty.setSaturationPressureAtDatumDepth(4);
    generalFluidProperty.setInitialTemperature(5);
    generalFluidProperty.setInitialPressure(6);

    when(generalFluidPropertyPersistenceService.getGeneralFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(generalFluidProperty));

    assertThat(generalFluidPropertyFormService.getGeneralFluidPropertyForm(reservoirSubmissionDetail))
        .extracting(
            theForm -> theForm.getDatumDepth().createBigDecimalOrNull(),
            theForm -> theForm.getWaterSalinity().createIntegerOrNull(),
            theForm -> theForm.getSaturationPressureAtDatumDepth().createIntegerOrNull(),
            theForm -> theForm.getPressureAtDatumDepth().createIntegerOrNull(),
            theForm -> theForm.getInitialTemperature().createIntegerOrNull(),
            theForm -> theForm.getTemperatureAtDatumDepth().createIntegerOrNull(),
            theForm -> theForm.getInitialPressure().createIntegerOrNull()
        ).containsExactly(
            BigDecimal.ONE,
            1,
            4,
            2,
            5,
            3,
            6
        );
  }

  @Test
  void generalFluidPropertyEntityToForm_existingEmptyEntity_thenEmptyForm() {
    var generalFluidProperty = new GeneralFluidProperty();

    when(generalFluidPropertyPersistenceService.getGeneralFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(generalFluidProperty));

    assertThat(generalFluidPropertyFormService.getGeneralFluidPropertyForm(reservoirSubmissionDetail))
        .extracting(
            theForm -> theForm.getDatumDepth().createBigDecimalOrNull(),
            theForm -> theForm.getWaterSalinity().createIntegerOrNull(),
            theForm -> theForm.getSaturationPressureAtDatumDepth().createIntegerOrNull(),
            theForm -> theForm.getPressureAtDatumDepth().createIntegerOrNull(),
            theForm -> theForm.getInitialTemperature().createIntegerOrNull(),
            theForm -> theForm.getTemperatureAtDatumDepth().createIntegerOrNull(),
            theForm -> theForm.getInitialPressure().createIntegerOrNull())
        .containsOnlyNulls();
  }
}
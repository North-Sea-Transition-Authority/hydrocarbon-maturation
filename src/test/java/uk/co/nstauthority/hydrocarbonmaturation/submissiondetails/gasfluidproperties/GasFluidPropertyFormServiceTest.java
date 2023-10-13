package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

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
class GasFluidPropertyFormServiceTest {


  @Mock
  private GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService;

  @InjectMocks
  private GasFluidPropertyFormService gasFluidPropertyFormService;

  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
  }

  @Test
  void gasFluidPropertyEntityToForm_newEntity_thenEmptyForm() {
    when(gasFluidPropertyPersistenceService.getGasFluidProperty(reservoirSubmissionDetail)).thenReturn(Optional.empty());

    assertThat(gasFluidPropertyFormService.getGasFluidPropertyForm(reservoirSubmissionDetail))
        .extracting(
            theForm -> theForm.getGasCo2().asBigDecimal(),
            theForm -> theForm.getGasH2s().asBigDecimal(),
            theForm -> theForm.getGasSpecificGravity().asBigDecimal(),
            theForm -> theForm.getGasN2().asBigDecimal(),
            GasFluidPropertyForm::getCondensatePresent,
            GasFluidPropertyForm::getAdviseOtherGasesConcentration,
            theForm -> theForm.getCondensateGravity().asBigDecimal(),
            theForm -> theForm.getCondensateOtherInclWax().asBigDecimal(),
            theForm -> theForm.getCondensateSulphur().asBigDecimal(),
            theForm -> theForm.getCondensateViscosity().asBigDecimal(),
            theForm -> theForm.getCondensateWax().asBigDecimal())
        .containsExactly(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            null,
            null,
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
        );
  }

  @Test
  void gasFluidPropertyEntityToForm_existingEntity_thenPopulatedForm() {
    var gasFluidProperty = new GasFluidProperty();
    gasFluidProperty.setGasN2(BigDecimal.ONE);
    gasFluidProperty.setGasCo2(BigDecimal.ONE);
    gasFluidProperty.setGasSpecificGravity(BigDecimal.ONE);
    gasFluidProperty.setGasH2s(BigDecimal.ONE);
    gasFluidProperty.setCondensatePresent(true);
    gasFluidProperty.setAdviseOtherGasesConcentration("advise");
    gasFluidProperty.setCondensateSulphur(BigDecimal.TEN);
    gasFluidProperty.setCondensateWax(BigDecimal.TEN);
    gasFluidProperty.setCondensateViscosity(BigDecimal.TEN);
    gasFluidProperty.setCondensateOtherInclWax(BigDecimal.TEN);
    gasFluidProperty.setCondensateGravity(BigDecimal.TEN);

    when(gasFluidPropertyPersistenceService.getGasFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(gasFluidProperty));

    assertThat(gasFluidPropertyFormService.getGasFluidPropertyForm(reservoirSubmissionDetail))
        .extracting(
            theForm -> theForm.getGasCo2().createBigDecimalOrNull(),
            theForm -> theForm.getGasH2s().createBigDecimalOrNull(),
            theForm -> theForm.getGasSpecificGravity().createBigDecimalOrNull(),
            theForm -> theForm.getGasN2().createBigDecimalOrNull(),
            GasFluidPropertyForm::getCondensatePresent,
            GasFluidPropertyForm::getAdviseOtherGasesConcentration,
            theForm -> theForm.getCondensateGravity().createBigDecimalOrNull(),
            theForm -> theForm.getCondensateOtherInclWax().createBigDecimalOrNull(),
            theForm -> theForm.getCondensateSulphur().createBigDecimalOrNull(),
            theForm -> theForm.getCondensateViscosity().createBigDecimalOrNull(),
            theForm -> theForm.getCondensateWax().createBigDecimalOrNull())
        .containsExactly(
            BigDecimal.ONE,
            BigDecimal.ONE,
            BigDecimal.ONE,
            BigDecimal.ONE,
            true,
            "advise",
            BigDecimal.TEN,
            BigDecimal.TEN,
            BigDecimal.TEN,
            BigDecimal.TEN,
            BigDecimal.TEN
        );
  }

  @Test
  void gasFluidPropertyEntityToForm_existingEmptyEntity_thenEmptyForm() {
    var gasFluidProperty = new GasFluidProperty();

    when(gasFluidPropertyPersistenceService.getGasFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(gasFluidProperty));

    assertThat(gasFluidPropertyFormService.getGasFluidPropertyForm(reservoirSubmissionDetail))
        .extracting(
            theForm -> theForm.getGasCo2().createBigDecimalOrNull(),
            theForm -> theForm.getGasH2s().createBigDecimalOrNull(),
            theForm -> theForm.getGasSpecificGravity().createBigDecimalOrNull(),
            theForm -> theForm.getGasN2().createBigDecimalOrNull(),
            GasFluidPropertyForm::getCondensatePresent,
            GasFluidPropertyForm::getAdviseOtherGasesConcentration,
            theForm -> theForm.getCondensateGravity().createBigDecimalOrNull(),
            theForm -> theForm.getCondensateOtherInclWax().createBigDecimalOrNull(),
            theForm -> theForm.getCondensateSulphur().createBigDecimalOrNull(),
            theForm -> theForm.getCondensateViscosity().createBigDecimalOrNull(),
            theForm -> theForm.getCondensateWax().createBigDecimalOrNull())
        .containsOnlyNulls();
  }
}
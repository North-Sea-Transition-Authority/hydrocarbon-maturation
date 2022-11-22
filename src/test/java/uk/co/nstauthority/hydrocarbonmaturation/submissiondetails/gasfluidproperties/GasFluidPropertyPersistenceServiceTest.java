package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.decimalinput.DecimalInput;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class GasFluidPropertyPersistenceServiceTest {

  @Mock
  private GasFluidPropertyRepository gasFluidPropertyRepository;

  @InjectMocks
  private GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  }

  @Test
  void saveGasFluidProperty_whenGivenAForm_verifyCreatedEntity() {
    var form = getValidForm();

    var gasFluidPropertyArgumentCaptor = ArgumentCaptor.forClass(GasFluidProperty.class);

    gasFluidPropertyPersistenceService.saveGasFluidProperty(form, submissionDetail);

    verify(gasFluidPropertyRepository, times(1)).save(gasFluidPropertyArgumentCaptor.capture());
    var savedGasFluidProperty = (GasFluidProperty) gasFluidPropertyArgumentCaptor.getValue();

    assertThat(savedGasFluidProperty)
        .extracting(
            GasFluidProperty::getGasH2s,
            GasFluidProperty::getGasCo2,
            GasFluidProperty::getGasN2,
            GasFluidProperty::getGasSpecificGravity,
            GasFluidProperty::getCondensatePresent,
            GasFluidProperty::getAdviseOtherGasesConcentration,
            GasFluidProperty::getCondensateGravity,
            GasFluidProperty::getCondensateOtherInclWax,
            GasFluidProperty::getCondensateSulphur,
            GasFluidProperty::getCondensateViscosity,
            GasFluidProperty::getCondensateWax)
        .containsExactly(
            form.getGasH2s().createBigDecimalOrNull(),
            form.getGasCo2().createBigDecimalOrNull(),
            form.getGasN2().createBigDecimalOrNull(),
            form.getGasSpecificGravity().createBigDecimalOrNull(),
            form.getCondensatePresent(),
            form.getAdviseOtherGasesConcentration(),
            form.getCondensateGravity().createBigDecimalOrNull(),
            form.getCondensateOtherInclWax().createBigDecimalOrNull(),
            form.getCondensateSulphur().createBigDecimalOrNull(),
            form.getCondensateViscosity().createBigDecimalOrNull(),
            form.getCondensateWax().createBigDecimalOrNull()
        );
  }

  @Test
  void saveGasFluidProperty_whenGivenEmptyForm_verifyCreatedEntity() {
    var form = new GasFluidPropertyForm();

    var gasFluidPropertyArgumentCaptor = ArgumentCaptor.forClass(GasFluidProperty.class);

    gasFluidPropertyPersistenceService.saveGasFluidProperty(form, submissionDetail);

    verify(gasFluidPropertyRepository, times(1)).save(gasFluidPropertyArgumentCaptor.capture());
    var savedGasFluidProperty = (GasFluidProperty) gasFluidPropertyArgumentCaptor.getValue();

    assertThat(savedGasFluidProperty)
        .extracting(
            GasFluidProperty::getGasH2s,
            GasFluidProperty::getGasCo2,
            GasFluidProperty::getGasN2,
            GasFluidProperty::getGasSpecificGravity,
            GasFluidProperty::getCondensatePresent,
            GasFluidProperty::getAdviseOtherGasesConcentration,
            GasFluidProperty::getCondensateGravity,
            GasFluidProperty::getCondensateOtherInclWax,
            GasFluidProperty::getCondensateSulphur,
            GasFluidProperty::getCondensateViscosity,
            GasFluidProperty::getCondensateWax)
        .containsExactly(
            form.getGasH2s().createBigDecimalOrNull(),
            form.getGasCo2().createBigDecimalOrNull(),
            form.getGasN2().createBigDecimalOrNull(),
            form.getGasSpecificGravity().createBigDecimalOrNull(),
            form.getCondensatePresent(),
            form.getAdviseOtherGasesConcentration(),
            form.getCondensateGravity().createBigDecimalOrNull(),
            form.getCondensateOtherInclWax().createBigDecimalOrNull(),
            form.getCondensateSulphur().createBigDecimalOrNull(),
            form.getCondensateViscosity().createBigDecimalOrNull(),
            form.getCondensateWax().createBigDecimalOrNull()
        );
  }

  private GasFluidPropertyForm getValidForm() {
    var form = new GasFluidPropertyForm();
    form.setGasN2(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setGasH2s(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setGasSpecificGravity(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setGasCo2(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setCondensatePresent(true);
    form.setAdviseOtherGasesConcentration("advise");
    return form;
  }
}
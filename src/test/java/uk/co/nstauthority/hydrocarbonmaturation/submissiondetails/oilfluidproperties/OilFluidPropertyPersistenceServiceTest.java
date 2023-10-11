package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class OilFluidPropertyPersistenceServiceTest {

  @Mock
  private OilFluidPropertyRepository oilFluidPropertyRepository;

  @InjectMocks
  private OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService;

  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();
  }

  @Test
  void saveOilFluidProperty_whenGivenAForm_verifyCreatedEntity() {
    var form = getValidForm();

    var oilFluidPropertyArgumentCaptor = ArgumentCaptor.forClass(OilFluidProperty.class);

    oilFluidPropertyPersistenceService.saveOilFluidProperty(form, reservoirSubmissionDetail);

    verify(oilFluidPropertyRepository, times(1)).save(oilFluidPropertyArgumentCaptor.capture());
    var savedOilFluidProperty = (OilFluidProperty) oilFluidPropertyArgumentCaptor.getValue();

    assertThat(savedOilFluidProperty)
        .extracting(
            OilFluidProperty::getOilGravity,
            OilFluidProperty::getOilSulphur,
            OilFluidProperty::getOilViscosity,
            OilFluidProperty::getOilOtherWaxAppearanceTemperature,
            OilFluidProperty::getOilWax)
        .containsExactly(
            form.getOilGravity().createIntegerOrNull(),
            form.getOilSulphur().createIntegerOrNull(),
            form.getOilViscosity().createBigDecimalOrNull(),
            form.getOilOtherWaxAppearanceTemperature().createIntegerOrNull(),
            form.getOilWax().createBigDecimalOrNull()
        );
  }

  @Test
  void saveOilFluidProperty_whenGivenEmptyForm_verifyCreatedEntity() {
    var form = new OilFluidPropertyForm();

    var oilFluidPropertyArgumentCaptor = ArgumentCaptor.forClass(OilFluidProperty.class);

    oilFluidPropertyPersistenceService.saveOilFluidProperty(form, reservoirSubmissionDetail);

    verify(oilFluidPropertyRepository, times(1)).save(oilFluidPropertyArgumentCaptor.capture());
    var savedOilFluidProperty = (OilFluidProperty) oilFluidPropertyArgumentCaptor.getValue();

    assertThat(savedOilFluidProperty)
        .extracting(
            OilFluidProperty::getOilGravity,
            OilFluidProperty::getOilSulphur,
            OilFluidProperty::getOilViscosity,
            OilFluidProperty::getOilOtherWaxAppearanceTemperature,
            OilFluidProperty::getOilWax)
        .containsOnlyNulls();
  }

  private OilFluidPropertyForm getValidForm() {
    var form = new OilFluidPropertyForm();
    form.setOilWax(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setOilViscosity(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setOilSulphur(DecimalInput.createDecimalInput(new BigDecimal("10")));
    form.setOilGravity(DecimalInput.createDecimalInput(new BigDecimal("25")));
    form.setOilOtherWaxAppearanceTemperature(DecimalInput.createDecimalInput(new BigDecimal("33")));
    return form;
  }
}
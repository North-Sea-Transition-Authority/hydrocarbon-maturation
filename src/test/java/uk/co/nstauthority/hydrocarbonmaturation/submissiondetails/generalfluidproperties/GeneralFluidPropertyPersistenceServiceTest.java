package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

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
class GeneralFluidPropertyPersistenceServiceTest {

  @Mock
  private GeneralFluidPropertyRepository generalFluidPropertyRepository;

  @InjectMocks
  private GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  }

  @Test
  void saveGeneralFluidProperty_whenGivenAForm_verifyCreatedEntity() {
    var form = getValidForm();

    var generalFluidPropertyArgumentCaptor = ArgumentCaptor.forClass(GeneralFluidProperty.class);

    generalFluidPropertyPersistenceService.saveGeneralFluidProperty(form, submissionDetail);

    verify(generalFluidPropertyRepository, times(1)).save(generalFluidPropertyArgumentCaptor.capture());
    var savedGeneralFluidProperty = (GeneralFluidProperty) generalFluidPropertyArgumentCaptor.getValue();

    assertThat(savedGeneralFluidProperty)
        .extracting(
            GeneralFluidProperty::getDatumDepth,
            GeneralFluidProperty::getPressureAtDatumDepth,
            GeneralFluidProperty::getSaturationPressureAtDatumDepth,
            GeneralFluidProperty::getWaterSalinity,
            GeneralFluidProperty::getTemperatureAtDatumDepth)
        .containsExactly(
            form.getDatumDepth().createBigDecimalOrNull(),
            form.getPressureAtDatumDepth().createIntegerOrNull(),
            form.getSaturationPressureAtDatumDepth().createIntegerOrNull(),
            form.getWaterSalinity().createIntegerOrNull(),
            form.getTemperatureAtDatumDepth().createIntegerOrNull()
        );
  }

  @Test
  void saveGeneralFluidProperty_whenGivenEmptyForm_verifyCreatedEntity() {
    var form = new GeneralFluidPropertyForm();

    var generalFluidPropertyArgumentCaptor = ArgumentCaptor.forClass(GeneralFluidProperty.class);

    generalFluidPropertyPersistenceService.saveGeneralFluidProperty(form, submissionDetail);

    verify(generalFluidPropertyRepository, times(1)).save(generalFluidPropertyArgumentCaptor.capture());
    var savedGeneralFluidProperty = (GeneralFluidProperty) generalFluidPropertyArgumentCaptor.getValue();

    assertThat(savedGeneralFluidProperty)
        .extracting(
            GeneralFluidProperty::getDatumDepth,
            GeneralFluidProperty::getPressureAtDatumDepth,
            GeneralFluidProperty::getSaturationPressureAtDatumDepth,
            GeneralFluidProperty::getWaterSalinity,
            GeneralFluidProperty::getTemperatureAtDatumDepth)
        .containsExactly(
            form.getDatumDepth().createBigDecimalOrNull(),
            form.getPressureAtDatumDepth().createIntegerOrNull(),
            form.getSaturationPressureAtDatumDepth().createIntegerOrNull(),
            form.getWaterSalinity().createIntegerOrNull(),
            form.getTemperatureAtDatumDepth().createIntegerOrNull()
        );
  }

  private GeneralFluidPropertyForm getValidForm() {
    var form = new GeneralFluidPropertyForm();
    form.setDatumDepth(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setPressureAtDatumDepth(DecimalInput.createDecimalInput(BigDecimal.TEN));
    form.setTemperatureAtDatumDepth(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setWaterSalinity(DecimalInput.createDecimalInput(BigDecimal.ONE));
    form.setSaturationPressureAtDatumDepth(DecimalInput.createDecimalInput(BigDecimal.ONE));
    return form;
  }
}
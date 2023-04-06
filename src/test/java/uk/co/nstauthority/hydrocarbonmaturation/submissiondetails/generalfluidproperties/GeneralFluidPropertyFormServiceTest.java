package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalfluidproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class GeneralFluidPropertyFormServiceTest {

  @Mock
  private GeneralFluidPropertyFormValidator generalFluidPropertyFormValidator;

  @Mock
  private GeneralFluidPropertyPersistenceService generalFluidPropertyPersistenceService;

  @InjectMocks
  private GeneralFluidPropertyFormService generalFluidPropertyFormService;

  private UUID submissionDetailId;
  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_DISCOVERY_FROM_PROSPECT);
    submissionDetailId = submissionDetail.getId();
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new GeneralFluidPropertyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var captor = ArgumentCaptor.forClass(SubmissionFormValidatorHints.class);

    generalFluidPropertyFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);

    verify(generalFluidPropertyFormValidator, times(1))
        .validate(eq(form), eq(bindingResult), captor.capture());

    assertThat(captor.getValue())
        .extracting(SubmissionFormValidatorHints::getSubmissionDetail, SubmissionFormValidatorHints::getValidationType)
        .containsExactly(submissionDetail, ValidationType.FULL);
  }

  @Test
  void generalFluidPropertyEntityToForm_newEntity_thenEmptyForm() {
    when(generalFluidPropertyPersistenceService.getGeneralFluidProperty(submissionDetail)).thenReturn(Optional.empty());

    assertThat(generalFluidPropertyFormService.getGeneralFluidPropertyForm(submissionDetail))
        .extracting(
            theForm -> theForm.getDatumDepth().asBigDecimal(),
            theForm -> theForm.getTemperatureAtDatumDepth().asInteger(),
            theForm -> theForm.getPressureAtDatumDepth().asInteger(),
            theForm -> theForm.getSaturationPressureAtDatumDepth().asInteger(),
            theForm -> theForm.getWaterSalinity().asInteger())
        .containsExactly(
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

    when(generalFluidPropertyPersistenceService.getGeneralFluidProperty(submissionDetail))
        .thenReturn(Optional.of(generalFluidProperty));

    assertThat(generalFluidPropertyFormService.getGeneralFluidPropertyForm(submissionDetail))
        .extracting(
            theForm -> theForm.getDatumDepth().createBigDecimalOrNull(),
            theForm -> theForm.getWaterSalinity().createIntegerOrNull(),
            theForm -> theForm.getSaturationPressureAtDatumDepth().createIntegerOrNull(),
            theForm -> theForm.getPressureAtDatumDepth().createIntegerOrNull(),
            theForm -> theForm.getTemperatureAtDatumDepth().createIntegerOrNull())
        .containsExactly(
            BigDecimal.ONE,
            1,
            4,
            2,
            3
        );
  }

  @Test
  void generalFluidPropertyEntityToForm_existingEmptyEntity_thenEmptyForm() {
    var generalFluidProperty = new GeneralFluidProperty();

    when(generalFluidPropertyPersistenceService.getGeneralFluidProperty(submissionDetail))
        .thenReturn(Optional.of(generalFluidProperty));

    assertThat(generalFluidPropertyFormService.getGeneralFluidPropertyForm(submissionDetail))
        .extracting(
            theForm -> theForm.getDatumDepth().createBigDecimalOrNull(),
            theForm -> theForm.getWaterSalinity().createIntegerOrNull(),
            theForm -> theForm.getSaturationPressureAtDatumDepth().createIntegerOrNull(),
            theForm -> theForm.getPressureAtDatumDepth().createIntegerOrNull(),
            theForm -> theForm.getTemperatureAtDatumDepth().createIntegerOrNull())
        .containsOnlyNulls();
  }
}
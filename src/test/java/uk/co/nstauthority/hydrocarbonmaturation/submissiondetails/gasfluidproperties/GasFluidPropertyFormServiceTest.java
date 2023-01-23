package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.gasfluidproperties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Assertions;
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
class GasFluidPropertyFormServiceTest {

  @Mock
  private GasFluidPropertyFormValidator gasFluidPropertyFormValidator;

  @Mock
  private GasFluidPropertyPersistenceService gasFluidPropertyPersistenceService;

  @InjectMocks
  private GasFluidPropertyFormService gasFluidPropertyFormService;

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
    var form = new GasFluidPropertyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var captor = ArgumentCaptor.forClass(SubmissionFormValidatorHints.class);

    gasFluidPropertyFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);

    verify(gasFluidPropertyFormValidator, times(1))
        .validate(eq(form), eq(bindingResult), captor.capture());

    Assertions.assertThat(captor.getValue())
        .extracting(SubmissionFormValidatorHints::getSubmissionDetail,
            SubmissionFormValidatorHints::getValidationType)
        .containsExactly(submissionDetail, ValidationType.FULL);
  }

  @Test
  void gasFluidPropertyEntityToForm_newEntity_thenEmptyForm() {
    when(gasFluidPropertyPersistenceService.getGasFluidProperty(submissionDetail)).thenReturn(Optional.empty());

    assertThat(gasFluidPropertyFormService.getGasFluidPropertyForm(submissionDetail))
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

    when(gasFluidPropertyPersistenceService.getGasFluidProperty(submissionDetail))
        .thenReturn(Optional.of(gasFluidProperty));

    assertThat(gasFluidPropertyFormService.getGasFluidPropertyForm(submissionDetail))
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

    when(gasFluidPropertyPersistenceService.getGasFluidProperty(submissionDetail))
        .thenReturn(Optional.of(gasFluidProperty));

    assertThat(gasFluidPropertyFormService.getGasFluidPropertyForm(submissionDetail))
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
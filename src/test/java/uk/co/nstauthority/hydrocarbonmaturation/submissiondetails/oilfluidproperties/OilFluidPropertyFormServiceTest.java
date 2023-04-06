package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

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
class OilFluidPropertyFormServiceTest {

  @Mock
  private OilFluidPropertyFormValidator oilFluidPropertyFormValidator;

  @Mock
  private OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService;

  @InjectMocks
  private OilFluidPropertyFormService oilFluidPropertyFormService;

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
    var form = new OilFluidPropertyForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");

    var captor = ArgumentCaptor.forClass(SubmissionFormValidatorHints.class);

    oilFluidPropertyFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);

    verify(oilFluidPropertyFormValidator, times(1))
        .validate(eq(form), eq(bindingResult), captor.capture());

    assertThat(captor.getValue())
        .extracting(
            SubmissionFormValidatorHints::getSubmissionDetail,
            SubmissionFormValidatorHints::getValidationType)
        .containsExactly(submissionDetail, ValidationType.FULL);
  }

  @Test
  void oilFluidPropertyEntityToForm_newEntity_thenEmptyForm() {
    when(oilFluidPropertyPersistenceService.getOilFluidProperty(submissionDetail)).thenReturn(Optional.empty());

    assertThat(oilFluidPropertyFormService.getOilFluidPropertyForm(submissionDetail))
        .extracting(
            theForm -> theForm.getOilGravity().asInteger(),
            theForm -> theForm.getOilSulphur().asInteger(),
            theForm -> theForm.getOilViscosity().asBigDecimal(),
            theForm -> theForm.getOilWax().asBigDecimal(),
            theForm -> theForm.getOilOtherWaxAppearanceTemperature().asInteger())
        .containsExactly(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
        );
  }

  @Test
  void oilFluidPropertyEntityToForm_existingEntity_thenPopulatedForm() {
    var oilFluidProperty = new OilFluidProperty();
    oilFluidProperty.setOilGravity(1);
    oilFluidProperty.setOilSulphur(2);
    oilFluidProperty.setOilViscosity(BigDecimal.ONE);
    oilFluidProperty.setOilWax(BigDecimal.TEN);
    oilFluidProperty.setOilOtherWaxAppearanceTemperature(3);

    when(oilFluidPropertyPersistenceService.getOilFluidProperty(submissionDetail))
        .thenReturn(Optional.of(oilFluidProperty));

    assertThat(oilFluidPropertyFormService.getOilFluidPropertyForm(submissionDetail))
        .extracting(
            theForm -> theForm.getOilGravity().asInteger(),
            theForm -> theForm.getOilSulphur().asInteger(),
            theForm -> theForm.getOilViscosity().asBigDecimal(),
            theForm -> theForm.getOilWax().asBigDecimal(),
            theForm -> theForm.getOilOtherWaxAppearanceTemperature().asInteger())
        .containsExactly(
            Optional.of(1),
            Optional.of(2),
            Optional.of(BigDecimal.ONE),
            Optional.of(BigDecimal.TEN),
            Optional.of(3)
        );
  }

  @Test
  void oilFluidPropertyEntityToForm_existingEmptyEntity_thenEmptyForm() {
    var oilFluidProperty = new OilFluidProperty();

    when(oilFluidPropertyPersistenceService.getOilFluidProperty(submissionDetail))
        .thenReturn(Optional.of(oilFluidProperty));

    assertThat(oilFluidPropertyFormService.getOilFluidPropertyForm(submissionDetail))
        .extracting(
            theForm -> theForm.getOilGravity().createIntegerOrNull(),
            theForm -> theForm.getOilSulphur().createIntegerOrNull(),
            theForm -> theForm.getOilViscosity().createBigDecimalOrNull(),
            theForm -> theForm.getOilWax().createBigDecimalOrNull(),
            theForm -> theForm.getOilOtherWaxAppearanceTemperature().createIntegerOrNull())
        .containsOnlyNulls();
  }
}
package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.oilfluidproperties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class OilFluidPropertyFormServiceTest {

  @Mock
  private OilFluidPropertyFormValidator oilFluidPropertyFormValidator;

  @Mock
  private OilFluidPropertyPersistenceService oilFluidPropertyPersistenceService;

  @InjectMocks
  private OilFluidPropertyFormService oilFluidPropertyFormService;

  private SubmissionDetail submissionDetail;
  private ReservoirSubmissionDetail reservoirSubmissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(SubmissionType.CREATE_COP);
    reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail(submissionDetail);
  }

  @Test
  void oilFluidPropertyEntityToForm_newEntity_thenEmptyForm() {
    when(oilFluidPropertyPersistenceService.getOilFluidProperty(reservoirSubmissionDetail)).thenReturn(Optional.empty());

    assertThat(oilFluidPropertyFormService.getOilFluidPropertyForm(reservoirSubmissionDetail))
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

    when(oilFluidPropertyPersistenceService.getOilFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(oilFluidProperty));

    assertThat(oilFluidPropertyFormService.getOilFluidPropertyForm(reservoirSubmissionDetail))
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

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      mode = EnumSource.Mode.INCLUDE,
      names = {"CREATE_CSR", "CREATE_FDP", "CREATE_FDPA", "CREATE_COP"}
  )
  void oilFluidPropertyEntityToForm_existingEmptyEntity_thenEmptyForm(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    var oilFluidProperty = new OilFluidProperty();

    when(oilFluidPropertyPersistenceService.getOilFluidProperty(reservoirSubmissionDetail))
        .thenReturn(Optional.of(oilFluidProperty));

    assertThat(oilFluidPropertyFormService.getOilFluidPropertyForm(reservoirSubmissionDetail))
        .extracting(
            theForm -> theForm.getOilGravity().createIntegerOrNull(),
            theForm -> theForm.getOilSulphur().createIntegerOrNull(),
            theForm -> theForm.getOilViscosity().createBigDecimalOrNull(),
            theForm -> theForm.getOilWax().createBigDecimalOrNull(),
            theForm -> theForm.getOilOtherWaxAppearanceTemperature().createIntegerOrNull())
        .containsOnlyNulls();
  }
}
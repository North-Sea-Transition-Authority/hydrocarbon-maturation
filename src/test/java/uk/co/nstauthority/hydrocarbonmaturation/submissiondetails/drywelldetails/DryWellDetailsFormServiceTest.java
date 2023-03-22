package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.WellQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.WellDetailsForm;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.WellDetailsFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class DryWellDetailsFormServiceTest {

  @Mock
  private DryWellDetailsFormValidator dryWellDetailsFormValidator;

  @Mock
  private DryWellDetailsPersistenceService dryWellDetailsPersistenceService;

  @Mock
  private SearchSelectorService searchSelectorService;

  @Mock
  private WellQueryService wellQueryService;

  @Mock
  private WellDetailsFormService wellDetailsFormService;

  @InjectMocks
  private DryWellDetailsFormService dryWellDetailsFormService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewPostDrillDryWellSubmissionDetail();
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new DryWellDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var validationType = ValidationType.FULL;

    var captor = ArgumentCaptor.forClass(SubmissionFormValidatorHints.class);

    dryWellDetailsFormService.validate(form, bindingResult, validationType, submissionDetail);

    verify(dryWellDetailsFormValidator, times(1))
        .validate(eq(form), eq(bindingResult), captor.capture());
    Assertions.assertThat(captor.getValue())
        .extracting(SubmissionFormValidatorHints::getSubmissionDetail,
            SubmissionFormValidatorHints::getValidationType)
        .containsExactly(submissionDetail, ValidationType.FULL);
  }

  @Test
  void dryWellDetailEntityToForm_newEntity_thenEmptyForm() {
    when(dryWellDetailsPersistenceService.getDryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var form = dryWellDetailsFormService.getDryWellDetailsForm(submissionDetail);

    assertThat(form).hasAllNullFieldsOrPropertiesExcept("drillDetailsForm", "dryHoleCost");

    assertThat(form.getDrillDetailsForm()).hasAllNullFieldsOrPropertiesExcept("coordinateInputForm");

    assertThat(form.getDryHoleCost().createBigDecimalOrNull()).isNull();
  }

  @Test
  void dryWellDetailEntityToForm_existingEntity_thenPopulatedForm() {
    var dryWellDetail = getDryWellDetail(submissionDetail);
    when(dryWellDetailsPersistenceService.getDryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(dryWellDetail));

    var wellDetailForm = new WellDetailsForm();
    when(wellDetailsFormService.drillDetailToForm(dryWellDetail)).thenReturn(wellDetailForm);

    var form = dryWellDetailsFormService.getDryWellDetailsForm(submissionDetail);

    var detailForm = form.getDrillDetailsForm();
    assertThat(form)
        .extracting(
            DryWellDetailsForm::getDrillDetailsForm,
            DryWellDetailsForm::getDryHoleType,
            DryWellDetailsForm::getDryHoleDescription,
            DryWellDetailsForm::getDryHoleScope,
            DryWellDetailsForm::getDryHoleComments,
            form1 -> form1.getDryHoleCost().asBigDecimal().get(),
            DryWellDetailsForm::getMigrationFailureCategory,
            DryWellDetailsForm::getReservoirFailureCategory,
            DryWellDetailsForm::getSourceFailureCategory,
            DryWellDetailsForm::getSealFailureCategory,
            DryWellDetailsForm::getTrapFailureCategory)
        .containsExactly(
            detailForm,
            dryWellDetail.getDryHoleType(),
            dryWellDetail.getDryHoleDescription(),
            dryWellDetail.getDryHoleScope(),
            dryWellDetail.getDryHoleComments(),
            dryWellDetail.getDryHoleCost(),
            dryWellDetail.getMigrationFailureCategory(),
            dryWellDetail.getReservoirFailureCategory(),
            dryWellDetail.getSourceFailureCategory(),
            dryWellDetail.getSealFailureCategory(),
            dryWellDetail.getTrapFailureCategory()
        );
  }

  @Test
  void dryWellDetailEntityToForm_existingEmptyEntity_thenPopulatedForm() {
    var dryWellDetail = new DryWellDetail();
    when(dryWellDetailsPersistenceService.getDryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(dryWellDetail));

    var wellDetailForm = new WellDetailsForm();
    when(wellDetailsFormService.drillDetailToForm(dryWellDetail)).thenReturn(wellDetailForm);


    var form = dryWellDetailsFormService.getDryWellDetailsForm(submissionDetail);
    var detailForm = form.getDrillDetailsForm();
    assertThat(form)
        .extracting(
            DryWellDetailsForm::getDrillDetailsForm,
            theForm -> detailForm.getDataAcquisitionProgramme(),
            theForm -> detailForm.getGeophysicalUpdate(),
            DryWellDetailsForm::getDryHoleType,
            DryWellDetailsForm::getDryHoleDescription,
            DryWellDetailsForm::getDryHoleScope,
            DryWellDetailsForm::getDryHoleComments,
            form1 -> form1.getDryHoleCost().asBigDecimal(),
            DryWellDetailsForm::getMigrationFailureCategory,
            DryWellDetailsForm::getReservoirFailureCategory,
            DryWellDetailsForm::getSourceFailureCategory,
            DryWellDetailsForm::getSealFailureCategory,
            DryWellDetailsForm::getTrapFailureCategory)
        .containsExactly(
            detailForm,
            dryWellDetail.getDataAcquisitionProgramme(),
            dryWellDetail.getGeophysicalUpdateCommentary(),
            dryWellDetail.getDryHoleType(),
            dryWellDetail.getDryHoleDescription(),
            dryWellDetail.getDryHoleScope(),
            dryWellDetail.getDryHoleComments(),
            Optional.empty(),
            dryWellDetail.getMigrationFailureCategory(),
            dryWellDetail.getReservoirFailureCategory(),
            dryWellDetail.getSourceFailureCategory(),
            dryWellDetail.getSealFailureCategory(),
            dryWellDetail.getTrapFailureCategory()
        );
  }

  @Test
  void getPreSelectedDryWell_verifyMethodCall() {
    var expectedMap = Map.of("k1", "v1");
    when(wellDetailsFormService.getPreSelectedWell("well"))
        .thenReturn(expectedMap);
    var result = dryWellDetailsFormService.getPreSelectedWell("well");

    verify(wellDetailsFormService).getPreSelectedWell("well");
    assertThat(result).isEqualTo(expectedMap);
  }

  private DryWellDetail getDryWellDetail(SubmissionDetail submissionDetail) {
    var dryWellDetail = new DryWellDetail();
    dryWellDetail.setSubmissionDetail(submissionDetail);
    dryWellDetail.setWellId(1);
    dryWellDetail.setWellReference("dryWellName");
    dryWellDetail.setDryHoleComments("dryHoleComments");
    dryWellDetail.setDryHoleScope("dryHoleScope");
    dryWellDetail.setTopReservoirDatum(ReservoirDatum.ED50);
    dryWellDetail.setDryHoleDescription("description");
    dryWellDetail.setTopReservoirLatitudeDegrees(BigDecimal.TEN);
    dryWellDetail.setTopReservoirLatitudeMinutes(BigDecimal.TEN);
    dryWellDetail.setTopReservoirLatitudeSeconds(BigDecimal.TEN);
    dryWellDetail.setTopReservoirLongitudeDegrees(BigDecimal.ONE);
    dryWellDetail.setTopReservoirLongitudeMinutes(BigDecimal.ONE);
    dryWellDetail.setTopReservoirLongitudeSeconds(BigDecimal.ONE);
    dryWellDetail.setTopReservoirLongitudeHemisphere(Hemisphere.EAST);
    dryWellDetail.setDataAcquisitionProgramme("data");
    dryWellDetail.setGeophysicalUpdateCommentary("update");
    dryWellDetail.setTotalDepthFormation("formation");
    dryWellDetail.setDryHoleType(DryHoleType.GAS);
    dryWellDetail.setDryHoleCost(BigDecimal.ONE);
    dryWellDetail.setTotalDepthFormationComments("Depth comments");
    dryWellDetail.setTrapFailureCategory(TrapFailureCategory.STRATIGRAPHIC_TRAP_COMPONENTS_NOT_PRESENT);
    dryWellDetail.setMigrationFailureCategory(MigrationFailureCategory.PROSPECT_NOT_ON_MIGRATION);
    dryWellDetail.setSourceFailureCategory(SourceFailureCategory.SOURCE_NOT_MATURE);
    dryWellDetail.setSealFailureCategory(SealFailureCategory.SEAL_LITHOLOGY_PRESENT_POOR_QUALITY);
    dryWellDetail.setReservoirFailureCategory(ReservoirFailureCategory.RESERVOIR_INTERVAL_NOT_PRESENT);
    return dryWellDetail;
  }
}
package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drywelldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import uk.co.nstauthority.hydrocarbonmaturation.energyportal.well.DryWellQueryService;
import uk.co.nstauthority.hydrocarbonmaturation.fds.searchselector.SearchSelectorService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;
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
  private DryWellQueryService dryWellQueryService;

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

    dryWellDetailsFormService.validate(form, bindingResult, validationType);

    verify(dryWellDetailsFormValidator, times(1))
        .validate(form, bindingResult, validationType);
  }

  @Test
  void dryWellDetailEntityToForm_newEntity_thenEmptyForm() {
    when(dryWellDetailsPersistenceService.getDryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var form = dryWellDetailsFormService.getDryWellDetailsForm(submissionDetail);

    assertThat(form).hasAllNullFieldsOrPropertiesExcept("coordinateInputForm", "dryHoleCost");

    assertThat(form.getDryHoleCost().createBigDecimalOrNull()).isNull();
  }

  @Test
  void dryWellDetailEntityToForm_existingEntity_thenPopulatedForm() {
    var dryWellDetail = getDryWellDetail(submissionDetail);
    when(dryWellDetailsPersistenceService.getDryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(dryWellDetail));

    var form = dryWellDetailsFormService.getDryWellDetailsForm(submissionDetail);

    assertThat(form)
        .extracting(
            DryWellDetailsForm::getDryWell,
            theForm -> theForm.getCoordinateInputForm().getLatitudeCoordinates().getDegrees().asBigDecimal().get(),
            theForm -> theForm.getCoordinateInputForm().getLatitudeCoordinates().getMinutes().asBigDecimal().get(),
            theForm -> theForm.getCoordinateInputForm().getLatitudeCoordinates().getSeconds().asBigDecimal().get(),
            theForm -> theForm.getCoordinateInputForm().getLongitudeCoordinates().getDegrees().asBigDecimal().get(),
            theForm -> theForm.getCoordinateInputForm().getLongitudeCoordinates().getMinutes().asBigDecimal().get(),
            theForm -> theForm.getCoordinateInputForm().getLongitudeCoordinates().getSeconds().asBigDecimal().get(),
            theForm -> theForm.getCoordinateInputForm().getLongitudeHemisphere(),
            DryWellDetailsForm::getReservoirDatum,
            DryWellDetailsForm::getTotalDepthFormation,
            DryWellDetailsForm::getTotalDepthFormationComments,
            DryWellDetailsForm::getDataAcquisitionProgramme,
            DryWellDetailsForm::getGeophysicalUpdate,
            DryWellDetailsForm::getDryHoleType,
            DryWellDetailsForm::getDryHoleDescription,
            DryWellDetailsForm::getDryHoleScope,
            DryWellDetailsForm::getDryHoleComments,
            form1 -> form1.getDryHoleCost().asBigDecimal().get())
        .containsExactly(
            dryWellDetail.getDryWellName(),
            dryWellDetail.getLatitudeDegrees(),
            dryWellDetail.getLatitudeMinutes(),
            dryWellDetail.getLatitudeMinutes(),
            dryWellDetail.getLongitudeDegrees(),
            dryWellDetail.getLongitudeMinutes(),
            dryWellDetail.getLongitudeSeconds(),
            dryWellDetail.getLongitudeHemisphere(),
            dryWellDetail.getDatum(),
            dryWellDetail.getTotalDepthFormation(),
            dryWellDetail.getTotalDepthFormationComments(),
            dryWellDetail.getDataAcquisitionProgramme(),
            dryWellDetail.getGeophysicalUpdateCommentary(),
            dryWellDetail.getDryHoleType(),
            dryWellDetail.getDryHoleDescription(),
            dryWellDetail.getDryHoleScope(),
            dryWellDetail.getDryHoleComments(),
            dryWellDetail.getDryHoleCost()
        );
  }

  @Test
  void dryWellDetailEntityToForm_existingEmptyEntity_thenPopulatedForm() {
    var dryWellDetail = new DryWellDetail();
    when(dryWellDetailsPersistenceService.getDryWellDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(dryWellDetail));

    var form = dryWellDetailsFormService.getDryWellDetailsForm(submissionDetail);

    assertThat(form)
        .extracting(
            DryWellDetailsForm::getDryWell,
            theForm -> theForm.getCoordinateInputForm().getLatitudeCoordinates().getDegrees().asBigDecimal(),
            theForm -> theForm.getCoordinateInputForm().getLatitudeCoordinates().getMinutes().asBigDecimal(),
            theForm -> theForm.getCoordinateInputForm().getLatitudeCoordinates().getSeconds().asBigDecimal(),
            theForm -> theForm.getCoordinateInputForm().getLongitudeCoordinates().getDegrees().asBigDecimal(),
            theForm -> theForm.getCoordinateInputForm().getLongitudeCoordinates().getMinutes().asBigDecimal(),
            theForm -> theForm.getCoordinateInputForm().getLongitudeCoordinates().getSeconds().asBigDecimal(),
            theForm -> theForm.getCoordinateInputForm().getLongitudeHemisphere(),
            DryWellDetailsForm::getReservoirDatum,
            DryWellDetailsForm::getTotalDepthFormation,
            DryWellDetailsForm::getTotalDepthFormationComments,
            DryWellDetailsForm::getDataAcquisitionProgramme,
            DryWellDetailsForm::getGeophysicalUpdate,
            DryWellDetailsForm::getDryHoleType,
            DryWellDetailsForm::getDryHoleDescription,
            DryWellDetailsForm::getDryHoleScope,
            DryWellDetailsForm::getDryHoleComments,
            form1 -> form1.getDryHoleCost().asBigDecimal())
        .containsExactly(
            dryWellDetail.getDryWellName(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            dryWellDetail.getLongitudeHemisphere(),
            dryWellDetail.getDatum(),
            dryWellDetail.getTotalDepthFormation(),
            dryWellDetail.getTotalDepthFormationComments(),
            dryWellDetail.getDataAcquisitionProgramme(),
            dryWellDetail.getGeophysicalUpdateCommentary(),
            dryWellDetail.getDryHoleType(),
            dryWellDetail.getDryHoleDescription(),
            dryWellDetail.getDryHoleScope(),
            dryWellDetail.getDryHoleComments(),
            Optional.empty()
        );
  }

  private DryWellDetail getDryWellDetail(SubmissionDetail submissionDetail) {
    var dryWellDetail = new DryWellDetail();
    dryWellDetail.setSubmissionDetail(submissionDetail);
    dryWellDetail.setDryWellId("dryWellId");
    dryWellDetail.setDryWellName("dryWellName");
    dryWellDetail.setDryHoleComments("dryHoleComments");
    dryWellDetail.setDryHoleScope("dryHoleScope");
    dryWellDetail.setDatum(ReservoirDatum.ED50);
    dryWellDetail.setDryHoleDescription("description");
    dryWellDetail.setLatitudeDegrees(BigDecimal.TEN);
    dryWellDetail.setLatitudeMinutes(BigDecimal.TEN);
    dryWellDetail.setLatitudeSeconds(BigDecimal.TEN);
    dryWellDetail.setLongitudeDegrees(BigDecimal.ONE);
    dryWellDetail.setLongitudeMinutes(BigDecimal.ONE);
    dryWellDetail.setLongitudeSeconds(BigDecimal.ONE);
    dryWellDetail.setLongitudeHemisphere(Hemisphere.EAST);
    dryWellDetail.setDataAcquisitionProgramme("data");
    dryWellDetail.setGeophysicalUpdateCommentary("update");
    dryWellDetail.setTotalDepthFormation("formation");
    dryWellDetail.setDryHoleType(DryHoleType.GAS);
    dryWellDetail.setDryHoleCost(BigDecimal.ONE);
    dryWellDetail.setTotalDepthFormationComments("Depth comments");
    return dryWellDetail;
  }
}
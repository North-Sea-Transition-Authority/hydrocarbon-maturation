package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.ReservoirDatum;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.drilldetails.WellDetailsFormService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.trapproperties.Hemisphere;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.SubmissionFormValidatorHints;
import uk.co.nstauthority.hydrocarbonmaturation.validationutil.ValidationType;

@ExtendWith(MockitoExtension.class)
class DiscoveryWellDetailsFormServiceTest {

  @Mock
  private DiscoveryWellDetailsFormValidator discoveryWellDetailsFormValidator;

  @Mock
  private DiscoveryWellDetailsPersistenceService discoveryWellDetailsPersistenceService;

  @Mock
  private WellDetailsFormService wellDetailsFormService;

  @InjectMocks
  private DiscoveryWellDetailsFormService discoveryWellDetailsFormService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewDiscoverySubmissionDetail();
  }

  @Test
  void getPreselectedDryWell_verifyMethodCall() {
    discoveryWellDetailsFormService.getPreSelectedWell("well");
    verify(wellDetailsFormService, times(1))
        .getPreSelectedWell("well");
  }

  @Test
  void validate_verifyMethodCall() {
    var form = new DiscoveryWellDetailsForm();
    var bindingResult = new BeanPropertyBindingResult(form, "form");
    var captor = ArgumentCaptor.forClass(SubmissionFormValidatorHints.class);

    discoveryWellDetailsFormService.validate(form, bindingResult, ValidationType.FULL, submissionDetail);
    verify(discoveryWellDetailsFormValidator, times(1))
        .validate(eq(form), eq(bindingResult), captor.capture());
    assertThat(captor.getValue())
        .extracting(SubmissionFormValidatorHints::getSubmissionDetail,
            SubmissionFormValidatorHints::getValidationType)
        .containsExactly(submissionDetail, ValidationType.FULL);
  }

  @Test
  void getDiscoveryWellDetailsForm_noDetail_assertEmptyForm() {
    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail)).thenReturn(
        Optional.empty());
    var form = discoveryWellDetailsFormService.getDiscoveryWellDetailsForm(submissionDetail);

    assertThat(form)
        .hasAllNullFieldsOrPropertiesExcept(
            "wellDetailsForm",
            "dryHoleCost",
            "successRelatedCosts"
        );
    assertThat(form.getWellDetailsForm())
        .hasAllNullFieldsOrPropertiesExcept("coordinateInputForm");
  }

  @Test
  void getDiscoveryWellDetailsForm_detailPresentEmpty_assertEmptyForm() {
    var discoveryWellDetail = new DiscoveryWellDetail();

    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail)).thenReturn(
        Optional.of(discoveryWellDetail));
    var form = discoveryWellDetailsFormService.getDiscoveryWellDetailsForm(submissionDetail);

    assertThat(form)
        .hasAllNullFieldsOrPropertiesExcept(
            "dryHoleCost",
            "successRelatedCosts"
        );
  }

  @Test
  void getDiscoveryWellDetailsForm_detailPresent_assertFilledForm() {
    var discoveryWellDetail = getDiscoveryWellDetail();

    when(discoveryWellDetailsPersistenceService.getDiscoveryWellDetailBySubmissionDetail(submissionDetail)).thenReturn(
        Optional.of(discoveryWellDetail));
    when(wellDetailsFormService.drillDetailToForm(discoveryWellDetail)).thenCallRealMethod();
    var form = discoveryWellDetailsFormService.getDiscoveryWellDetailsForm(submissionDetail);

    assertThat(form)
        .extracting(
            theForm -> theForm.getWellDetailsForm().getCoordinateInputForm().getLongitudeCoordinates().getDegrees().asBigDecimal(),
            theForm -> theForm.getWellDetailsForm().getCoordinateInputForm().getLongitudeCoordinates().getMinutes().asBigDecimal(),
            theForm -> theForm.getWellDetailsForm().getCoordinateInputForm().getLongitudeCoordinates().getSeconds().asBigDecimal(),
            theForm -> theForm.getWellDetailsForm().getCoordinateInputForm().getLatitudeCoordinates().getDegrees().asBigDecimal(),
            theForm -> theForm.getWellDetailsForm().getCoordinateInputForm().getLatitudeCoordinates().getMinutes().asBigDecimal(),
            theForm -> theForm.getWellDetailsForm().getCoordinateInputForm().getLatitudeCoordinates().getSeconds().asBigDecimal(),
            theForm -> theForm.getWellDetailsForm().getCoordinateInputForm().getLongitudeHemisphere(),
            theForm -> theForm.getWellDetailsForm().getWell(),
            theForm -> theForm.getWellDetailsForm().getDataAcquisitionProgramme(),
            theForm -> theForm.getWellDetailsForm().getGeophysicalUpdate(),
            theForm -> theForm.getWellDetailsForm().getReservoirDatum(),
            theForm -> theForm.getWellDetailsForm().getTotalDepthFormation(),
            theForm -> theForm.getWellDetailsForm().getTotalDepthFormationComments(),
            DiscoveryWellDetailsForm::getChanceOfCommercialityComments,
            DiscoveryWellDetailsForm::getDiscoveryStatus,
            DiscoveryWellDetailsForm::getHydrocarbonsDiscovered,
            theForm -> theForm.getDryHoleCost().asBigDecimal(),
            theForm -> theForm.getSuccessRelatedCosts().asBigDecimal())
        .containsExactly(
            Optional.of(discoveryWellDetail.getTopReservoirLongitudeDegrees()),
            Optional.of(discoveryWellDetail.getTopReservoirLongitudeMinutes()),
            Optional.of(discoveryWellDetail.getTopReservoirLongitudeSeconds()),
            Optional.of(discoveryWellDetail.getTopReservoirLatitudeDegrees()),
            Optional.of(discoveryWellDetail.getTopReservoirLatitudeMinutes()),
            Optional.of(discoveryWellDetail.getTopReservoirLatitudeSeconds()),
            discoveryWellDetail.getTopReservoirLongitudeHemisphere(),
            discoveryWellDetail.getWellId().toString(),
            discoveryWellDetail.getDataAcquisitionProgramme(),
            discoveryWellDetail.getGeophysicalUpdateCommentary(),
            discoveryWellDetail.getTopReservoirDatum(),
            discoveryWellDetail.getTotalDepthFormation(),
            discoveryWellDetail.getTotalDepthFormationComments(),
            discoveryWellDetail.getChanceOfCommercialityComments(),
            discoveryWellDetail.getDiscoveryStatus(),
            discoveryWellDetail.getHydrocarbonsDiscovered(),
            Optional.of(discoveryWellDetail.getTotalWellCost()),
            Optional.of(discoveryWellDetail.getSuccessRelatedCosts())
        );
  }

  DiscoveryWellDetail getDiscoveryWellDetail() {
    var discoveryWellDetail = new DiscoveryWellDetail();
    discoveryWellDetail.setSubmissionDetail(submissionDetail);
    discoveryWellDetail.setWellId(1);
    discoveryWellDetail.setWellReference("well ref");
    discoveryWellDetail.setTopReservoirDatum(ReservoirDatum.ED50);
    discoveryWellDetail.setTopReservoirLatitudeDegrees(BigDecimal.TEN);
    discoveryWellDetail.setTopReservoirLatitudeMinutes(BigDecimal.TEN);
    discoveryWellDetail.setTopReservoirLatitudeSeconds(BigDecimal.TEN);
    discoveryWellDetail.setTopReservoirLongitudeDegrees(BigDecimal.ONE);
    discoveryWellDetail.setTopReservoirLongitudeMinutes(BigDecimal.ONE);
    discoveryWellDetail.setTopReservoirLongitudeSeconds(BigDecimal.ONE);
    discoveryWellDetail.setTopReservoirLongitudeHemisphere(Hemisphere.EAST);
    discoveryWellDetail.setDataAcquisitionProgramme("data");
    discoveryWellDetail.setGeophysicalUpdateCommentary("update");
    discoveryWellDetail.setTotalDepthFormation("formation");
    discoveryWellDetail.setTotalDepthFormationComments("Depth comments");
    discoveryWellDetail.setSuccessRelatedCosts(BigDecimal.ONE);
    discoveryWellDetail.setTotalWellCost(BigDecimal.ONE);
    discoveryWellDetail.setChanceOfCommercialityComments("comments");
    discoveryWellDetail.setDiscoveryStatus(DiscoveryStatus.TECHNICAL_DISCOVERY);
    discoveryWellDetail.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
    return discoveryWellDetail;
  }
}
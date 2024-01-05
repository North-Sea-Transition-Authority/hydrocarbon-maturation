package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fieldvolumetrics.fieldoilcasevolumetrics;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation.GeneralReservoirInformationService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailPersistenceService;

@ExtendWith(MockitoExtension.class)
class VolumetricTotalsServiceTest {
  @Mock
  private GeneralReservoirInformationService generalReservoirInformationService;
  @Mock
  private ReservoirSubmissionDetailPersistenceService reservoirSubmissionDetailPersistenceService;
  @InjectMocks
  private VolumetricTotalsService volumetricTotalsService;

  @Test
  void isOilCaseOnly_providesReservoirSubmissionDetails_providesReservoirSubmissionDetails_allOilCase_thenTrue() {
    var oilOnlyDetail1 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.OIL_ONLY);
    var oilOnlyDetail2 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.OIL_ONLY);
    var oilOnlyDetail3 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.OIL_ONLY);

    var reservoirSubmissionDetails = List.of(oilOnlyDetail1, oilOnlyDetail2, oilOnlyDetail3);
    assertTrue(volumetricTotalsService.isOilCaseOnly(reservoirSubmissionDetails));
  }

  @Test
  void isOilCaseOnly_providesReservoirSubmissionDetails_allOilCase_thenTrue() {
    var oilOnlyDetail1 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.OIL_ONLY);
    var oilOnlyDetail2 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.OIL_ONLY);
    var oilOnlyDetail3 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.OIL_ONLY);

    var reservoirSubmissionDetails = List.of(oilOnlyDetail1, oilOnlyDetail2, oilOnlyDetail3);
    assertTrue(volumetricTotalsService.isOilCaseOnly(reservoirSubmissionDetails));
  }

  @Test
  void isOilCaseOnly_providesReservoirSubmissionDetails_notAllOilCase_thenFalse() {
    var oilOnlyDetail1 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.OIL_ONLY);
    var oilOnlyDetail2 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.OIL_ONLY);
    var nonOilOnlyDetail = new ReservoirSubmissionDetail();

    var reservoirSubmissionDetails = List.of(oilOnlyDetail1, oilOnlyDetail2, nonOilOnlyDetail);
    assertFalse(volumetricTotalsService.isOilCaseOnly(reservoirSubmissionDetails));
  }

  @Test
  void isGasCaseOnly_providesReservoirSubmissionDetails_allGasCase_thenTrue() {
    var gasOnlyDetail1 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.GAS_ONLY);
    var gasOnlyDetail2 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.GAS_ONLY);
    var gasOnlyDetail3 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.GAS_ONLY);

    var reservoirSubmissionDetails = List.of(gasOnlyDetail1, gasOnlyDetail2, gasOnlyDetail3);
    assertTrue(volumetricTotalsService.isGasCaseOnly(reservoirSubmissionDetails));
  }

  @Test
  void isGasCaseOnly_providesReservoirSubmissionDetails_notAllGasCase_thenFalse() {
    var gasOnlyDetail1 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.GAS_ONLY);
    var gasOnlyDetail2 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.GAS_ONLY);
    var nonGasCaseDetail = new ReservoirSubmissionDetail();

    var reservoirSubmissionDetails = List.of(gasOnlyDetail1, gasOnlyDetail2, nonGasCaseDetail);
    assertFalse(volumetricTotalsService.isGasCaseOnly(reservoirSubmissionDetails));
  }

  @Test
  void isCombinedCase_providesReservoirSubmissionDetailsoneCombinedCase_thenTrue() {
    var combinedDetail = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    var detail1 = new ReservoirSubmissionDetail();
    var detail2 = new ReservoirSubmissionDetail();

    var reservoirSubmissionDetails = List.of(combinedDetail, detail1, detail2);
    assertTrue(volumetricTotalsService.isCombinedCase(reservoirSubmissionDetails));
  }

  @Test
  void isOilCaseOnly_providesSubmissionDetail_providesSubmissionDetail_allOilCase_thenTrue() {
    var oilOnlyDetail1 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.OIL_ONLY);
    var oilOnlyDetail2 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.OIL_ONLY);
    var oilOnlyDetail3 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.OIL_ONLY);

    var reservoirSubmissionDetails = List.of(oilOnlyDetail1, oilOnlyDetail2, oilOnlyDetail3);

    var submissionDetail = mock(SubmissionDetail.class);
    when(reservoirSubmissionDetailPersistenceService
        .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    assertTrue(volumetricTotalsService.isOilCaseOnly(submissionDetail));
  }

  @Test
  void isOilCaseOnly_providesSubmissionDetail_allOilCase_thenTrue() {
    var oilOnlyDetail1 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.OIL_ONLY);
    var oilOnlyDetail2 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.OIL_ONLY);
    var oilOnlyDetail3 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.OIL_ONLY);

    var reservoirSubmissionDetails = List.of(oilOnlyDetail1, oilOnlyDetail2, oilOnlyDetail3);

    var submissionDetail = mock(SubmissionDetail.class);
    when(reservoirSubmissionDetailPersistenceService
        .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    assertTrue(volumetricTotalsService.isOilCaseOnly(submissionDetail));
  }

  @Test
  void isOilCaseOnly_providesSubmissionDetail_notAllOilCase_thenFalse() {
    var oilOnlyDetail1 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.OIL_ONLY);
    var oilOnlyDetail2 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.OIL_ONLY);
    var nonOilOnlyDetail = new ReservoirSubmissionDetail();

    var reservoirSubmissionDetails = List.of(oilOnlyDetail1, oilOnlyDetail2, nonOilOnlyDetail);

    var submissionDetail = mock(SubmissionDetail.class);
    when(reservoirSubmissionDetailPersistenceService
        .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    assertFalse(volumetricTotalsService.isOilCaseOnly(submissionDetail));
  }

  @Test
  void isGasCaseOnly_providesSubmissionDetail_allGasCase_thenTrue() {
    var gasOnlyDetail1 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.GAS_ONLY);
    var gasOnlyDetail2 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.GAS_ONLY);
    var gasOnlyDetail3 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.GAS_ONLY);

    var reservoirSubmissionDetails = List.of(gasOnlyDetail1, gasOnlyDetail2, gasOnlyDetail3);

    var submissionDetail = mock(SubmissionDetail.class);
    when(reservoirSubmissionDetailPersistenceService
        .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    assertTrue(volumetricTotalsService.isGasCaseOnly(submissionDetail));
  }

  @Test
  void isGasCaseOnly_providesSubmissionDetail_notAllGasCase_thenFalse() {
    var gasOnlyDetail1 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.GAS_ONLY);
    var gasOnlyDetail2 = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.GAS_ONLY);
    var nonGasCaseDetail = new ReservoirSubmissionDetail();

    var reservoirSubmissionDetails = List.of(gasOnlyDetail1, gasOnlyDetail2, nonGasCaseDetail);

    var submissionDetail = mock(SubmissionDetail.class);
    when(reservoirSubmissionDetailPersistenceService
        .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    assertFalse(volumetricTotalsService.isGasCaseOnly(submissionDetail));
  }

  @Test
  void isCombinedCase_providesSubmissionDetail_oneCombinedCase_thenTrue() {
    var combinedDetail = createReservoirSubmissionDetailWithMockedHydrocarbonType(HydrocarbonsDiscovered.MIXED_OIL_AND_GAS);
    var detail1 = new ReservoirSubmissionDetail();
    var detail2 = new ReservoirSubmissionDetail();

    var reservoirSubmissionDetails = List.of(combinedDetail, detail1, detail2);

    var submissionDetail = mock(SubmissionDetail.class);
    when(reservoirSubmissionDetailPersistenceService
        .getAllExtantReservoirSubmissionDetailsForSubmissionDetail(submissionDetail))
        .thenReturn(reservoirSubmissionDetails);
    assertTrue(volumetricTotalsService.isCombinedCase(submissionDetail));
  }

  private ReservoirSubmissionDetail createReservoirSubmissionDetailWithMockedHydrocarbonType(
      HydrocarbonsDiscovered hydrocarbonsDiscovered) {
    var reservoirSubmissionDetail = new ReservoirSubmissionDetail();
    if (Objects.requireNonNull(hydrocarbonsDiscovered) == HydrocarbonsDiscovered.GAS_ONLY) {
      when(generalReservoirInformationService
          .isGasOnly(reservoirSubmissionDetail))
          .thenReturn(true);
    } else if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.OIL_ONLY) {
      when(generalReservoirInformationService
          .isOilOnly(reservoirSubmissionDetail))
          .thenReturn(true);
    } else if (hydrocarbonsDiscovered == HydrocarbonsDiscovered.MIXED_OIL_AND_GAS) {
      when(generalReservoirInformationService
          .isCombinedOilAndGas(reservoirSubmissionDetail))
          .thenReturn(true);
    }
    return reservoirSubmissionDetail;
  }
}
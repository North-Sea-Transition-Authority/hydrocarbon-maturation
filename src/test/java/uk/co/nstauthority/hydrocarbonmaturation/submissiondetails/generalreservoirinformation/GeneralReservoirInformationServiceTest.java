package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generalreservoirinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionFieldInstance;
import uk.co.nstauthority.hydrocarbonmaturation.probabilitydistribution.ProbabilityDistributionPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.reservoirs.ReservoirPersistenceService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.reservoirsubmissiondetails.ReservoirSubmissionDetailsTestUtils;

@ExtendWith(MockitoExtension.class)
class GeneralReservoirInformationServiceTest {

  @Mock
  private ReservoirHydrocarbonsRepository reservoirHydrocarbonsRepository;

  @Mock
  private ReservoirPersistenceService reservoirPersistenceService;

  @Mock
  private ProbabilityDistributionPersistenceService probabilityDistributionPersistenceService;

  @Captor
  private ArgumentCaptor<ReservoirHydrocarbons> reservoirHydrocarbonsArgumentCaptor;

  @InjectMocks
  private GeneralReservoirInformationService generalReservoirInformationService;

  private final ReservoirSubmissionDetail reservoirSubmissionDetail = ReservoirSubmissionDetailsTestUtils.createReservoirSubmissionDetail();

  @Test
  void saveGeneralReservoirInformation() {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName("Reservoir Name");
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);

    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    when(reservoirHydrocarbonsRepository.findReservoirHydrocarbonsByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    generalReservoirInformationService.saveGeneralReservoirInformation(form, reservoirSubmissionDetail);

    verify(reservoirPersistenceService, times(1))
        .saveReservoir(reservoirSubmissionDetail.getReservoir(), form.getReservoirName());
    verify(reservoirHydrocarbonsRepository, times(1)).save(reservoirHydrocarbonsArgumentCaptor.capture());
    assertThat(reservoirHydrocarbonsArgumentCaptor.getValue())
        .extracting(
            ReservoirHydrocarbons::getId,
            ReservoirHydrocarbons::getReservoirSubmissionDetail,
            ReservoirHydrocarbons::getHydrocarbonsDiscovered)
        .containsExactly(
            reservoirHydrocarbons.getId(),
            reservoirSubmissionDetail,
            form.getHydrocarbonsDiscovered()
        );
  }

  @Test
  void saveGeneralReservoirInformation_noReservoirHydrocarbons() {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName("Reservoir Name");
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);

    when(reservoirHydrocarbonsRepository.findReservoirHydrocarbonsByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    generalReservoirInformationService.saveGeneralReservoirInformation(form, reservoirSubmissionDetail);

    verify(reservoirPersistenceService, times(1))
        .saveReservoir(reservoirSubmissionDetail.getReservoir(), form.getReservoirName());
    verify(reservoirHydrocarbonsRepository, times(1)).save(reservoirHydrocarbonsArgumentCaptor.capture());
    assertThat(reservoirHydrocarbonsArgumentCaptor.getValue())
        .extracting(
            ReservoirHydrocarbons::getReservoirSubmissionDetail,
            ReservoirHydrocarbons::getHydrocarbonsDiscovered)
        .containsExactly(
            reservoirSubmissionDetail,
            form.getHydrocarbonsDiscovered()
        );
  }

  @Test
  void saveGeneralReservoirInformation_formGasOnly() {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName("Reservoir Name");
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    when(reservoirHydrocarbonsRepository.findReservoirHydrocarbonsByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    generalReservoirInformationService.saveGeneralReservoirInformation(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1)).deleteProbabilityDistributions(
        List.of(
            ProbabilityDistributionFieldInstance.OIL_RECOVERY_FACTOR_DISCOVERY,
            ProbabilityDistributionFieldInstance.OIL_FORMATION_VOLUME_FACTOR_DISCOVERY,
            ProbabilityDistributionFieldInstance.OIL_HYDROCARBON_SATURATION_DISCOVERY,
            ProbabilityDistributionFieldInstance.ASSOCIATED_GAS_OIL_RATIO_LIFE_OF_FIELD
        ),
        reservoirSubmissionDetail
    );
  }

  @Test
  void saveGeneralReservoirInformation_formOilOnly() {
    var form = new GeneralReservoirInformationForm();
    form.setReservoirName("Reservoir Name");
    form.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.GAS_ONLY);
    when(reservoirHydrocarbonsRepository.findReservoirHydrocarbonsByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    generalReservoirInformationService.saveGeneralReservoirInformation(form, reservoirSubmissionDetail);

    verify(probabilityDistributionPersistenceService, times(1)).deleteProbabilityDistributions(
        List.of(
            ProbabilityDistributionFieldInstance.GAS_HYDROCARBON_SATURATION_DISCOVERY,
            ProbabilityDistributionFieldInstance.GAS_EXPANSION_FACTOR,
            ProbabilityDistributionFieldInstance.GAS_FORMATION_VOLUME_FACTOR_DISCOVERY,
            ProbabilityDistributionFieldInstance.CONDENSATE_GAS_RATIO_LIFE_OF_FIELD,
            ProbabilityDistributionFieldInstance.GAS_RECOVERY_FACTOR
        ),
        reservoirSubmissionDetail
    );
  }

  @Test
  void getGeneralReservoirInformationForm_hydrocarbonsDiscovered() {
    var reservoirHydrocarbons = new ReservoirHydrocarbons();
    reservoirHydrocarbons.setHydrocarbonsDiscovered(HydrocarbonsDiscovered.OIL_ONLY);
    when(reservoirHydrocarbonsRepository.findReservoirHydrocarbonsByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.of(reservoirHydrocarbons));

    assertThat(generalReservoirInformationService.getGeneralReservoirInformationForm(reservoirSubmissionDetail))
        .extracting(
            GeneralReservoirInformationForm::getReservoirName,
            GeneralReservoirInformationForm::getHydrocarbonsDiscovered)
        .containsExactly(
            reservoirSubmissionDetail.getReservoir().getName(),
            reservoirHydrocarbons.getHydrocarbonsDiscovered()
        );
  }

  @Test
  void getGeneralReservoirInformationForm_noHydrocarbonsDiscovered() {
    when(reservoirHydrocarbonsRepository.findReservoirHydrocarbonsByReservoirSubmissionDetail(reservoirSubmissionDetail))
        .thenReturn(Optional.empty());

    assertThat(generalReservoirInformationService.getGeneralReservoirInformationForm(reservoirSubmissionDetail))
        .extracting(
            GeneralReservoirInformationForm::getReservoirName,
            GeneralReservoirInformationForm::getHydrocarbonsDiscovered)
        .containsExactly(
            reservoirSubmissionDetail.getReservoir().getName(),
            null
        );
  }

  @Test
  void getReservoirHydrocarbons_verifyCalls() {
    generalReservoirInformationService.getReservoirHydrocarbons(reservoirSubmissionDetail);

    verify(reservoirHydrocarbonsRepository, times(1))
        .findReservoirHydrocarbonsByReservoirSubmissionDetail(reservoirSubmissionDetail);
  }
}
package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscovery;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoveryTestUtils;

@ExtendWith(MockitoExtension.class)
class UndevelopedDiscoveryInformationServiceTest {

  @Mock
  private UndevelopedDiscoveryInformationRepository informationRepository;

  @Mock
  private UndevelopedDiscovery undevelopedDiscovery;

  @InjectMocks
  private UndevelopedDiscoveryInformationService informationService;

  private final ArgumentCaptor<UndevelopedDiscoveryInformation> undevelopedDiscoveryInformationCaptor = ArgumentCaptor.forClass(
      UndevelopedDiscoveryInformation.class);

  @Test
  void saveUndevelopedDiscoveryInformation_noComplexity() {
    var informationForm = new UndevelopedDiscoveryInformationForm();
    informationForm.setDiscoveryInformationDateDay("11");
    informationForm.setDiscoveryInformationDateMonth("1");
    informationForm.setDiscoveryInformationDateYear("2002");

    informationForm.setVolumetricsForm(UndevelopedDiscoveryTestUtils.getVolumetricsForm(1, 2, 3, 4, "reason"));

    informationForm.setProvideDiscoveryComplexity(Boolean.FALSE);
    informationForm.setComplexityForm(UndevelopedDiscoveryTestUtils.getComplexityForm());

    informationService.saveUndevelopedDiscoveryInformation(informationForm, undevelopedDiscovery);

    verify(informationRepository, times(1))
        .save(undevelopedDiscoveryInformationCaptor.capture());

    assertThat(undevelopedDiscoveryInformationCaptor.getValue())
        .extracting(
            UndevelopedDiscoveryInformation::getInformationDate,
            UndevelopedDiscoveryInformation::getOilInPlaceP50,
            UndevelopedDiscoveryInformation::getOilRecoverableOneC,
            UndevelopedDiscoveryInformation::getOilRecoverableTwoC,
            UndevelopedDiscoveryInformation::getOilRecoverableThreeC,
            UndevelopedDiscoveryInformation::getGasInPlaceP50,
            UndevelopedDiscoveryInformation::getGasRecoverableOneC,
            UndevelopedDiscoveryInformation::getGasRecoverableTwoC,
            UndevelopedDiscoveryInformation::getGasRecoverableThreeC,
            UndevelopedDiscoveryInformation::getProvidedDiscoveryComplexity,
            UndevelopedDiscoveryInformation::getArealDensityRange,
            UndevelopedDiscoveryInformation::getVerticalHeterogeneityRange,
            UndevelopedDiscoveryInformation::getViscosityRange,
            UndevelopedDiscoveryInformation::getStructuralComplexityRange,
            UndevelopedDiscoveryInformation::getDifferenceReason)
        .containsExactly(
            LocalDate.of(2002, 1, 11),
            BigDecimal.valueOf(4),
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(2),
            BigDecimal.valueOf(3),
            BigDecimal.valueOf(4),
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(2),
            BigDecimal.valueOf(3),
            Boolean.FALSE,
            null,
            null,
            null,
            null,
            "reason"
        );
  }

  @Test
  void saveUndevelopedDiscoveryInformation_savedComplexity() {
    var informationForm = new UndevelopedDiscoveryInformationForm();
    informationForm.setDiscoveryInformationDateDay("11");
    informationForm.setDiscoveryInformationDateMonth("1");
    informationForm.setDiscoveryInformationDateYear("2002");

    informationForm.setVolumetricsForm(UndevelopedDiscoveryTestUtils.getVolumetricsForm(1, 2, 3, 4, "reason"));

    informationForm.setProvideDiscoveryComplexity(Boolean.TRUE);
    var complexityForm = UndevelopedDiscoveryTestUtils.getComplexityForm();
    informationForm.setComplexityForm(complexityForm);

    informationService.saveUndevelopedDiscoveryInformation(informationForm, undevelopedDiscovery);

    verify(informationRepository, times(1))
        .save(undevelopedDiscoveryInformationCaptor.capture());

    assertThat(undevelopedDiscoveryInformationCaptor.getValue())
        .extracting(
            UndevelopedDiscoveryInformation::getInformationDate,
            UndevelopedDiscoveryInformation::getOilInPlaceP50,
            UndevelopedDiscoveryInformation::getOilRecoverableOneC,
            UndevelopedDiscoveryInformation::getOilRecoverableTwoC,
            UndevelopedDiscoveryInformation::getOilRecoverableThreeC,
            UndevelopedDiscoveryInformation::getGasInPlaceP50,
            UndevelopedDiscoveryInformation::getGasRecoverableOneC,
            UndevelopedDiscoveryInformation::getGasRecoverableTwoC,
            UndevelopedDiscoveryInformation::getGasRecoverableThreeC,
            UndevelopedDiscoveryInformation::getProvidedDiscoveryComplexity,
            UndevelopedDiscoveryInformation::getArealDensityRange,
            UndevelopedDiscoveryInformation::getVerticalHeterogeneityRange,
            UndevelopedDiscoveryInformation::getViscosityRange,
            UndevelopedDiscoveryInformation::getStructuralComplexityRange,
            UndevelopedDiscoveryInformation::getDifferenceReason)
        .containsExactly(
            LocalDate.of(2002, 1, 11),
            BigDecimal.valueOf(4),
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(2),
            BigDecimal.valueOf(3),
            BigDecimal.valueOf(4),
            BigDecimal.valueOf(1),
            BigDecimal.valueOf(2),
            BigDecimal.valueOf(3),
            Boolean.TRUE,
            complexityForm.getArealDensityRange(),
            complexityForm.getVerticalHeterogeneityRange(),
            complexityForm.getViscosityRange(),
            complexityForm.getStructuralComplexityRange(),
            "reason"
        );
  }

  @Test
  void getOrderedDiscoveryInformation_noValues() {
    when(informationRepository.findAllByUndevelopedDiscovery(undevelopedDiscovery))
        .thenReturn(List.of());

    assertThat(informationService.getOrderedDiscoveryInformation(undevelopedDiscovery))
        .isEqualTo(List.of());
  }

  @Test
  void getOrderedDiscoveryInformation_ordersByMostRecent() {
    var oldestInformation = new UndevelopedDiscoveryInformation();
    oldestInformation.setInformationDate(LocalDate.of(2002, 1, 11));

    var newestInformation = new UndevelopedDiscoveryInformation();
    newestInformation.setInformationDate(LocalDate.of(2024, 4, 29));

    when(informationRepository.findAllByUndevelopedDiscovery(undevelopedDiscovery))
        .thenReturn(List.of(oldestInformation, newestInformation));

    assertThat(informationService.getOrderedDiscoveryInformation(undevelopedDiscovery))
        .isEqualTo(List.of(newestInformation, oldestInformation));
  }
}

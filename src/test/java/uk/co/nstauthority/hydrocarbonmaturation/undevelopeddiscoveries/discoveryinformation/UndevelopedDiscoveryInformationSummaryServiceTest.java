package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCrossTabView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;
import uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.UndevelopedDiscoveryTestUtils;

@ExtendWith(MockitoExtension.class)
class UndevelopedDiscoveryInformationSummaryServiceTest {

  @Mock
  private UndevelopedDiscoveryInformationService informationService;

  @InjectMocks
  private UndevelopedDiscoveryInformationSummaryService informationSummaryService;

  @Test
  void getSummarySection() {
    var resource = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    var undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(resource, Boolean.FALSE);
    var undevelopedDiscoveryInformation = UndevelopedDiscoveryTestUtils.getUndevelopedDiscoveryInformation(
        LocalDate.of(2002, 1, 11),
        undevelopedDiscovery
    );

    when(informationService.getOrderedDiscoveryInformation(undevelopedDiscovery))
        .thenReturn(List.of(undevelopedDiscoveryInformation));

    var summarySection = informationSummaryService.getSummarySection(undevelopedDiscovery);
    assertThat(summarySection)
        .isPresent()
        .get()
        .extracting(
            SummarySection::summaryText,
            SummarySection::displayOrder
        )
        .containsExactly(
            "",
            20
        );

    var summaryItems = summarySection.get().summaryItems();
    assertThat(summaryItems)
        .hasSize(1)
        .extracting(
            SummaryItem::displayName,
            item -> item.summaryCards().size()
        )
        .containsExactly(tuple(
            "Discovery information for 11 January 2002",
            2
        ));

    var expectedVolumetricData = new SummaryCrossTabView(
        UndevelopedDiscoveryInformationSummaryService.CROSS_TAB_HEADINGS,
        List.of(
            new UndevelopedDiscoveryInformationVolumetricsRow("Oil in place", "1", "", "", "", "mmbbl"),
            new UndevelopedDiscoveryInformationVolumetricsRow("Oil recoverable resources", "", "1", "1", "1", "mmbbl"),
            new UndevelopedDiscoveryInformationVolumetricsRow("Gas in place", "1", "", "", "", "bcf"),
            new UndevelopedDiscoveryInformationVolumetricsRow("Gas recoverable resources", "", "1", "1", "1", "bcf")
        )
    );
    var volumetricsCard = summaryItems.get(0).summaryCards().get(0);
    assertThat(volumetricsCard)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryActions,
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        )
        .containsExactly(
            UndevelopedDiscoveryInformationSummaryService.DISCOVERY_VOLUMETRICS,
            List.of(),
            SummaryCardType.CROSS_TAB,
            expectedVolumetricData
        );

    var expectedJustificationData = new SummaryDataView(List.of(
        new SummaryKeyValue(UndevelopedDiscoveryInformationSummaryService.JUSTIFICATION_KEY,
            undevelopedDiscoveryInformation.getDifferenceReason())
    ));
    var justificationCard = summaryItems.get(0).summaryCards().get(1);
    assertThat(justificationCard)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryActions,
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        )
        .containsExactly(
            UndevelopedDiscoveryInformationSummaryService.JUSTIFICATION_HEADING,
            List.of(),
            SummaryCardType.SIMPLE_SUMMARY,
            expectedJustificationData
        );
  }
}

package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries.discoveryinformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static org.mockito.Mockito.when;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.complexity.ComplexityUtil;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryAction;
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

    var expectedSummaryActions = getExpectedSummaryActions(resource, undevelopedDiscoveryInformation);
    var summaryItems = summarySection.get().summaryItems();
    assertThat(summaryItems)
        .hasSize(1)
        .extracting(
            SummaryItem::displayName,
            item -> item.summaryCards().size(),
            SummaryItem::summarySectionActions
        )
        .containsExactly(tuple(
            "Discovery information for 11 January 2002",
            3,
            expectedSummaryActions
        ));

    var expectedVolumetricData = getExpectedVolumetricData();
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

    var expectedJustificationData = getExpectedJustificationData(undevelopedDiscoveryInformation);
    var justificationCard = summaryItems.get(0).summaryCards().get(1);
    assertThat(justificationCard)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryActions,
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        )
        .containsExactly(
            UndevelopedDiscoveryInformationSummaryService.VOLUMETRICS_DIFFERENCE_HEADING,
            List.of(),
            SummaryCardType.SIMPLE_SUMMARY,
            expectedJustificationData
        );

    var expectedComplexityData = getExpectedComplexityData(undevelopedDiscoveryInformation);
    var complexityCard = summaryItems.get(0).summaryCards().get(2);
    assertThat(complexityCard)
        .extracting(
            SummaryCard::displayName,
            SummaryCard::summaryActions,
            SummaryCard::summaryCardType,
            SummaryCard::summaryData
        )
        .containsExactly(
            UndevelopedDiscoveryInformationSummaryService.DISCOVERY_COMPLEXITY,
            List.of(),
            SummaryCardType.SIMPLE_SUMMARY,
            expectedComplexityData
        );
  }

  private List<SummaryAction> getExpectedSummaryActions(Resource resource,
                                                        UndevelopedDiscoveryInformation undevelopedDiscoveryInformation) {
    return List.of(
        new SummaryAction(
            "Update undeveloped discovery information",
            ReverseRouter.route(on(UndevelopedDiscoveryInformationController.class)
                .getExistingUndevelopedDiscoveryInformation(resource.getId(), undevelopedDiscoveryInformation.getId())),
            "Update undeveloped discovery information"
        ),
        new SummaryAction(
            "Remove undeveloped discovery information",
            ReverseRouter.route(on(UndevelopedDiscoveryRemoveInformationController.class)
                .getRemoveInformationConfirmation(resource.getId(), undevelopedDiscoveryInformation.getId())),
            "Remove undeveloped discovery information"
        )
    );
  }

  private SummaryCrossTabView getExpectedVolumetricData() {
    var expectedVolumetricData = new SummaryCrossTabView(
        UndevelopedDiscoveryInformationSummaryService.CROSS_TAB_HEADINGS);
    expectedVolumetricData.addRow("Oil initially in place", "1", null, null, null, "mmbbl");
    expectedVolumetricData.addRow("Oil recoverable resources", null, "1", "1", "1", "mmbbl");
    expectedVolumetricData.addRow("Gas initially in place", "1", null, null, null, "bcf");
    expectedVolumetricData.addRow("Gas recoverable resources", null, "1", "1", "1", "bcf");
    return expectedVolumetricData;
  }

  private SummaryDataView getExpectedJustificationData(UndevelopedDiscoveryInformation information) {
    return new SummaryDataView(List.of(
        new SummaryKeyValue(UndevelopedDiscoveryInformationSummaryService.VOLUMETRIC_DIFFERENCE_KEY,
            information.getDifferenceReason())
    ));
  }

  private SummaryDataView getExpectedComplexityData(UndevelopedDiscoveryInformation information) {
    return new SummaryDataView(List.of(
        new SummaryKeyValue(
            ComplexityUtil.VISCOSITY,
            information.getViscosityRange().getDisplayName()
        ),
        new SummaryKeyValue(
            ComplexityUtil.VERTICAL_HETEROGENEITY,
            information.getVerticalHeterogeneityRange().getDisplayName()
        ),
        new SummaryKeyValue(
            ComplexityUtil.AREAL_DENSITY,
            information.getArealDensityRange().getDisplayName()
        ),
        new SummaryKeyValue(
            ComplexityUtil.STRUCTURAL_COMPLEXITY,
            information.getStructuralComplexityRange().getDisplayName()
        )
    ));
  }
}

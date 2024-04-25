package uk.co.nstauthority.hydrocarbonmaturation.undevelopeddiscoveries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.mvc.ReverseRouter;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryAction;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;

@ExtendWith(MockitoExtension.class)
class UndevelopedDiscoveryTrackingDetailsSummaryServiceTest {

  @InjectMocks
  private UndevelopedDiscoveryTrackingDetailsSummaryService summaryService;

  @Test
  void getSummarySection() {
    var resource = ResourceTestUtils.buildResource(ResourceType.DISCOVERY);
    var undevelopedDiscovery = UndevelopedDiscoveryTestUtils.createUndevelopedDiscovery(resource, Boolean.FALSE);
    var summarySection = summaryService.getSummarySection(undevelopedDiscovery);

    var expectedSummaryActions = List.of(new SummaryAction(
        "Change",
        ReverseRouter.route(on(UndevelopedDiscoveryTrackingDetailsController.class)
            .getUndevelopedDiscoveryTrackingDetails(resource.getId())),
        "Change tracking details"
    ));

    var expectedSummaryData = new SummaryDataView(List.of(
        new SummaryKeyValue("Discovery included in the undeveloped discoveries tracker?", "No")
    ));

    assertThat(summarySection)
        .isPresent()
        .get()
        .extracting(
            SummarySection::summaryText,
            SummarySection::displayOrder
        )
        .containsExactly(
            "",
            10
        );

    var summaryCard = summarySection.get().summaryItems().get(0).summaryCards().get(0);
    assertThat(summaryCard).extracting(
        SummaryCard::displayName,
        SummaryCard::summaryCardType,
        SummaryCard::summaryData,
        SummaryCard::summaryActions
    ).containsExactly(
        "Tracking details",
        SummaryCardType.SIMPLE_SUMMARY,
        expectedSummaryData,
        expectedSummaryActions
    );
  }
}
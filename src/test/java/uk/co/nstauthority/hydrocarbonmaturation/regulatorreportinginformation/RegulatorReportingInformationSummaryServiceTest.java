package uk.co.nstauthority.hydrocarbonmaturation.regulatorreportinginformation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.ResourceType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.discoverywelldetails.HydrocarbonsDiscovered;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityType;

@ExtendWith(MockitoExtension.class)
class RegulatorReportingInformationSummaryServiceTest {

  @Mock
  private RegulatorReportingInformationService regulatorReportingInformationService;

  @InjectMocks
  private RegulatorReportingInformationSummaryService regulatorReportingInformationSummaryService;

  @Test
  void getSummary_noReportingInfo() {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    when(regulatorReportingInformationService.getByResource(resource)).thenReturn(Optional.empty());
    assertThat(regulatorReportingInformationSummaryService.getSummary(resource)).extracting(
        RegulatorReportingInformationSummaryView::resourceName,
        RegulatorReportingInformationSummaryView::hydrocarbonCase,
        RegulatorReportingInformationSummaryView::opportunityType,
        RegulatorReportingInformationSummaryView::wellName
    ).containsExactly(
        "",
        "",
        "",
        ""
    );
  }

  @Test
  void getSummary() {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    var regulatorReportingInformation = new RegulatorReportingInformation();
    var resourceName = "Resource Name";
    regulatorReportingInformation.setResourceName(resourceName);
    regulatorReportingInformation.setHydrocarbonCase(HydrocarbonsDiscovered.GAS_ONLY);
    regulatorReportingInformation.setOpportunityType(OpportunityType.PROSPECT);
    var wellReference = "Well Reference";
    regulatorReportingInformation.setWellReference(wellReference);
    when(regulatorReportingInformationService.getByResource(resource)).thenReturn(Optional.of(regulatorReportingInformation));
    assertThat(regulatorReportingInformationSummaryService.getSummary(resource)).extracting(
        RegulatorReportingInformationSummaryView::resourceName,
        RegulatorReportingInformationSummaryView::hydrocarbonCase,
        RegulatorReportingInformationSummaryView::opportunityType,
        RegulatorReportingInformationSummaryView::wellName
    ).containsExactly(
        resourceName,
        HydrocarbonsDiscovered.GAS_ONLY.getDisplayName(),
        OpportunityType.PROSPECT.getDisplayName(),
        wellReference
    );
  }

  @Test
  void getSummary_otherOpportunityType() {
    var resource = ResourceTestUtils.buildResource(ResourceType.PROSPECT);
    var regulatorReportingInformation = new RegulatorReportingInformation();
    var resourceName = "Resource Name";
    regulatorReportingInformation.setResourceName(resourceName);
    regulatorReportingInformation.setHydrocarbonCase(HydrocarbonsDiscovered.GAS_ONLY);
    var otherOpportunityType = "Other opportunity type";
    regulatorReportingInformation.setOtherTypeOfOpportunity(otherOpportunityType);
    var wellReference = "Well Reference";
    regulatorReportingInformation.setWellReference(wellReference);
    when(regulatorReportingInformationService.getByResource(resource)).thenReturn(Optional.of(regulatorReportingInformation));
    assertThat(regulatorReportingInformationSummaryService.getSummary(resource)).extracting(
        RegulatorReportingInformationSummaryView::resourceName,
        RegulatorReportingInformationSummaryView::hydrocarbonCase,
        RegulatorReportingInformationSummaryView::opportunityType,
        RegulatorReportingInformationSummaryView::wellName
    ).containsExactly(
        resourceName,
        HydrocarbonsDiscovered.GAS_ONLY.getDisplayName(),
        otherOpportunityType,
        wellReference
    );
  }
}

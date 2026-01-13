package uk.co.nstauthority.hydrocarbonmaturation.regulatorreportinginformation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.co.nstauthority.hydrocarbonmaturation.energyresource.Resource;

@Service
public class RegulatorReportingInformationSummaryService {

  private final RegulatorReportingInformationService regulatorReportingInformationService;

  @Autowired
  public RegulatorReportingInformationSummaryService(
      RegulatorReportingInformationService regulatorReportingInformationService) {
    this.regulatorReportingInformationService = regulatorReportingInformationService;
  }

  public RegulatorReportingInformationSummaryView getSummary(Resource resource) {
    var info = regulatorReportingInformationService.getByResource(resource);
    if (info.isEmpty()) {
      return RegulatorReportingInformationSummaryView.empty();
    }
    var regulatorReportingInfo = info.get();
    var opportunityType = "";
    if (regulatorReportingInfo.getOtherTypeOfOpportunity() != null) {
      opportunityType = regulatorReportingInfo.getOtherTypeOfOpportunity();
    } else if (regulatorReportingInfo.getOpportunityType() != null) {
      opportunityType = regulatorReportingInfo.getOpportunityType().getDisplayName();
    }
    return new RegulatorReportingInformationSummaryView(
        regulatorReportingInfo.getResourceName() != null ? regulatorReportingInfo.getResourceName() : "",
        regulatorReportingInfo.getHydrocarbonCase() != null ? regulatorReportingInfo.getHydrocarbonCase().getDisplayName() : "",
        opportunityType,
        regulatorReportingInfo.getWellReference() != null ? regulatorReportingInfo.getWellReference() : ""
    );
  }
}

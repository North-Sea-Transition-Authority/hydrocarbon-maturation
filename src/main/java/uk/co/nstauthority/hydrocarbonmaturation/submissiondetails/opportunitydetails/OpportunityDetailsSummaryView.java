package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryService.GEOLOGICAL_FILE_KEY;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryService.MAP_OF_OPPORTUNITY_FILE_KEY;
import static uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails.OpportunityDetailsSummaryService.REPRESENTATIVE_SEISMIC_SECTION_FILE_KEY;

import java.util.List;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryFileViewRow;

public record OpportunityDetailsSummaryView(String opportunityType,
                                            Boolean otherOpportunity,
                                            String otherOpportunityType,
                                            String opportunityComments,
                                            String keyEvaluationWork,
                                            String keyWorkRequired,
                                            List<SummaryFileViewRow> mapOfOpportunity,
                                            List<SummaryFileViewRow> representativeSeismicSection,
                                            List<SummaryFileViewRow> geologicalCrossSection) {

  public static OpportunityDetailsSummaryView empty() {
    return new OpportunityDetailsSummaryView(
        "",
        false,
        "",
        "",
        "",
        "",
        List.of(SummaryFileViewRow.empty(MAP_OF_OPPORTUNITY_FILE_KEY)),
        List.of(SummaryFileViewRow.empty(REPRESENTATIVE_SEISMIC_SECTION_FILE_KEY)),
        List.of(SummaryFileViewRow.empty(GEOLOGICAL_FILE_KEY))
    );
  }
}

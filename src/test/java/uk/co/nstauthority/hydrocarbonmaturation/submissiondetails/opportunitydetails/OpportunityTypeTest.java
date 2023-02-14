package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.opportunitydetails;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class OpportunityTypeTest {

  @Test
  void getSelectableOpportunityTypes_containsOnlySelectable() {
    assertThat(OpportunityType.getSelectableOpportunityTypes())
        .isEqualTo(
            Map.of(
                "LEAD", "Lead",
                "PROSPECT", "Prospect",
                "DRILL_READY_PROSPECT", "Drill-ready prospect",
                "OTHER", "Other")
        );
  }
}
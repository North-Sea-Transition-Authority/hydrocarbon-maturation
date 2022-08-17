package uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpportunityTaskListSectionTest {
  private static OpportunityTaskListSection opportunityTaskListSection;

  @BeforeAll
  static void setUp() {
    opportunityTaskListSection = new OpportunityTaskListSection();
  }

  @Test
  void getSectionName() {
    assertThat(opportunityTaskListSection.getSectionName()).isEqualTo(OpportunityTaskListSection.SECTION_NAME);
  }

  @Test
  void getDisplayOrder() {
    assertThat(opportunityTaskListSection.getDisplayOrder()).isEqualTo(OpportunityTaskListSection.SECTION_DISPLAY_ORDER);
  }
}
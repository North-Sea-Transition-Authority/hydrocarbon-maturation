package uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ReservoirTaskListSectionTest {
  
  private static ReservoirTaskListSection reservoirTaskListSection;

  @BeforeAll
  static void setUp() {
    reservoirTaskListSection = new ReservoirTaskListSection();
  }

  @Test
  void getSectionName() {
    assertThat(reservoirTaskListSection.getSectionName()).isEqualTo(ReservoirTaskListSection.SECTION_NAME);
  }

  @Test
  void getDisplayOrder() {
    assertThat(reservoirTaskListSection.getDisplayOrder()).isEqualTo(ReservoirTaskListSection.SECTION_DISPLAY_ORDER);
  }
}
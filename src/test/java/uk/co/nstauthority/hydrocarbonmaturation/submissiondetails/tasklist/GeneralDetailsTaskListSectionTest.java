package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GeneralDetailsTaskListSectionTest {

  private static GeneralDetailsTaskListSection generalDetailsTaskListSection;

  @BeforeAll
  static void setUp() {
    generalDetailsTaskListSection = new GeneralDetailsTaskListSection();
  }

  @Test
  void getSectionName() {
    assertThat(generalDetailsTaskListSection.getSectionName()).isEqualTo(GeneralDetailsTaskListSection.SECTION_NAME);
  }

  @Test
  void getDisplayOrder() {
    assertThat(generalDetailsTaskListSection.getDisplayOrder()).isEqualTo(GeneralDetailsTaskListSection.SECTION_DISPLAY_ORDER);
  }
}
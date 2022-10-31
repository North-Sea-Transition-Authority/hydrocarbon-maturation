package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AdditionalInformationTaskListSectionTest {

  private static AdditionalInformationTaskListSection additionalInformationTaskListSection;

  @BeforeEach
  void setUp() {
    additionalInformationTaskListSection = new AdditionalInformationTaskListSection();
  }

  @Test
  void getSectionName() {
    assertThat(additionalInformationTaskListSection.getSectionName()).isEqualTo(AdditionalInformationTaskListSection.SECTION_NAME);
  }

  @Test
  void getDisplayOrder() {
    assertThat(additionalInformationTaskListSection.getDisplayOrder()).isEqualTo(AdditionalInformationTaskListSection.SECTION_DISPLAY_ORDER);
  }
}
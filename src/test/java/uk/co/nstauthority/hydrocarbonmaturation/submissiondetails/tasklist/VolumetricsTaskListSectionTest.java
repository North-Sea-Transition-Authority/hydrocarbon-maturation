package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VolumetricsTaskListSectionTest {

  private static VolumetricsTaskListSection volumetricsTaskListSection;

  @BeforeEach
  void setUp() {
    volumetricsTaskListSection = new VolumetricsTaskListSection();
  }

  @Test
  void getSectionName() {
    assertThat(volumetricsTaskListSection.getSectionName()).isEqualTo(VolumetricsTaskListSection.SECTION_NAME);
  }

  @Test
  void getDisplayOrder() {
    assertThat(volumetricsTaskListSection.getDisplayOrder()).isEqualTo(VolumetricsTaskListSection.SECTION_DISPLAY_ORDER);
  }
}
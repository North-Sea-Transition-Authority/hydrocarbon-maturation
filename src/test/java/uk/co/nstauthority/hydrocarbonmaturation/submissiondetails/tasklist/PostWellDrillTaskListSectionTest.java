package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostWellDrillTaskListSectionTest {

  private PostWellDrillTaskListSection postWellDrillTaskListSection;

  @BeforeEach
  void setUp() {
    postWellDrillTaskListSection = new PostWellDrillTaskListSection();
  }

  @Test
  void getSectionName() {
    assertThat(postWellDrillTaskListSection.getSectionName()).isEqualTo("Post well drill");
  }

  @Test
  void getDisplayOrder() {
    assertThat(postWellDrillTaskListSection.getDisplayOrder()).isEqualTo(30);
  }
}
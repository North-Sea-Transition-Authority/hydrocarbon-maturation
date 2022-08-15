package uk.co.nstauthority.hydrocarbonmaturation.prospect.tasklist;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailsController;

@ExtendWith(MockitoExtension.class)
class GeneralDetailsTaskListSectionTest {

  private static GeneralDetailsTaskListSection generalDetailsTaskListSection;

  @BeforeAll
  static void setUp() {
    generalDetailsTaskListSection = new GeneralDetailsTaskListSection();
  }

  @Test
  void getSectionName() {
    assertThat(generalDetailsTaskListSection.getSectionName()).isEqualTo(GeneralDetailsController.PAGE_NAME);
  }

  @Test
  void getDisplayOrder() {
    assertThat(generalDetailsTaskListSection.getDisplayOrder()).isEqualTo(10);
  }
}
package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSubmissionService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.ReviewAndSubmitTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.SourceAndSealTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.VolumetricsTaskListSection;

@ExtendWith(MockitoExtension.class)
class SubNavigationSectionItemTest {

  @Mock
  private SourceAndSealSubmissionService sourceAndSealSubmissionService;

  private SubNavigationSectionItem subNavigationSectionItem;
  private ReviewAndSubmitTaskListItem reviewAndSubmitTaskListItem;
  private SourceAndSealTaskListItem sourceAndSealTaskListItem;
  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    subNavigationSectionItem = new SubNavigationSectionItem(new VolumetricsTaskListSection());
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    sourceAndSealTaskListItem = new SourceAndSealTaskListItem(sourceAndSealSubmissionService);
    reviewAndSubmitTaskListItem = new ReviewAndSubmitTaskListItem();
  }

  @Test
  void addTaskListItem_reviewAndSubmitItem_doesntAdd() {
    subNavigationSectionItem.addTaskListItem(reviewAndSubmitTaskListItem);
    assertThat(subNavigationSectionItem.getTaskListItems()).isEmpty();
  }

  @Test
  void addTaskListItem_validProspectItem_addsToList() {
    subNavigationSectionItem.addTaskListItem(sourceAndSealTaskListItem);
    assertThat(subNavigationSectionItem.getTaskListItems()).hasSize(1);
  }

  @Test
  void getSectionName() {
    assertThat(subNavigationSectionItem.getSectionName()).hasToString("Volumetrics");
  }

  @Test
  void getAction() {
    assertThat(subNavigationSectionItem.getAction()).hasToString("#volumetrics");
  }
}
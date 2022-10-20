package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.hydrocarboncase.HydrocarbonCaseSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal.SourceAndSealSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.ReviewAndSubmitTaskListSection;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.VolumetricsTaskListSection;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListItem;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.tasklist.prospect.ProspectTaskListSection;

@ExtendWith(MockitoExtension.class)
class ProspectSummaryServiceTest {

  @Mock
  private GeneralDetailSummaryService generalDetailSummaryService;

  @Mock
  private SourceAndSealSummaryService sourceAndSealSummaryService;

  @Mock
  private HydrocarbonCaseSummaryService hydrocarbonCaseSummaryService;

  @Mock
  private ProspectTaskListItem prospectTaskListItem;

  private ProspectSummaryService prospectSummaryService;
  private SubmissionDetail submissionDetail;
  private List<ProspectTaskListSection> listOfTaskListSections;


  @BeforeEach
  void setUp() {
    listOfTaskListSections = List.of(new VolumetricsTaskListSection(), new ReviewAndSubmitTaskListSection());
    List<ProspectTaskListItem> listOfTaskListItems = List.of(prospectTaskListItem);
    prospectSummaryService = new ProspectSummaryService(
        generalDetailSummaryService,
        sourceAndSealSummaryService,
        hydrocarbonCaseSummaryService,
        listOfTaskListSections,
        listOfTaskListItems);
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
  }

  @Test
  void getGeneralDetailSummaryViewFromSubmissionDetail_verifyMethodCall() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    prospectSummaryService.getGeneralDetailSummaryViewFromSubmissionDetail(submissionDetail);

    verify(generalDetailSummaryService, times(1))
        .getGeneralDetailSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getSourceAndSealSummaryViewFromSubmissionDetail_verifyMethodCall() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    prospectSummaryService.getSourceAndSealSummaryViewFromSubmissionDetail(submissionDetail);

    verify(sourceAndSealSummaryService, times(1))
        .getSourceAndSealSummaryFromSubmissionDetail(submissionDetail);
  }

  @Test
  void getHydrocarbonCaseSummaryViewFromSubmissionDetail_verifyMethodCall() {
    var submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();

    prospectSummaryService.getHydrocarbonCaseSummaryViewFromSubmissionDetail(submissionDetail);

    verify(hydrocarbonCaseSummaryService, times(1))
        .getHydrocarbonSummaryFromSubmissionDetail(submissionDetail);
  }


  @Test
  void getProspectSubNavigation_whenTwoSections_oneResult() {
    var subNavItems = prospectSummaryService.getProspectSubNavigation(submissionDetail);

    assertThat(subNavItems).hasSize(1);
  }

  @Test
  void getProspectSubNavigation_whenOneWrongSection_noResults() {
    listOfTaskListSections = List.of(new ReviewAndSubmitTaskListSection());
    List<ProspectTaskListItem> listOfTaskListItems = List.of(prospectTaskListItem);
    prospectSummaryService = new ProspectSummaryService(
        generalDetailSummaryService,
        sourceAndSealSummaryService,
        hydrocarbonCaseSummaryService, listOfTaskListSections,
        listOfTaskListItems);

    var subNavItems = prospectSummaryService.getProspectSubNavigation(submissionDetail);

    assertThat(subNavItems).isEmpty();
  }
}
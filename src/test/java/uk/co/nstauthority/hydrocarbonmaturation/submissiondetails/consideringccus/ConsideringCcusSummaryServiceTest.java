package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;

@ExtendWith(MockitoExtension.class)
class ConsideringCcusSummaryServiceTest {

  @Mock
  private ConsideringCcusDetailRepository consideringCcusDetailRepository;

  @InjectMocks
  private ConsideringCcusSummaryService consideringCcusSummaryService;

  private SubmissionDetail submissionDetail;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewFieldSubmissionDetail();
  }

  @Test
  void getConsideringCcusSummary_existingDetail_answeredYes_thenValues() {
    var consideringCcusDetail = new ConsideringCcusDetail();
    consideringCcusDetail.setIsConnectedToProject(true);
    consideringCcusDetail.setConnectedProjects("projects");
    consideringCcusDetail.setIsAnyShallowerHorizons(true);
    consideringCcusDetail.setAgeAndFormationOfHorizons("ages");
    when(consideringCcusDetailRepository.findConsideringCcusDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(consideringCcusDetail));

    var expectedSummaryData = new SummaryDataView(List.of(
        new SummaryKeyValue(
            ConsideringCcusSummaryService.IS_CONNECTED_TO_PROJECTS,
            "Yes"
        ),
        new SummaryKeyValue(
            ConsideringCcusSummaryService.CONNECTED_PROJECTS,
            consideringCcusDetail.getConnectedProjects()
        ),
        new SummaryKeyValue(
            ConsideringCcusSummaryService.IS_SHALLOWER_HORIZONS,
            "Yes"
        ),
        new SummaryKeyValue(
            ConsideringCcusSummaryService.AGE_AND_FORMATION_OF_HORIZONS,
            consideringCcusDetail.getAgeAndFormationOfHorizons()
        )
    ));

    var expectedSummaryCard = SummaryCard.simpleSummaryCardWithHeading(
        ConsideringCcusSummaryService.CONSIDERING_CCUS,
        expectedSummaryData);

    assertThat(consideringCcusSummaryService.getConsideringCcusSummary(submissionDetail))
        .usingRecursiveComparison()
        .isEqualTo(expectedSummaryCard);
  }

  @Test
  void getConsideringCcusSummary_existingDetail_answeredNo_thenDoNotShowSubAnswers() {
    var consideringCcusDetail = new ConsideringCcusDetail();
    consideringCcusDetail.setIsConnectedToProject(false);
    consideringCcusDetail.setConnectedProjects("projects");
    consideringCcusDetail.setIsAnyShallowerHorizons(false);
    consideringCcusDetail.setAgeAndFormationOfHorizons("ages");
    when(consideringCcusDetailRepository.findConsideringCcusDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(consideringCcusDetail));

    var expectedSummaryData = new SummaryDataView(List.of(
        new SummaryKeyValue(
            ConsideringCcusSummaryService.IS_CONNECTED_TO_PROJECTS,
            "No"
        ),
        new SummaryKeyValue(
            ConsideringCcusSummaryService.IS_SHALLOWER_HORIZONS,
            "No"
        )
    ));

    var expectedSummaryCard = SummaryCard.simpleSummaryCardWithHeading(
        ConsideringCcusSummaryService.CONSIDERING_CCUS,
        expectedSummaryData);

    assertThat(consideringCcusSummaryService.getConsideringCcusSummary(submissionDetail))
        .usingRecursiveComparison()
        .isEqualTo(expectedSummaryCard);
  }

  @Test
  void getConsideringCcusSummary_existingDetail_noAnswersGivenYet_thenNoValues() {
    var consideringCcusDetail = new ConsideringCcusDetail();
    when(consideringCcusDetailRepository.findConsideringCcusDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(consideringCcusDetail));

    var expectedSummaryData = new SummaryDataView(List.of(
        new SummaryKeyValue(
            ConsideringCcusSummaryService.IS_CONNECTED_TO_PROJECTS,
            null
        ),
        new SummaryKeyValue(
            ConsideringCcusSummaryService.IS_SHALLOWER_HORIZONS,
            null
        )
    ));

    var expectedSummaryCard = SummaryCard.simpleSummaryCardWithHeading(
        ConsideringCcusSummaryService.CONSIDERING_CCUS,
        expectedSummaryData);

    assertThat(consideringCcusSummaryService.getConsideringCcusSummary(submissionDetail))
        .usingRecursiveComparison()
        .isEqualTo(expectedSummaryCard);
  }

  @Test
  void getConsideringCcusSummary_noExistingDetail_thenEmptySummaryCard() {
    when(consideringCcusDetailRepository.findConsideringCcusDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    assertThat(consideringCcusSummaryService.getConsideringCcusSummary(submissionDetail))
        .usingRecursiveComparison()
        .isEqualTo(SummaryCard.emptySummaryCardWithHeading(ConsideringCcusSummaryService.CONSIDERING_CCUS));
  }
}
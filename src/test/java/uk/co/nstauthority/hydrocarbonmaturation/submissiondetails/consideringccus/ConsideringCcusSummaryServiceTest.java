package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.consideringccus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCardType;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryDataView;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryKeyValue;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;

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

  @ParameterizedTest
  @MethodSource("allNonFieldSubmissionTypes")
  void getSummarySection_notFieldSubmission_thenEmpty(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);
    assertThat(consideringCcusSummaryService.getSummarySection(submissionDetail)).isEmpty();
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void getSummarySection_assertStructure(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    when(consideringCcusDetailRepository.findConsideringCcusDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    var summarySectionOptional = consideringCcusSummaryService.getSummarySection(submissionDetail);
    assertThat(summarySectionOptional).isPresent();

    var summarySection = summarySectionOptional.get();
    assertThat(summarySection)
        .extracting(
            SummarySection::displayOrder,
            SummarySection::summaryText
        ).containsExactly(
           ConsideringCcusSummaryService.CONSIDERING_CCUS_DISPLAY_ORDER,
            ""
        );
    var summaryItems = summarySection.summaryItems();
    assertThat(summaryItems).hasSize(1);

     var summaryItem = summaryItems.get(0);
     assertThat(summaryItem.displayName()).isEqualTo(ConsideringCcusSummaryService.CONSIDERING_CCUS);

     var summaryCards = summaryItem.summaryCards();
     assertThat(summaryCards).hasSize(1);

     var summaryCard = summaryCards.get(0);
     assertThat(summaryCard)
         .extracting(
             SummaryCard::displayName,
             SummaryCard::summaryCardType
         ).containsExactly(
             null,
             SummaryCardType.SIMPLE_SUMMARY
         );

     var summaryData = summaryCard.summaryData();
     assertThat(summaryData).isNotNull();
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void getSummarySection_existingDetail_answeredYes_thenValues(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    var consideringCcusDetail = new ConsideringCcusDetail();
    consideringCcusDetail.setIsConnectedToProject(true);
    consideringCcusDetail.setConnectedProjects("projects");
    consideringCcusDetail.setIsAnyShallowerHorizons(true);
    consideringCcusDetail.setAgeAndFormationOfHorizons("ages");
    when(consideringCcusDetailRepository.findConsideringCcusDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(consideringCcusDetail));

    SummaryDataView summaryData = (SummaryDataView) consideringCcusSummaryService.getSummarySection(submissionDetail).get()
        .summaryItems()
        .get(0)
        .summaryCards()
        .get(0)
        .summaryData();

    assertThat(summaryData.keyValues())
        .extracting(
            SummaryKeyValue::key,
            SummaryKeyValue::value
        ).containsExactly(
            Tuple.tuple(ConsideringCcusSummaryService.IS_CONNECTED_TO_PROJECTS,
                "Yes"),
            Tuple.tuple(ConsideringCcusSummaryService.CONNECTED_PROJECTS,
                consideringCcusDetail.getConnectedProjects()),
            Tuple.tuple(ConsideringCcusSummaryService.IS_SHALLOWER_HORIZONS,
                "Yes"),
            Tuple.tuple(ConsideringCcusSummaryService.AGE_AND_FORMATION_OF_HORIZONS,
                consideringCcusDetail.getAgeAndFormationOfHorizons())
        );
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void getSummarySection_existingDetail_answeredNo_thenDoNotShowSubAnswers(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    var consideringCcusDetail = new ConsideringCcusDetail();
    consideringCcusDetail.setIsConnectedToProject(false);
    consideringCcusDetail.setIsAnyShallowerHorizons(false);

    when(consideringCcusDetailRepository.findConsideringCcusDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.of(consideringCcusDetail));

    SummaryDataView summaryData = (SummaryDataView) consideringCcusSummaryService.getSummarySection(submissionDetail).get()
        .summaryItems()
        .get(0)
        .summaryCards()
        .get(0)
        .summaryData();

    assertThat(summaryData.keyValues())
        .extracting(
            SummaryKeyValue::key,
            SummaryKeyValue::value
        ).containsExactly(
            Tuple.tuple(ConsideringCcusSummaryService.IS_CONNECTED_TO_PROJECTS, "No"),
            Tuple.tuple(ConsideringCcusSummaryService.IS_SHALLOWER_HORIZONS, "No")
        );
  }

  @ParameterizedTest
  @MethodSource("allFieldSubmissionTypes")
  void getSummarySection_noExistingDetail_thenNoValues(SubmissionType submissionType) {
    submissionDetail.setSubmissionType(submissionType);

    when(consideringCcusDetailRepository.findConsideringCcusDetailBySubmissionDetail(submissionDetail))
        .thenReturn(Optional.empty());

    SummaryDataView summaryData = (SummaryDataView) consideringCcusSummaryService.getSummarySection(submissionDetail).get()
        .summaryItems()
        .get(0)
        .summaryCards()
        .get(0)
        .summaryData();

    assertThat(summaryData.keyValues())
        .extracting(
            SummaryKeyValue::key,
            SummaryKeyValue::value
        ).containsExactly(
            Tuple.tuple(ConsideringCcusSummaryService.IS_CONNECTED_TO_PROJECTS, ""),
            Tuple.tuple(ConsideringCcusSummaryService.IS_SHALLOWER_HORIZONS, "")
        );
  }

  private static Stream<Arguments> allFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(SubmissionDetailUtil::isFieldSubmission)
        .map(Arguments::of);
  }

  private static Stream<Arguments> allNonFieldSubmissionTypes() {
    return Arrays.stream(SubmissionType.values())
        .filter(submissionType -> !SubmissionDetailUtil.isFieldSubmission(submissionType))
        .map(Arguments::of);
  }
}
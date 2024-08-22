package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedsanctionedwells.PlannedSanctionedWellsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.plannedunsanctionedwells.PlannedUnsanctionedWellsSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.fieldsummarysectionservices.WellInformationSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;

@ExtendWith(MockitoExtension.class)
class WellInformationSummaryServiceTest {
  private static final User USER = UserTestUtil.Builder().build();
  @Mock
  private PlannedSanctionedWellsSummaryService plannedSanctionedWellsSummaryService;
  @Mock
  private PlannedUnsanctionedWellsSummaryService plannedUnsanctionedWellsSummaryService;
  @InjectMocks
  WellInformationSummaryService wellInformationSummaryService;

  private SubmissionDetail submissionDetail;

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_FDP", "CREATE_FDPA"},
      mode = EnumSource.Mode.EXCLUDE
  )
  void getSummarySection_notFdpOrFdpa_thenEmpty(SubmissionType submissionType) {
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    assertThat(wellInformationSummaryService.getSummarySection(submissionDetail, USER)).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(
      value = SubmissionType.class,
      names = {"CREATE_FDP", "CREATE_FDPA"}
  )
  void getSummarySection_isFdpOrFdpa_thenAssertProperties(SubmissionType submissionType) {
    submissionDetail = SubmissionDetailTestUtils.createSubmissionDetail(submissionType);

    var plannedSanctionedWellsCard = SummaryCard.emptySummaryCard();
    var wellTypesCard = SummaryCard.emptySummaryCard();
    var holeCompletionTypesCard = SummaryCard.emptySummaryCard();
    var plannedCompletionTypesCard = SummaryCard.emptySummaryCard();
    var artificialLiftMechanismCard = SummaryCard.emptySummaryCard();
    var plannedUnsanctionedWellsCard = SummaryCard.emptySummaryCard();

    var expectedCards = List.of(
        plannedSanctionedWellsCard,
        wellTypesCard,
        holeCompletionTypesCard,
        plannedCompletionTypesCard,
        artificialLiftMechanismCard,
        plannedUnsanctionedWellsCard
    );

    var expectedSummaryItems = List.of(SummaryItem.withCards(WellInformationSummaryService.WELL_INFORMATION, expectedCards));

    when(plannedSanctionedWellsSummaryService.getPlannedSanctionedWellsCard(submissionDetail))
        .thenReturn(plannedSanctionedWellsCard);
    when(plannedSanctionedWellsSummaryService.getWellTypesSummaryCard(submissionDetail))
        .thenReturn(wellTypesCard);
    when(plannedSanctionedWellsSummaryService.getHoleCompletionTypesSummaryCard(submissionDetail))
        .thenReturn(holeCompletionTypesCard);
    when(plannedSanctionedWellsSummaryService.getPlannedCompletionTypesSummaryCard(submissionDetail))
        .thenReturn(plannedCompletionTypesCard);
    when(plannedSanctionedWellsSummaryService.getArtificialLiftMechanismsSummaryCard(submissionDetail))
        .thenReturn(artificialLiftMechanismCard);
    when(plannedUnsanctionedWellsSummaryService.getPlannedSanctionedWellsCard(submissionDetail))
        .thenReturn(plannedUnsanctionedWellsCard);

    var summarySectionOptional = wellInformationSummaryService.getSummarySection(submissionDetail, USER);

    assertThat(summarySectionOptional).isPresent();

    assertThat(summarySectionOptional.get())
        .extracting(
            SummarySection::displayOrder,
            SummarySection::summaryText
        ).containsExactly(
            WellInformationSummaryService.WELL_INFORMATION_SECTION_ORDER,
            WellInformationSummaryService.SUMMARY_DESCRIPTION
        );

    assertThat(summarySectionOptional.get().summaryItems())
        .usingRecursiveComparison()
        .isEqualTo(expectedSummaryItems);
  }
}
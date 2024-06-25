package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.fieldsummarysectionservices;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionType;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fielddevelopmentplanaddendumincreasedvolumes.FieldDevelopmentPlanAddendumIncreasedVolumesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;

@ExtendWith(MockitoExtension.class)
class FieldDevelopmentPlanAddendumIncreasedVolumesSummarySectionServiceTest {

  @Mock
  private FieldDevelopmentPlanAddendumIncreasedVolumesSummaryService fdpaIncreasedVolumesSummaryService;

  @InjectMocks
  private FieldDevelopmentPlanAddendumIncreasedVolumesSummarySectionService summarySectionService;

  @Test
  void getSummarySection_whenIsFdpa_returnsSection() {
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDPA);

    var informationCard = SummaryCard.emptySummaryCard();
    var volumetricsCard1 = SummaryCard.emptySummaryCard();
    var volumetricsCard2 = SummaryCard.emptySummaryCard();

    when(fdpaIncreasedVolumesSummaryService.getGeneralInformationSummaryCard(submissionDetail))
        .thenReturn(informationCard);
    when(fdpaIncreasedVolumesSummaryService.getVolumetricsSummaryCards(submissionDetail))
        .thenReturn(List.of(volumetricsCard1, volumetricsCard2));

    var expectedSummaryItem = SummaryItem.withCards(
        FieldDevelopmentPlanAddendumIncreasedVolumesSummarySectionService.SUMMARY_TITLE,
        List.of(informationCard, volumetricsCard1, volumetricsCard2)
    );

    assertThat(summarySectionService.getSummarySection(submissionDetail))
        .isPresent()
        .get()
        .usingRecursiveComparison()
        .isEqualTo(
            new SummarySection(
                FieldDevelopmentPlanAddendumIncreasedVolumesSummarySectionService.SECTION_ORDER,
                List.of(expectedSummaryItem),
                FieldDevelopmentPlanAddendumIncreasedVolumesSummarySectionService.SUMMARY_DESCRIPTION
            )
        );
  }

  @ParameterizedTest
  @EnumSource(value = SubmissionType.class, names = "CREATE_FDPA", mode = EnumSource.Mode.EXCLUDE)
  void getSummarySection_whenIsNotFdpa_noSectionReturned(SubmissionType submissionType) {
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setSubmissionType(submissionType);

    assertThat(summarySectionService.getSummarySection(submissionDetail))
        .isEmpty();
  }
}
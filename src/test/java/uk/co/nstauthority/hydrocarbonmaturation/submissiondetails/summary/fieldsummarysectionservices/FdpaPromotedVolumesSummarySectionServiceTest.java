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
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.fdpapromotedvolumes.FdpaPromotedVolumesSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryCard;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummaryItem;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;

@ExtendWith(MockitoExtension.class)
class FdpaPromotedVolumesSummarySectionServiceTest {

  @Mock
  private FdpaPromotedVolumesSummaryService fdpaPromotedVolumesSummaryService;

  @InjectMocks
  private FdpaPromotedVolumesSummarySectionService summarySectionService;

  @Test
  void getSummarySection_whenIsFdpa_returnsSection() {
    var submissionDetail = new SubmissionDetail();
    submissionDetail.setSubmissionType(SubmissionType.CREATE_FDPA);

    var informationCard = SummaryCard.emptySummaryCard();
    var volumetricsCard = SummaryCard.emptySummaryCard();

    when(fdpaPromotedVolumesSummaryService.getGeneralInformationSummaryCard(submissionDetail))
        .thenReturn(informationCard);
    when(fdpaPromotedVolumesSummaryService.getPromotedResourcesVolumetricsCard(submissionDetail))
        .thenReturn(volumetricsCard);

    var expectedSummaryItem = SummaryItem.withCards(
        FdpaPromotedVolumesSummarySectionService.SUMMARY_TITLE,
        List.of(informationCard, volumetricsCard)
    );

    assertThat(summarySectionService.getSummarySection(submissionDetail))
        .isPresent()
        .get()
        .usingRecursiveComparison()
        .isEqualTo(
            new SummarySection(
                FdpaPromotedVolumesSummarySectionService.SECTION_ORDER,
                List.of(expectedSummaryItem),
                FdpaPromotedVolumesSummarySectionService.SUMMARY_DESCRIPTION
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
package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.fieldsummarysectionservices.GeneralDetailsSummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;

@ExtendWith(MockitoExtension.class)
class SubmissionSummaryServiceTest {

  @Mock
  private GeneralDetailsSummarySectionService generalDetailsSummarySectionService;

  private SubmissionSummaryService submissionSummaryService;

  @BeforeEach
  void setUp() {
    submissionSummaryService = new SubmissionSummaryService(
        List.of(generalDetailsSummarySectionService)
    );
  }

  @Test
  void getSubmissionSummaries_ForField() {
    var submissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    when(generalDetailsSummarySectionService.getSummarySection(submissionDetail))
        .thenReturn(Optional.of(SubmissionSummaryTestUtil.getGeneralDetailsSummarySection(null)));
    var submissionSummaries = submissionSummaryService.getSummarySections(submissionDetail);
    var generalDetailsSummarySection = new SummarySection(GeneralDetailSummaryService.GENERAL_DETAIL_SECTION_ORDER, null, "");
    assertThat(submissionSummaries).containsExactly(generalDetailsSummarySection);
  }
}

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
import uk.co.nstauthority.hydrocarbonmaturation.authentication.User;
import uk.co.nstauthority.hydrocarbonmaturation.authentication.UserTestUtil;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.generaldetails.GeneralDetailSummaryService;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.summary.fieldsummarysectionservices.FieldGeneralDetailsSummarySectionService;
import uk.co.nstauthority.hydrocarbonmaturation.summary.SummarySection;

@ExtendWith(MockitoExtension.class)
class SubmissionSummaryServiceTest {
  private static final User USER = UserTestUtil.Builder().build();

  @Mock
  private FieldGeneralDetailsSummarySectionService fieldGeneralDetailsSummarySectionService;

  private SubmissionSummaryService submissionSummaryService;

  @BeforeEach
  void setUp() {
    submissionSummaryService = new SubmissionSummaryService(
        List.of(fieldGeneralDetailsSummarySectionService)
    );
  }

  @Test
  void getSubmissionSummaries_ForField() {
    var submissionDetail = SubmissionDetailTestUtils.createNewNonCopFieldSubmissionDetail();
    when(fieldGeneralDetailsSummarySectionService.getSummarySection(submissionDetail, USER))
        .thenReturn(Optional.of(SubmissionSummaryTestUtil.getGeneralDetailsSummarySection(null)));
    var submissionSummaries = submissionSummaryService.getSummarySections(submissionDetail, USER);
    var generalDetailsSummarySection = new SummarySection(GeneralDetailSummaryService.GENERAL_DETAIL_SECTION_ORDER, null, "");
    assertThat(submissionSummaries).containsExactly(generalDetailsSummarySection);
  }
}

package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetailTestUtils;

@ExtendWith(MockitoExtension.class)
class ChanceOfSuccessSummaryServiceTest {

  @Mock
  private ChanceOfSuccessPersistenceService chanceOfSuccessPersistenceService;

  @InjectMocks
  private ChanceOfSuccessSummaryService chanceOfSuccessSummaryService;

  private SubmissionDetail submissionDetail;
  private ChanceOfSuccess chanceOfSuccess;

  @BeforeEach
  void setUp() {
    submissionDetail = SubmissionDetailTestUtils.createNewProspectSubmissionDetail();
    chanceOfSuccess = new ChanceOfSuccess();
    chanceOfSuccess.setSubmissionDetail(submissionDetail);
  }

  @Test
  void getChanceOfSuccessSummaryFromSubmissionDetail_whenPresent_fullSummary() {
    when(chanceOfSuccessPersistenceService.getChanceOfSuccess(submissionDetail))
        .thenReturn(Optional.of(chanceOfSuccess));
    chanceOfSuccess.setGeophysicallyModified(true);
    chanceOfSuccess.setGeophysicallyModifiedComments("comments");
    chanceOfSuccess.setSourcePercentage(BigDecimal.valueOf(5.5));
    chanceOfSuccess.setSourceComments("comment");
    chanceOfSuccess.setTopSealPercentage(BigDecimal.valueOf(5.0));
    chanceOfSuccess.setTopSealComments("comment");
    chanceOfSuccess.setReservoirPercentage(BigDecimal.valueOf(5.0));
    chanceOfSuccess.setReservoirComments("comment");
    chanceOfSuccess.setPlayTotal(BigDecimal.valueOf(5.0));
    chanceOfSuccess.setProspectSourcePercentage(BigDecimal.valueOf(5.0));
    chanceOfSuccess.setProspectSourceComments("comment");
    chanceOfSuccess.setProspectReservoirPercentage(BigDecimal.valueOf(5.0));
    chanceOfSuccess.setProspectReservoirComments("comment");
    chanceOfSuccess.setProspectTopSealPercentage(BigDecimal.valueOf(5.0));
    chanceOfSuccess.setProspectTopSealComments("comment");
    chanceOfSuccess.setProspectTrapPercentage(BigDecimal.valueOf(5.0));
    chanceOfSuccess.setProspectTrapComments("comment");
    chanceOfSuccess.setProspectMigrationPercentage(BigDecimal.valueOf(5.0));
    chanceOfSuccess.setProspectMigrationComments("comment");
    chanceOfSuccess.setProspectSpecificTotal(BigDecimal.valueOf(10.10));
    chanceOfSuccess.setOverallTotal(BigDecimal.valueOf(90.99));
    chanceOfSuccess.setRelatedToSpecificWellLocation(true);
    chanceOfSuccess.setWellModifiedPercentage(BigDecimal.valueOf(5.0));
    chanceOfSuccess.setWellModifiedComments("comment");
    chanceOfSuccess.setSubmissionDetail(submissionDetail);
    chanceOfSuccess.setGeophysicallyModifiedPercentage(BigDecimal.valueOf(5.0));

    var view = chanceOfSuccessSummaryService.getChanceOfSuccessSummaryFromSubmissionDetail(submissionDetail);
    assertThat(view)
        .extracting(
            ChanceOfSuccessSummaryView::playSourcePercentage,
            ChanceOfSuccessSummaryView::playSourceComments,
            ChanceOfSuccessSummaryView::playReservoirPercentage,
            ChanceOfSuccessSummaryView::playReservoirComments,
            ChanceOfSuccessSummaryView::playTopSealPercentage,
            ChanceOfSuccessSummaryView::playTopSealComments,
            ChanceOfSuccessSummaryView::totalPlayChanceOfSuccess,
            ChanceOfSuccessSummaryView::prospectSpecificReservoirPercentage,
            ChanceOfSuccessSummaryView::prospectSpecificReservoirComments,
            ChanceOfSuccessSummaryView::prospectSpecificTopSealPercentage,
            ChanceOfSuccessSummaryView::prospectSpecificTopSealComments,
            ChanceOfSuccessSummaryView::prospectSpecificTrapPercentage,
            ChanceOfSuccessSummaryView::prospectSpecificTrapComments,
            ChanceOfSuccessSummaryView::prospectSpecificMigrationPercentage,
            ChanceOfSuccessSummaryView::prospectSpecificMigrationComments,
            ChanceOfSuccessSummaryView::totalProspectSpecificChance,
            ChanceOfSuccessSummaryView::overallChanceOfSuccess,
            ChanceOfSuccessSummaryView::geophysicallyModified,
            ChanceOfSuccessSummaryView::geophysicallyModifiedPercentage,
            ChanceOfSuccessSummaryView::geophysicallyModifiedComments,
            ChanceOfSuccessSummaryView::specificWellLocation,
            ChanceOfSuccessSummaryView::wellModifiedPercentage,
            ChanceOfSuccessSummaryView::wellModifiedComments)
        .containsExactly(
            chanceOfSuccess.getSourcePercentage().toString() + "%",
            chanceOfSuccess.getSourceComments(),
            chanceOfSuccess.getReservoirPercentage().toString() + "%",
            chanceOfSuccess.getReservoirComments(),
            chanceOfSuccess.getTopSealPercentage().toString() + "%",
            chanceOfSuccess.getTopSealComments(),
            chanceOfSuccess.getPlayTotal().toString() + "%",
            chanceOfSuccess.getProspectReservoirPercentage().toString() + "%",
            chanceOfSuccess.getProspectReservoirComments(),
            chanceOfSuccess.getProspectTopSealPercentage().toString() + "%",
            chanceOfSuccess.getProspectTopSealComments(),
            chanceOfSuccess.getProspectTrapPercentage().toString() + "%",
            chanceOfSuccess.getProspectTrapComments(),
            chanceOfSuccess.getProspectMigrationPercentage().toString() + "%",
            chanceOfSuccess.getProspectMigrationComments(),
            chanceOfSuccess.getProspectSpecificTotal().toString() + "%",
            chanceOfSuccess.getOverallTotal().toString() + "%",
            "Yes",
            chanceOfSuccess.getGeophysicallyModifiedPercentage().toString() + "%",
            chanceOfSuccess.getGeophysicallyModifiedComments(),
            "Yes",
            chanceOfSuccess.getWellModifiedPercentage().toString() + "%",
            chanceOfSuccess.getWellModifiedComments()
        );
  }

  @Test
  void getChanceOfSuccessSummaryFromSubmissionDetail_whenPresentEmpty_emptySummary() {
    when(chanceOfSuccessPersistenceService.getChanceOfSuccess(submissionDetail))
        .thenReturn(Optional.of(chanceOfSuccess));

    var view = chanceOfSuccessSummaryService.getChanceOfSuccessSummaryFromSubmissionDetail(submissionDetail);
    assertThat(view)
        .extracting(
            ChanceOfSuccessSummaryView::playSourcePercentage,
            ChanceOfSuccessSummaryView::playSourceComments,
            ChanceOfSuccessSummaryView::playReservoirPercentage,
            ChanceOfSuccessSummaryView::playReservoirComments,
            ChanceOfSuccessSummaryView::playTopSealPercentage,
            ChanceOfSuccessSummaryView::playTopSealComments,
            ChanceOfSuccessSummaryView::totalPlayChanceOfSuccess,
            ChanceOfSuccessSummaryView::prospectSpecificReservoirPercentage,
            ChanceOfSuccessSummaryView::prospectSpecificReservoirComments,
            ChanceOfSuccessSummaryView::prospectSpecificTopSealPercentage,
            ChanceOfSuccessSummaryView::prospectSpecificTopSealComments,
            ChanceOfSuccessSummaryView::prospectSpecificTrapPercentage,
            ChanceOfSuccessSummaryView::prospectSpecificTrapComments,
            ChanceOfSuccessSummaryView::prospectSpecificMigrationPercentage,
            ChanceOfSuccessSummaryView::prospectSpecificMigrationComments,
            ChanceOfSuccessSummaryView::totalProspectSpecificChance,
            ChanceOfSuccessSummaryView::overallChanceOfSuccess,
            ChanceOfSuccessSummaryView::geophysicallyModified,
            ChanceOfSuccessSummaryView::geophysicallyModifiedPercentage,
            ChanceOfSuccessSummaryView::geophysicallyModifiedComments,
            ChanceOfSuccessSummaryView::specificWellLocation,
            ChanceOfSuccessSummaryView::wellModifiedPercentage,
            ChanceOfSuccessSummaryView::wellModifiedComments)
        .containsExactly(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        );
  }

  @Test
  void getChanceOfSuccessSummaryFromSubmissionDetail_whenNotPresent_emptySummary() {
    when(chanceOfSuccessPersistenceService.getChanceOfSuccess(submissionDetail))
        .thenReturn(Optional.empty());

    var view = chanceOfSuccessSummaryService.getChanceOfSuccessSummaryFromSubmissionDetail(submissionDetail);
    assertThat(view)
        .extracting(
            ChanceOfSuccessSummaryView::playSourcePercentage,
            ChanceOfSuccessSummaryView::playSourceComments,
            ChanceOfSuccessSummaryView::playReservoirPercentage,
            ChanceOfSuccessSummaryView::playReservoirComments,
            ChanceOfSuccessSummaryView::playTopSealPercentage,
            ChanceOfSuccessSummaryView::playTopSealComments,
            ChanceOfSuccessSummaryView::totalPlayChanceOfSuccess,
            ChanceOfSuccessSummaryView::prospectSpecificReservoirPercentage,
            ChanceOfSuccessSummaryView::prospectSpecificReservoirComments,
            ChanceOfSuccessSummaryView::prospectSpecificTopSealPercentage,
            ChanceOfSuccessSummaryView::prospectSpecificTopSealComments,
            ChanceOfSuccessSummaryView::prospectSpecificTrapPercentage,
            ChanceOfSuccessSummaryView::prospectSpecificTrapComments,
            ChanceOfSuccessSummaryView::prospectSpecificMigrationPercentage,
            ChanceOfSuccessSummaryView::prospectSpecificMigrationComments,
            ChanceOfSuccessSummaryView::totalProspectSpecificChance,
            ChanceOfSuccessSummaryView::overallChanceOfSuccess,
            ChanceOfSuccessSummaryView::geophysicallyModified,
            ChanceOfSuccessSummaryView::geophysicallyModifiedPercentage,
            ChanceOfSuccessSummaryView::geophysicallyModifiedComments,
            ChanceOfSuccessSummaryView::specificWellLocation,
            ChanceOfSuccessSummaryView::wellModifiedPercentage,
            ChanceOfSuccessSummaryView::wellModifiedComments)
        .containsExactly(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        );
  }
}
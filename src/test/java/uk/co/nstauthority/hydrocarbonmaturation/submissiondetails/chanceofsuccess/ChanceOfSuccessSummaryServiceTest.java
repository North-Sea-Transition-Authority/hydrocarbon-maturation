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
    chanceOfSuccess.setProspectTrapPresence(BigDecimal.valueOf(5.0));
    chanceOfSuccess.setProspectTrapPresenceComments("comment");
    chanceOfSuccess.setProspectTrapEffectiveness(BigDecimal.valueOf(5.0));
    chanceOfSuccess.setProspectTrapEffectivenessComments("comment");
    chanceOfSuccess.setProspectSourcePresence(BigDecimal.valueOf(5.0));
    chanceOfSuccess.setProspectSourcePresenceComments("comment");
    chanceOfSuccess.setProspectSourceEffectiveness(BigDecimal.valueOf(5.0));
    chanceOfSuccess.setProspectSourceEffectivenessComments("comment");
    chanceOfSuccess.setProspectReservoirPresence(BigDecimal.valueOf(5.0));
    chanceOfSuccess.setProspectReservoirPresenceComments("comment");
    chanceOfSuccess.setProspectReservoirEffectiveness(BigDecimal.valueOf(5.0));
    chanceOfSuccess.setProspectReservoirEffectivenessComments("comment");
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
            ChanceOfSuccessSummaryView::prospectSpecificTrapPresence,
            ChanceOfSuccessSummaryView::prospectSpecificTrapPresenceComments,
            ChanceOfSuccessSummaryView::prospectSpecificTrapEffectiveness,
            ChanceOfSuccessSummaryView::prospectSpecificTrapEffectivenessComments,
            ChanceOfSuccessSummaryView::prospectSpecificSourcePresence,
            ChanceOfSuccessSummaryView::prospectSpecificSourcePresenceComments,
            ChanceOfSuccessSummaryView::prospectSpecificSourceEffectiveness,
            ChanceOfSuccessSummaryView::prospectSpecificSourceEffectivenessComments,
            ChanceOfSuccessSummaryView::prospectSpecificReservoirPresence,
            ChanceOfSuccessSummaryView::prospectSpecificReservoirPresenceComments,
            ChanceOfSuccessSummaryView::prospectSpecificReservoirEffectiveness,
            ChanceOfSuccessSummaryView::prospectSpecificReservoirEffectivenessComments,
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
            chanceOfSuccess.getProspectTrapPresence().toString() + "%",
            chanceOfSuccess.getProspectTrapPresenceComments(),
            chanceOfSuccess.getProspectTrapEffectiveness().toString() + "%",
            chanceOfSuccess.getProspectTrapEffectivenessComments(),
            chanceOfSuccess.getProspectSourcePresence().toString() + "%",
            chanceOfSuccess.getProspectSourcePresenceComments(),
            chanceOfSuccess.getProspectSourceEffectiveness().toString() + "%",
            chanceOfSuccess.getProspectSourceEffectivenessComments(),
            chanceOfSuccess.getProspectReservoirPresence().toString() + "%",
            chanceOfSuccess.getProspectReservoirPresenceComments(),
            chanceOfSuccess.getProspectReservoirEffectiveness().toString() + "%",
            chanceOfSuccess.getProspectReservoirEffectivenessComments(),
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
            ChanceOfSuccessSummaryView::prospectSpecificTrapPresence,
            ChanceOfSuccessSummaryView::prospectSpecificTrapPresenceComments,
            ChanceOfSuccessSummaryView::prospectSpecificTrapEffectiveness,
            ChanceOfSuccessSummaryView::prospectSpecificTrapEffectivenessComments,
            ChanceOfSuccessSummaryView::prospectSpecificSourcePresence,
            ChanceOfSuccessSummaryView::prospectSpecificSourcePresenceComments,
            ChanceOfSuccessSummaryView::prospectSpecificSourceEffectiveness,
            ChanceOfSuccessSummaryView::prospectSpecificSourceEffectivenessComments,
            ChanceOfSuccessSummaryView::prospectSpecificReservoirPresence,
            ChanceOfSuccessSummaryView::prospectSpecificReservoirPresenceComments,
            ChanceOfSuccessSummaryView::prospectSpecificReservoirEffectiveness,
            ChanceOfSuccessSummaryView::prospectSpecificReservoirEffectivenessComments,
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
            ChanceOfSuccessSummaryView::prospectSpecificTrapPresence,
            ChanceOfSuccessSummaryView::prospectSpecificTrapPresenceComments,
            ChanceOfSuccessSummaryView::prospectSpecificTrapEffectiveness,
            ChanceOfSuccessSummaryView::prospectSpecificTrapEffectivenessComments,
            ChanceOfSuccessSummaryView::prospectSpecificSourcePresence,
            ChanceOfSuccessSummaryView::prospectSpecificSourcePresenceComments,
            ChanceOfSuccessSummaryView::prospectSpecificSourceEffectiveness,
            ChanceOfSuccessSummaryView::prospectSpecificSourceEffectivenessComments,
            ChanceOfSuccessSummaryView::prospectSpecificReservoirPresence,
            ChanceOfSuccessSummaryView::prospectSpecificReservoirPresenceComments,
            ChanceOfSuccessSummaryView::prospectSpecificReservoirEffectiveness,
            ChanceOfSuccessSummaryView::prospectSpecificReservoirEffectivenessComments,
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
            "",
            "",
            "",
            "",
            ""
        );
  }
}
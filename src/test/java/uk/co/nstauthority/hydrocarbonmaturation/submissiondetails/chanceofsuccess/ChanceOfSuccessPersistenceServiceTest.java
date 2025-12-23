package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@ExtendWith(MockitoExtension.class)
class ChanceOfSuccessPersistenceServiceTest {

  @Mock
  private static ChanceOfSuccessRepository chanceOfSuccessRepository;
  private static ChanceOfSuccessPersistenceService chanceOfSuccessPersistenceService;

  private static SubmissionDetail submissionDetail;

  @BeforeEach
  void setup() {
    chanceOfSuccessPersistenceService = new ChanceOfSuccessPersistenceService(chanceOfSuccessRepository);

    UUID submissionDetailId = UUID.randomUUID();
    submissionDetail = new SubmissionDetail();
    submissionDetail.setId(submissionDetailId);
  }
  
  @Test
  void getChanceOfSuccess_verifyMethodCall() {
    chanceOfSuccessPersistenceService.getChanceOfSuccess(submissionDetail);
    verify(chanceOfSuccessRepository, times(1)).findBySubmissionDetail(submissionDetail);
  }

  @Test
  void saveChanceOfSuccess_whenGivenAForm_verifyCreatedEntity() {
    var form = ChanceOfSuccessTestUtil.createChanceOfSuccessForm();

    var chanceOfSuccessArgumentCaptor = ArgumentCaptor.forClass(ChanceOfSuccess.class);

    chanceOfSuccessPersistenceService.saveChanceOfSuccess(form, submissionDetail);

    verify(chanceOfSuccessRepository, times(1)).save(chanceOfSuccessArgumentCaptor.capture());
    var savedChanceOfSuccess = (ChanceOfSuccess) chanceOfSuccessArgumentCaptor.getValue();

    assertThat(savedChanceOfSuccess)
        .extracting(
            ChanceOfSuccess::getSourcePercentage,
            ChanceOfSuccess::getSourceComments,
            ChanceOfSuccess::getReservoirPercentage,
            ChanceOfSuccess::getReservoirComments,
            ChanceOfSuccess::getTopSealPercentage,
            ChanceOfSuccess::getTopSealComments,
            ChanceOfSuccess::getProspectTrapPresence,
            ChanceOfSuccess::getProspectTrapPresenceComments,
            ChanceOfSuccess::getProspectTrapEffectiveness,
            ChanceOfSuccess::getProspectTrapEffectivenessComments,
            ChanceOfSuccess::getProspectSourcePresence,
            ChanceOfSuccess::getProspectSourcePresenceComments,
            ChanceOfSuccess::getProspectSourceEffectiveness,
            ChanceOfSuccess::getProspectSourceEffectivenessComments,
            ChanceOfSuccess::getProspectReservoirPresence,
            ChanceOfSuccess::getProspectReservoirPresenceComments,
            ChanceOfSuccess::getProspectReservoirEffectiveness,
            ChanceOfSuccess::getProspectReservoirEffectivenessComments,
            ChanceOfSuccess::getGeophysicallyModified,
            ChanceOfSuccess::getGeophysicallyModifiedPercentage,
            ChanceOfSuccess::getGeophysicallyModifiedComments,
            ChanceOfSuccess::getRelatedToSpecificWellLocation,
            ChanceOfSuccess::getWellModifiedPercentage,
            ChanceOfSuccess::getWellModifiedComments
        )
        .containsExactly(
            form.getPlayChanceOfSuccess().getSourcePercentage().createBigDecimalOrNull(),
            form.getPlayChanceOfSuccess().getSourceComments(),
            form.getPlayChanceOfSuccess().getReservoirPercentage().createBigDecimalOrNull(),
            form.getPlayChanceOfSuccess().getReservoirComments(),
            form.getPlayChanceOfSuccess().getTopSealPercentage().createBigDecimalOrNull(),
            form.getPlayChanceOfSuccess().getTopSealComments(),
            form.getProspectSpecificChanceOfSuccess().getTrapPresence().createBigDecimalOrNull(),
            form.getProspectSpecificChanceOfSuccess().getTrapPresenceComments(),
            form.getProspectSpecificChanceOfSuccess().getTrapEffectiveness().createBigDecimalOrNull(),
            form.getProspectSpecificChanceOfSuccess().getTrapEffectivenessComments(),
            form.getProspectSpecificChanceOfSuccess().getSourcePresence().createBigDecimalOrNull(),
            form.getProspectSpecificChanceOfSuccess().getSourcePresenceComments(),
            form.getProspectSpecificChanceOfSuccess().getSourceEffectiveness().createBigDecimalOrNull(),
            form.getProspectSpecificChanceOfSuccess().getSourceEffectivenessComments(),
            form.getProspectSpecificChanceOfSuccess().getReservoirPresence().createBigDecimalOrNull(),
            form.getProspectSpecificChanceOfSuccess().getReservoirPresenceComments(),
            form.getProspectSpecificChanceOfSuccess().getReservoirEffectiveness().createBigDecimalOrNull(),
            form.getProspectSpecificChanceOfSuccess().getReservoirEffectivenessComments(),
            form.getGeophysicallyModified(),
            form.getGeophysicallyModifiedPercentage().createBigDecimalOrNull(),
            form.getGeophysicallyModifiedComments(),
            form.getRelatedToSpecificWellLocation(),
            form.getWellModifiedPercentage().createBigDecimalOrNull(),
            form.getWellModifiedComments()
        );
  }

  @Test
  void saveChanceOfSuccess_whenMissingPercentages_totalsAreZero() {
    var form = new ChanceOfSuccessForm();
    var chanceOfSuccessDetailsForm = new PlayChanceOfSuccessDetailsForm();
    var prospectForm = new ProspectSpecificChanceOfSuccessForm();
    form.setPlayChanceOfSuccess(chanceOfSuccessDetailsForm);
    form.setProspectSpecificChanceOfSuccess(prospectForm);

    var chanceOfSuccessArgumentCaptor = ArgumentCaptor.forClass(ChanceOfSuccess.class);

    chanceOfSuccessPersistenceService.saveChanceOfSuccess(form, submissionDetail);

    verify(chanceOfSuccessRepository, times(1)).save(chanceOfSuccessArgumentCaptor.capture());
    var savedChanceOfSuccess = chanceOfSuccessArgumentCaptor.getValue();

    assertThat(savedChanceOfSuccess)
        .extracting(
            ChanceOfSuccess::getPlayTotal,
            ChanceOfSuccess::getProspectSpecificTotal,
            ChanceOfSuccess::getOverallTotal
        )
        .containsExactly(
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            BigDecimal.ZERO
        );
  }

  @Test
  void saveChanceOfSuccess_whenPopulatedPercentages_totalsAreCalculated() {
    var form = ChanceOfSuccessTestUtil.createChanceOfSuccessForm();

    var playTotal =
        ((Float.parseFloat(form.getPlayChanceOfSuccess().getSourcePercentage().getDecimalInputValue()) / 100)
            * (Float.parseFloat(form.getPlayChanceOfSuccess().getReservoirPercentage().getDecimalInputValue()) / 100)
            * (Float.parseFloat(form.getPlayChanceOfSuccess().getTopSealPercentage().getDecimalInputValue()) / 100))
            * 100;

    var prospectSpecificTotal =
        ((Float.parseFloat(form.getProspectSpecificChanceOfSuccess().getTrapPresence().getDecimalInputValue()) / 100)
            * (Float.parseFloat(form.getProspectSpecificChanceOfSuccess().getTrapEffectiveness().getDecimalInputValue()) / 100)
            * (Float.parseFloat(form.getProspectSpecificChanceOfSuccess().getSourcePresence().getDecimalInputValue()) / 100)
            * (Float.parseFloat(form.getProspectSpecificChanceOfSuccess().getSourceEffectiveness().getDecimalInputValue()) / 100)
            * (Float.parseFloat(form.getProspectSpecificChanceOfSuccess().getReservoirPresence().getDecimalInputValue()) / 100)
            * (Float.parseFloat(form.getProspectSpecificChanceOfSuccess().getReservoirEffectiveness().getDecimalInputValue()) / 100))
            * 100;

    var overallTotal = ((playTotal / 100) * (prospectSpecificTotal / 100)) * 100;

    var chanceOfSuccessArgumentCaptor = ArgumentCaptor.forClass(ChanceOfSuccess.class);

    chanceOfSuccessPersistenceService.saveChanceOfSuccess(form, submissionDetail);

    verify(chanceOfSuccessRepository, times(1)).save(chanceOfSuccessArgumentCaptor.capture());
    var savedChanceOfSuccess = chanceOfSuccessArgumentCaptor.getValue();

    assertThat(savedChanceOfSuccess)
        .extracting(
            ChanceOfSuccess::getPlayTotal,
            ChanceOfSuccess::getProspectSpecificTotal,
            ChanceOfSuccess::getOverallTotal
        )
        .containsExactly(
            BigDecimal.valueOf(playTotal).setScale(2, RoundingMode.HALF_EVEN),
            BigDecimal.valueOf(prospectSpecificTotal).setScale(2, RoundingMode.HALF_EVEN),
            BigDecimal.valueOf(overallTotal).setScale(2, RoundingMode.HALF_EVEN)
        );
  }

  @Test
  void saveChanceOfSuccess_whenGeophysicallyModifiedAndSpecificWellLocationAreFalse() {
    var form = ChanceOfSuccessTestUtil.createChanceOfSuccessForm();

    form.setGeophysicallyModified(false);
    form.setRelatedToSpecificWellLocation(false);

    var chanceOfSuccessArgumentCaptor = ArgumentCaptor.forClass(ChanceOfSuccess.class);

    chanceOfSuccessPersistenceService.saveChanceOfSuccess(form, submissionDetail);

    verify(chanceOfSuccessRepository, times(1)).save(chanceOfSuccessArgumentCaptor.capture());
    var savedChanceOfSuccess = (ChanceOfSuccess) chanceOfSuccessArgumentCaptor.getValue();

    assertThat(savedChanceOfSuccess)
        .extracting(
            ChanceOfSuccess::getGeophysicallyModified,
            ChanceOfSuccess::getGeophysicallyModifiedPercentage,
            ChanceOfSuccess::getGeophysicallyModifiedComments,
            ChanceOfSuccess::getRelatedToSpecificWellLocation,
            ChanceOfSuccess::getWellModifiedPercentage,
            ChanceOfSuccess::getWellModifiedComments
        )
        .containsExactly(
            form.getGeophysicallyModified(),
            null,
            null,
            form.getRelatedToSpecificWellLocation(),
            null,
            null
        );
  }
}
package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class ChanceOfSuccessPersistenceService {

  private final ChanceOfSuccessRepository chanceOfSuccessRepository;

  @Autowired
  public ChanceOfSuccessPersistenceService(ChanceOfSuccessRepository chanceOfSuccessRepository) {
    this.chanceOfSuccessRepository = chanceOfSuccessRepository;
  }

  @Transactional
  public void saveChanceOfSuccess(ChanceOfSuccessForm chanceOfSuccessForm, SubmissionDetail submissionDetail) {
    var chanceOfSuccess = getChanceOfSuccess(submissionDetail).orElse(new ChanceOfSuccess());
    chanceOfSuccess.setSubmissionDetail(submissionDetail);

    var chanceOfSuccessDetailForm = chanceOfSuccessForm.getPlayChanceOfSuccess();

    var playSourcePercentage = chanceOfSuccessDetailForm.getSourcePercentage();
    var playReservoirPercentage = chanceOfSuccessDetailForm.getReservoirPercentage();
    var playTopSealPercentage = chanceOfSuccessDetailForm.getTopSealPercentage();

    chanceOfSuccess.setSourcePercentage(playSourcePercentage.createBigDecimalOrNull());
    chanceOfSuccess.setSourceComments(chanceOfSuccessDetailForm.getSourceComments());
    chanceOfSuccess.setReservoirPercentage(playReservoirPercentage.createBigDecimalOrNull());
    chanceOfSuccess.setReservoirComments(chanceOfSuccessDetailForm.getReservoirComments());
    chanceOfSuccess.setTopSealPercentage(playTopSealPercentage.createBigDecimalOrNull());
    chanceOfSuccess.setTopSealComments(chanceOfSuccessDetailForm.getTopSealComments());

    float playTotal = 0;
    if (ObjectUtils.anyNull(
        playSourcePercentage.getDecimalInputValue(),
        playReservoirPercentage.getDecimalInputValue(),
        playTopSealPercentage.getDecimalInputValue())) {
      chanceOfSuccess.setPlayTotal(BigDecimal.ZERO);
    } else {
      playTotal =
          ((Float.parseFloat(playSourcePercentage.getDecimalInputValue()) / 100)
              * (Float.parseFloat(playReservoirPercentage.getDecimalInputValue()) / 100)
              * (Float.parseFloat(playTopSealPercentage.getDecimalInputValue()) / 100))
              * 100;
      chanceOfSuccess.setPlayTotal(BigDecimal.valueOf(playTotal).setScale(2, RoundingMode.HALF_EVEN));
    }

    var prospectSpecificChanceOfSuccessForm = chanceOfSuccessForm.getProspectSpecificChanceOfSuccess();

    var prospectTrapPresence = prospectSpecificChanceOfSuccessForm.getTrapPresence();
    var prospectTrapEffectiveness = prospectSpecificChanceOfSuccessForm.getTrapEffectiveness();
    var prospectSourcePresence = prospectSpecificChanceOfSuccessForm.getSourcePresence();
    var prospectSourceEffectiveness = prospectSpecificChanceOfSuccessForm.getSourceEffectiveness();
    var prospectReservoirPresence = prospectSpecificChanceOfSuccessForm.getReservoirPresence();
    var prospectReservoirEffectiveness = prospectSpecificChanceOfSuccessForm.getReservoirEffectiveness();

    // Set prospect specific properties
    chanceOfSuccess.setProspectTrapPresence(prospectTrapPresence.createBigDecimalOrNull());
    chanceOfSuccess.setProspectTrapPresenceComments(prospectSpecificChanceOfSuccessForm.getTrapPresenceComments());
    chanceOfSuccess.setProspectTrapEffectiveness(prospectTrapEffectiveness.createBigDecimalOrNull());
    chanceOfSuccess.setProspectTrapEffectivenessComments(
        prospectSpecificChanceOfSuccessForm.getTrapEffectivenessComments()
    );
    chanceOfSuccess.setProspectSourcePresence(prospectSourcePresence.createBigDecimalOrNull());
    chanceOfSuccess.setProspectSourcePresenceComments(prospectSpecificChanceOfSuccessForm.getSourcePresenceComments());
    chanceOfSuccess.setProspectSourceEffectiveness(prospectSourceEffectiveness.createBigDecimalOrNull());
    chanceOfSuccess.setProspectSourceEffectivenessComments(
        prospectSpecificChanceOfSuccessForm.getSourceEffectivenessComments()
    );
    chanceOfSuccess.setProspectReservoirPresence(prospectReservoirPresence.createBigDecimalOrNull());
    chanceOfSuccess.setProspectReservoirPresenceComments(
        prospectSpecificChanceOfSuccessForm.getReservoirPresenceComments()
    );
    chanceOfSuccess.setProspectReservoirEffectiveness(prospectReservoirEffectiveness.createBigDecimalOrNull());
    chanceOfSuccess.setProspectReservoirEffectivenessComments(
        prospectSpecificChanceOfSuccessForm.getReservoirEffectivenessComments()
    );

    float prospectSpecificTotal = 0;
    if (ObjectUtils.anyNull(
        prospectTrapPresence.getDecimalInputValue(),
        prospectTrapEffectiveness.getDecimalInputValue(),
        prospectSourcePresence.getDecimalInputValue(),
        prospectSourceEffectiveness.getDecimalInputValue(),
        prospectReservoirPresence.getDecimalInputValue(),
        prospectReservoirEffectiveness.getDecimalInputValue())) {
      chanceOfSuccess.setProspectSpecificTotal(BigDecimal.ZERO);
    } else {
      prospectSpecificTotal =
          ((Float.parseFloat(prospectTrapPresence.getDecimalInputValue()) / 100)
              * (Float.parseFloat(prospectTrapEffectiveness.getDecimalInputValue()) / 100)
              * (Float.parseFloat(prospectSourcePresence.getDecimalInputValue()) / 100)
              * (Float.parseFloat(prospectSourceEffectiveness.getDecimalInputValue()) / 100)
              * (Float.parseFloat(prospectReservoirPresence.getDecimalInputValue()) / 100)
              * (Float.parseFloat(prospectReservoirEffectiveness.getDecimalInputValue()) / 100))
              * 100;
      chanceOfSuccess.setProspectSpecificTotal(
          BigDecimal.valueOf(prospectSpecificTotal).setScale(2, RoundingMode.HALF_EVEN)
      );
    }

    if (playTotal == 0 || prospectSpecificTotal == 0) {
      chanceOfSuccess.setOverallTotal(BigDecimal.ZERO);
    } else {
      var overallTotal = ((playTotal / 100) * (prospectSpecificTotal / 100)) * 100;
      chanceOfSuccess.setOverallTotal(
          BigDecimal.valueOf(overallTotal).setScale(2, RoundingMode.HALF_EVEN)
      );
    }
    chanceOfSuccess.setGeophysicallyModified(chanceOfSuccessForm.getGeophysicallyModified());
    chanceOfSuccess.setRelatedToSpecificWellLocation(chanceOfSuccessForm.getRelatedToSpecificWellLocation());

    if (chanceOfSuccessForm.getGeophysicallyModified() != null && chanceOfSuccessForm.getGeophysicallyModified()) {
      chanceOfSuccess.setGeophysicallyModifiedPercentage(
          chanceOfSuccessForm.getGeophysicallyModifiedPercentage().createBigDecimalOrNull()
      );
      chanceOfSuccess.setGeophysicallyModifiedComments(chanceOfSuccessForm.getGeophysicallyModifiedComments());
    } else {
      chanceOfSuccess.setGeophysicallyModifiedPercentage(null);
      chanceOfSuccess.setGeophysicallyModifiedComments(null);
    }

    if (chanceOfSuccessForm.getRelatedToSpecificWellLocation() != null
        && chanceOfSuccessForm.getRelatedToSpecificWellLocation()) {
      chanceOfSuccess.setWellModifiedPercentage(chanceOfSuccessForm.getWellModifiedPercentage().createBigDecimalOrNull());
      chanceOfSuccess.setWellModifiedComments(chanceOfSuccessForm.getWellModifiedComments());
    } else {
      chanceOfSuccess.setWellModifiedPercentage(null);
      chanceOfSuccess.setWellModifiedComments(null);
    }

    chanceOfSuccessRepository.save(chanceOfSuccess);
  }

  Optional<ChanceOfSuccess> getChanceOfSuccess(SubmissionDetail submissionDetail) {
    return chanceOfSuccessRepository.findBySubmissionDetail(submissionDetail);
  }
}

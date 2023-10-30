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
    var prospectSpecificDetailsForm = prospectSpecificChanceOfSuccessForm.getChanceOfSuccessDetailsForm();

    var prospectSourcePercentage = prospectSpecificDetailsForm.getSourcePercentage();
    var prospectReservoirPercentage = prospectSpecificDetailsForm.getReservoirPercentage();
    var prospectTopSealPercentage = prospectSpecificDetailsForm.getTopSealPercentage();
    var prospectTrapPercentage = prospectSpecificChanceOfSuccessForm.getTrapPercentage();
    var prospectMigrationPercentage = prospectSpecificChanceOfSuccessForm.getMigrationPercentage();

    // Set prospect specific properties
    chanceOfSuccess.setProspectSourcePercentage(prospectSourcePercentage.createBigDecimalOrNull());
    chanceOfSuccess.setProspectSourceComments(prospectSpecificDetailsForm.getSourceComments());
    chanceOfSuccess.setProspectReservoirPercentage(prospectReservoirPercentage.createBigDecimalOrNull());
    chanceOfSuccess.setProspectReservoirComments(prospectSpecificDetailsForm.getReservoirComments());
    chanceOfSuccess.setProspectTopSealPercentage(prospectTopSealPercentage.createBigDecimalOrNull());
    chanceOfSuccess.setProspectTopSealComments(prospectSpecificDetailsForm.getTopSealComments());
    chanceOfSuccess.setProspectTrapPercentage(prospectTrapPercentage.createBigDecimalOrNull());
    chanceOfSuccess.setProspectTrapComments(prospectSpecificChanceOfSuccessForm.getTrapComments());
    chanceOfSuccess.setProspectMigrationPercentage(prospectMigrationPercentage.createBigDecimalOrNull());
    chanceOfSuccess.setProspectMigrationComments(prospectSpecificChanceOfSuccessForm.getMigrationComments());

    float prospectSpecificTotal = 0;
    if (ObjectUtils.anyNull(
        prospectSourcePercentage.getDecimalInputValue(),
        prospectReservoirPercentage.getDecimalInputValue(),
        prospectTopSealPercentage.getDecimalInputValue(),
        prospectTrapPercentage.getDecimalInputValue(),
        prospectMigrationPercentage.getDecimalInputValue())) {
      chanceOfSuccess.setProspectSpecificTotal(BigDecimal.ZERO);
    } else {
      prospectSpecificTotal =
          ((Float.parseFloat(prospectSourcePercentage.getDecimalInputValue()) / 100)
              * (Float.parseFloat(prospectReservoirPercentage.getDecimalInputValue()) / 100)
              * (Float.parseFloat(prospectTopSealPercentage.getDecimalInputValue()) / 100)
              * (Float.parseFloat(prospectTrapPercentage.getDecimalInputValue()) / 100)
              * (Float.parseFloat(prospectMigrationPercentage.getDecimalInputValue()) / 100))
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

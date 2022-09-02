package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.chanceofsuccess;

import java.util.Optional;
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

    chanceOfSuccess.setSourcePercentage(
        chanceOfSuccessDetailForm.getSourcePercentage().createBigDecimalOrNull()
    );
    chanceOfSuccess.setSourceComments(chanceOfSuccessDetailForm.getSourceComments());
    chanceOfSuccess.setTopSealPercentage(
        chanceOfSuccessDetailForm.getTopSealPercentage().createBigDecimalOrNull()
    );
    chanceOfSuccess.setTopSealComments(chanceOfSuccessDetailForm.getTopSealComments());
    chanceOfSuccess.setReservoirPercentage(
        chanceOfSuccessDetailForm.getReservoirPercentage().createBigDecimalOrNull()
    );
    chanceOfSuccess.setReservoirComments(chanceOfSuccessDetailForm.getReservoirComments());

    var prospectSpecificChanceOfSuccessForm = chanceOfSuccessForm.getProspectSpecificChanceOfSuccess();
    var prospectSpecificDetailsForm = prospectSpecificChanceOfSuccessForm.getChanceOfSuccessDetailsForm();
    // Set prospect specific properties
    chanceOfSuccess.setProspectSourcePercentage(
        prospectSpecificDetailsForm
            .getSourcePercentage()
            .createBigDecimalOrNull()
    );
    chanceOfSuccess.setProspectSourceComments(prospectSpecificDetailsForm.getSourceComments());

    chanceOfSuccess.setProspectReservoirPercentage(
        prospectSpecificDetailsForm
            .getReservoirPercentage()
            .createBigDecimalOrNull()
    );
    chanceOfSuccess.setProspectReservoirComments(prospectSpecificDetailsForm.getReservoirComments());

    chanceOfSuccess.setProspectTopSealPercentage(
        prospectSpecificDetailsForm
            .getTopSealPercentage()
            .createBigDecimalOrNull()
    );
    chanceOfSuccess.setProspectTopSealComments(prospectSpecificDetailsForm.getTopSealComments());

    chanceOfSuccess.setProspectTrapPercentage(
        prospectSpecificChanceOfSuccessForm
            .getTrapPercentage()
            .createBigDecimalOrNull()
    );
    chanceOfSuccess.setProspectTrapComments(prospectSpecificChanceOfSuccessForm.getTrapComments());
    chanceOfSuccess.setProspectMigrationPercentage(
        prospectSpecificChanceOfSuccessForm
            .getMigrationPercentage()
            .createBigDecimalOrNull()
    );
    chanceOfSuccess.setProspectMigrationComments(prospectSpecificChanceOfSuccessForm.getMigrationComments());

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

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

    chanceOfSuccess.setPlayChanceOfSuccess(chanceOfSuccessForm.getPlayChanceOfSuccess());
    chanceOfSuccess.setProspectSpecificChanceOfSuccess(chanceOfSuccessForm.getProspectSpecificChanceOfSuccess());

    chanceOfSuccess.setGeophysicallyModified(chanceOfSuccessForm.getGeophysicallyModified());
    chanceOfSuccess.setRelatedToSpecificWellLocation(chanceOfSuccessForm.getRelatedToSpecificWellLocation());

    if (chanceOfSuccessForm.getGeophysicallyModified() != null && chanceOfSuccessForm.getGeophysicallyModified()) {
      chanceOfSuccess.setGeophysicallyModifiedPercentage(
          chanceOfSuccessForm.getGeophysicallyModifiedPercentage().getDecimalInputValue());
      chanceOfSuccess.setGeophysicallyModifiedComments(chanceOfSuccessForm.getGeophysicallyModifiedComments());
    }

    if (chanceOfSuccessForm.getRelatedToSpecificWellLocation() != null
        && chanceOfSuccessForm.getRelatedToSpecificWellLocation()) {

      chanceOfSuccess.setWellModifiedPercentage(chanceOfSuccessForm.getWellModifiedPercentage().getDecimalInputValue());
      chanceOfSuccess.setWellModifiedComments(chanceOfSuccessForm.getWellModifiedComments());
    }

    chanceOfSuccessRepository.save(chanceOfSuccess);
  }

  Optional<ChanceOfSuccess> getChanceOfSuccess(SubmissionDetail submissionDetail) {
    return chanceOfSuccessRepository.findBySubmissionDetail(submissionDetail);
  }
}

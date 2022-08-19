package uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.sourceandseal;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.nstauthority.hydrocarbonmaturation.submissiondetails.SubmissionDetail;

@Service
class SourceAndSealPersistenceService {

  private final SourceAndSealRepository sourceAndSealRepository;

  @Autowired
  public SourceAndSealPersistenceService(SourceAndSealRepository sourceAndSealRepository) {
    this.sourceAndSealRepository = sourceAndSealRepository;
  }

  @Transactional
  public void saveSourceAndSealDetails(SourceAndSealForm form, SubmissionDetail submissionDetail) {

    var sourceAndSeal = getSourceAndSeal(submissionDetail).orElse(new SourceAndSeal());
    sourceAndSeal.setSubmissionDetail(submissionDetail);
    sourceAndSeal.setSourceFormationName(form.getSourceFormationName());
    sourceAndSeal.setSourceAgeStage(form.getSourceAgeStage());
    sourceAndSeal.setSourceLithology(form.getSourceLithology());
    sourceAndSeal.setTopSealFormationName(form.getTopSealFormationName());
    sourceAndSeal.setTopSealAgeStage(form.getTopSealAgeStage());
    sourceAndSeal.setTopSealLithology(form.getTopSealLithology());

    sourceAndSealRepository.save(sourceAndSeal);
  }

  Optional<SourceAndSeal> getSourceAndSeal(SubmissionDetail submissionDetail) {
    return sourceAndSealRepository.findBySubmissionDetail(submissionDetail);
  }
}

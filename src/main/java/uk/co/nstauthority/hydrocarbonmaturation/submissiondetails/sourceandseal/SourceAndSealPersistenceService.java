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

    var sourceAndSeal = getSourceAndSeal(submissionDetail).orElse(new SourceAndSeal())
        .setSubmissionDetail(submissionDetail)
        .setSourceFormationName(form.getSourceFormationName())
        .setSourceAgeStage(form.getSourceAgeStage())
        .setSourceLithology(form.getSourceLithology())
        .setTopSealFormationName(form.getTopSealFormationName())
        .setTopSealAgeStage(form.getTopSealAgeStage())
        .setTopSealLithology(form.getTopSealLithology());

    sourceAndSealRepository.save(sourceAndSeal);
  }

  Optional<SourceAndSeal> getSourceAndSeal(SubmissionDetail submissionDetail) {
    return sourceAndSealRepository.findBySubmissionDetail(submissionDetail);
  }
}
